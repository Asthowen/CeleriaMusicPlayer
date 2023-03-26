use crate::database::models::albums::Album;
use crate::database::models::tracks::Track;
use crate::database::schemas::albums::dsl as albums_dsl;
use crate::database::schemas::tracks::dsl as tracks_dsl;
use crate::database::sqlite::{get_pool, SqlitePool, SqlitePooled};
use crate::util::config_manager::ConfigManagerStruct;
use base64::{engine::general_purpose, Engine as _};
use diesel::prelude::*;
use diesel::RunQueryDsl;
use lofty::{Accessor, AudioFile, PictureType, Tag, TaggedFile, TaggedFileExt};
use serde::Serialize;
use std::collections::HashMap;
use std::path::{Path, PathBuf};
use std::process::exit;
use std::sync::atomic::{AtomicI64, Ordering};
use std::sync::Arc;
use tokio::sync::{Mutex, Semaphore};
use walkdir::{DirEntry, WalkDir};

#[derive(Clone)]
pub struct LibraryManagerStruct(pub Arc<Mutex<LibraryManager>>);

#[derive(Clone)]
pub struct LibraryManager {
    pool: SqlitePool,
    config_manager: ConfigManagerStruct,
}

#[derive(Clone, Debug, Serialize)]
pub struct MusicFileInfos {
    pub title: Option<String>,
    pub cover: Option<String>,
    pub artist: Option<String>,
    pub album: Option<String>,
    pub year: Option<i64>,
    pub duration: Option<i64>,
}

pub fn get_file_infos<P: AsRef<Path>>(file_path: &P) -> Option<MusicFileInfos> {
    let file_path: PathBuf = file_path.as_ref().to_owned();
    if !file_path.is_file() {
        return None;
    }

    let tagged_file: TaggedFile = if let Ok(open_file) = lofty::Probe::open(&file_path) {
        let detect_file_type_result = open_file.guess_file_type();
        if let Ok(detect_file_type) = detect_file_type_result {
            let read_file_result = detect_file_type.read();
            if let Ok(read_file) = read_file_result {
                read_file
            } else {
                return None;
            }
        } else {
            return None;
        }
    } else {
        return None;
    };

    let tags: Tag = if let Some(tags) = tagged_file.primary_tag() {
        tags.clone()
    } else if let Some(tags) = tagged_file.first_tag() {
        tags.clone()
    } else {
        return None;
    };

    let pictures = tags.pictures();
    let mut cover: Option<String> = None;
    for picture in pictures {
        if picture.pic_type() == PictureType::CoverFront {
            cover = Option::from(general_purpose::STANDARD.encode(picture.data()));
            break;
        }
    }
    if cover.is_none() {
        if let Some(picture) = pictures.iter().next() {
            cover = Option::from(general_purpose::STANDARD.encode(picture.data()));
        }
    }

    return Option::from(MusicFileInfos {
        title: if let Some(title) = tags.title() {
            Option::from(title.to_string())
        } else if let Some(file_str) = file_path.file_stem() {
            Option::from(file_str.to_str().unwrap_or("").to_owned())
        } else {
            None
        },
        cover,
        artist: if let Some(artist) = tags.artist() {
            Option::from(artist.to_string())
        } else {
            let mut album_name: Option<String> = None;
            for t in tagged_file.tags() {
                for i in t.items() {
                    if format!("{:?}", i.key()) == "AlbumArtist" {
                        if let Some(album_name_str) = i.value().text() {
                            album_name = Option::from(album_name_str.to_owned());
                            break;
                        }
                    }
                }

                if album_name.is_some() {
                    break;
                }
            }

            album_name
        },
        album: tags
            .album()
            .and_then(|album_title| Option::from(album_title.to_string())),
        year: tags.year().and_then(|year| Option::from(year as i64)),
        duration: Option::from(tagged_file.properties().duration().as_secs() as i64),
    });
}

fn is_hidden(entry: &DirEntry) -> bool {
    entry
        .file_name()
        .to_str()
        .map(|s| s.starts_with('.'))
        .unwrap_or(false)
}

async fn scan_file(
    file_path: PathBuf,
    insert_queue_albums: Arc<Mutex<Vec<Album>>>,
    insert_queue_tracks: Arc<Mutex<Vec<Track>>>,
    album_list: &[(String, String)],
    new_album_values: Arc<Mutex<HashMap<String, String>>>,
    albums_covers_dir: PathBuf,
) -> bool {
    return if let Some(file_infos) = get_file_infos(&file_path) {
        let album_uuid: Option<String> = if let Some(album_name) = file_infos.album {
            let mut new_album_values_lock = new_album_values.lock().await;
            let search_album_cache: Option<String> =
                new_album_values_lock.get(&album_name).cloned();
            let mut need_creation: bool = false;

            let album_uuid: String = if let Some(uuid) = search_album_cache {
                drop(new_album_values_lock);
                uuid.clone()
            } else if let Some((_, uuid)) = album_list.iter().find(|&(k, _)| k == &album_name) {
                drop(new_album_values_lock);
                uuid.clone()
            } else {
                need_creation = true;
                let new_uuid: String = uuid::Uuid::new_v4().to_string();
                new_album_values_lock.insert(album_name.clone(), new_uuid.clone());
                drop(new_album_values_lock);
                new_uuid
            };

            if need_creation {
                let mut has_cover: i16 = 0;

                if let Some(cover) = file_infos.cover {
                    let decode_cover_result = general_purpose::STANDARD.decode(cover);
                    if let Ok(decode_cover) = decode_cover_result {
                        std::fs::write(
                            albums_covers_dir.join(format!("{}.png", album_uuid)),
                            decode_cover,
                        )
                        .ok();
                        has_cover = 1;
                    }
                }

                insert_queue_albums.lock().await.push(Album {
                    uuid: album_uuid.clone(),
                    name: album_name,
                    artist: file_infos.artist,
                    year: file_infos.year,
                    cover: has_cover,
                });
            }

            Option::from(album_uuid)
        } else {
            None
        };

        insert_queue_tracks.lock().await.push(Track {
            uuid: uuid::Uuid::new_v4().to_string(),
            title: file_infos.title,
            album: album_uuid,
            duration: file_infos.duration.unwrap_or(0),
            file_path: file_path.to_str().unwrap().to_owned(),
        });

        true
    } else {
        false
    };
}

impl LibraryManager {
    pub fn init<P: AsRef<Path>>(database_path: P, config_manager: ConfigManagerStruct) -> Self {
        let database_path: PathBuf = database_path.as_ref().to_owned().join("library.db");
        let pool: SqlitePool = get_pool(database_path);

        Self {
            pool,
            config_manager,
        }
    }

    pub async fn scan_all(&self) -> i64 {
        let libraries: Vec<String> = self
            .config_manager
            .0
            .lock()
            .await
            .get_config()
            .library
            .paths;
        let added_counter: Arc<AtomicI64> = Arc::from(AtomicI64::new(0));

        // Retrieval and creation (if needed) of the folder to store the album covers.
        let albums_covers_dir: PathBuf = dirs::data_dir()
            .unwrap()
            .join("celeria")
            .join("cover")
            .join("albums");
        std::fs::create_dir_all(&albums_covers_dir).ok();

        // Creation of two vectors for caching album and track data to be inserted into the database.
        let insert_queue_albums: Arc<Mutex<Vec<Album>>> = Arc::new(Mutex::new(Vec::new()));
        let insert_queue_tracks: Arc<Mutex<Vec<Track>>> = Arc::new(Mutex::new(Vec::new()));

        let mut pool: SqlitePooled = self.pool.get().unwrap_or_else(|e| {
            log::error!(
                "An error occurred while acquiring a connection to the database pool: {}",
                e.to_string()
            );
            exit(9);
        });
        let albums_list: Vec<(String, String)> = albums_dsl::albums
            .select((albums_dsl::name, albums_dsl::uuid))
            .load::<(String, String)>(&mut pool)
            .unwrap_or_default();
        let tracks_path_list: Vec<String> = tracks_dsl::tracks
            .select(tracks_dsl::file_path)
            .load::<String>(&mut pool)
            .unwrap_or_default();

        // A hashmap that allows you to associate a UUID with the name of an album as it is associated.
        let new_album_values: Arc<Mutex<HashMap<String, String>>> =
            Arc::from(Mutex::from(HashMap::new()));

        let semaphore: Arc<Semaphore> = Arc::new(Semaphore::new(15));
        let mut join_handles = Vec::new();

        for library in libraries {
            for entry in WalkDir::new(library)
                .follow_links(true)
                .into_iter()
                .filter_entry(|e| !is_hidden(e))
                .filter_map(|e| e.ok())
            {
                let path: PathBuf = entry.path().to_owned();
                if tracks_path_list.contains(&path.to_str().unwrap_or_default().to_owned()) {
                    continue;
                }

                let insert_queue_albums = insert_queue_albums.clone();
                let insert_queue_tracks = insert_queue_tracks.clone();
                let albums_list = albums_list.clone();
                let new_album_values = new_album_values.clone();
                let albums_covers_dir = albums_covers_dir.clone();
                let added_counter = added_counter.clone();

                let semaphore = Arc::clone(&semaphore);
                let permit = semaphore.clone().acquire_owned().await.unwrap();
                join_handles.push(tokio::spawn(async move {
                    let has_added_track: bool = scan_file(
                        path,
                        insert_queue_albums,
                        insert_queue_tracks,
                        &albums_list,
                        new_album_values,
                        albums_covers_dir,
                    )
                    .await;

                    if has_added_track {
                        added_counter.fetch_add(1, Ordering::SeqCst);
                    }

                    drop(permit);
                }));
            }
        }

        for handle in join_handles {
            handle.await.unwrap();
        }

        // Insert new data in database
        let insert_albums_values: Vec<Album> = insert_queue_albums.lock().await.clone();
        diesel::insert_into(albums_dsl::albums)
            .values(&insert_albums_values)
            .execute(&mut pool)
            .ok();
        let insert_tracks_values: Vec<Track> = insert_queue_tracks.lock().await.clone();
        diesel::insert_into(tracks_dsl::tracks)
            .values(&insert_tracks_values)
            .execute(&mut pool)
            .ok();

        added_counter.load(Ordering::SeqCst)
    }

    pub fn get_pool(&self) -> SqlitePool {
        self.pool.clone()
    }

    pub fn get_all_albums(&self) -> Vec<Album> {
        let mut pool: SqlitePooled = self.pool.get().unwrap_or_else(|e| {
            log::error!(
                "An error occurred while acquiring a connection to the database pool: {}",
                e.to_string()
            );
            exit(9);
        });

        albums_dsl::albums
            .order(albums_dsl::name.asc())
            .load::<Album>(&mut pool)
            .unwrap_or_default()
    }

    pub fn get_all_tracks(&self) -> Vec<Track> {
        let mut pool: SqlitePooled = self.pool.get().unwrap_or_else(|e| {
            log::error!(
                "An error occurred while acquiring a connection to the database pool: {}",
                e.to_string()
            );
            exit(9);
        });

        tracks_dsl::tracks
            .order(tracks_dsl::title.asc())
            .load::<Track>(&mut pool)
            .unwrap_or_default()
    }

    pub fn get_album_by_uuid(&self, album_uuid: &str) -> Option<(Album, Vec<Track>)> {
        let mut pool: SqlitePooled = self.pool.get().unwrap_or_else(|e| {
            log::error!(
                "An error occurred while acquiring a connection to the database pool: {}",
                e.to_string()
            );
            exit(9);
        });

        let album: Album = albums_dsl::albums
            .filter(albums_dsl::uuid.eq(&album_uuid))
            .first::<Album>(&mut pool)
            .optional()
            .unwrap_or_default()?;

        let tracks_list: Vec<Track> = tracks_dsl::tracks
            .filter(tracks_dsl::album.eq(&album_uuid))
            .load::<Track>(&mut pool)
            .unwrap_or_default();

        Option::from((album, tracks_list))
    }

    pub fn get_track_by_uuid(&self, track_uuid: &str) -> Option<(Track, Option<Album>)> {
        let mut pool: SqlitePooled = self.pool.get().unwrap_or_else(|e| {
            log::error!(
                "An error occurred while acquiring a connection to the database pool: {}",
                e.to_string()
            );
            exit(9);
        });

        let track: Track = tracks_dsl::tracks
            .filter(tracks_dsl::uuid.eq(track_uuid))
            .first::<Track>(&mut pool)
            .optional()
            .unwrap_or_default()?;

        let album_option: Option<Album> = track.album.as_ref().and_then(|album| {
            albums_dsl::albums
                .filter(albums_dsl::uuid.eq(album))
                .first::<Album>(&mut pool)
                .optional()
                .unwrap_or_default()
        });

        Option::from((track, album_option))
    }
}
