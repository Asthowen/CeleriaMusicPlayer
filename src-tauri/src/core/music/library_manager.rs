use crate::database::models::albums::Album;
use crate::database::models::tracks::Track;
use crate::database::schemas::albums::dsl as albums_dsl;
use crate::database::schemas::tracks::dsl as tracks_dsl;
use crate::database::sqlite::{get_pool, SqlitePool, SqlitePooled};
use crate::util::config_manager::ConfigManagerStruct;
use audiotags::Tag;
use diesel::prelude::*;
use diesel::sql_types::Integer;
use diesel::RunQueryDsl;
use mpeg_audio_header::ParseMode;
use serde::Serialize;
use std::path::{Path, PathBuf};
use std::sync::Arc;
use tokio::sync::Mutex;
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

    let tag_result = Tag::new().read_from_path(&file_path);

    if let Ok(tag) = tag_result {
        return Option::from(MusicFileInfos {
            title: if let Some(title) = tag.title() {
                Option::from(title.to_owned())
            } else {
                None
            },
            cover: if let Some(cover) = tag.album_cover() {
                Option::from(base64::encode(cover.data))
            } else {
                None
            },
            artist: if let Some(artist) = tag.album_artist() {
                Option::from(artist.to_owned())
            } else if let Some(artist) = tag.artist() {
                Option::from(artist.to_owned())
            } else {
                None
            },
            album: if let Some(album_title) = tag.album_title() {
                Option::from(album_title.to_owned())
            } else {
                None
            },
            year: if let Some(year) = tag.year() {
                Option::from(year as i64)
            } else {
                None
            },
            duration: if let Some(duration) = tag.duration() {
                Option::from(duration as i64)
            } else {
                let header_result = mpeg_audio_header::Header::read_from_path(
                    file_path,
                    ParseMode::PreferVbrHeaders,
                );
                if let Ok(header) = header_result {
                    Option::from(header.total_duration.as_secs() as i64)
                } else {
                    None
                }
            },
        });
    }
    None
}

fn is_hidden(entry: &DirEntry) -> bool {
    entry
        .file_name()
        .to_str()
        .map(|s| s.starts_with('.'))
        .unwrap_or(false)
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

    pub fn scan_file<P: AsRef<Path>>(&self, file_path: P) -> bool {
        let file_path: PathBuf = file_path.as_ref().to_owned();
        if !file_path.exists() {
            return false;
        }
        let mut pool: SqlitePooled = self.pool.get().unwrap();

        let file_saved: i32 = tracks_dsl::tracks
            .select(diesel::dsl::sql::<Integer>("1"))
            .filter(tracks_dsl::file_path.eq(&file_path.to_str().unwrap()))
            .first::<i32>(&mut pool)
            .optional()
            .unwrap_or(None)
            .unwrap_or(0);

        if file_saved == 0 {
            let file_infos_option = get_file_infos(&file_path);
            return if let Some(file_infos) = file_infos_option {
                let album_uuid: Option<String> = if let Some(album_name) = file_infos.album {
                    let album_uuid_option: Option<String> = albums_dsl::albums
                        .select(albums_dsl::uuid)
                        .filter(albums_dsl::name.eq(&album_name))
                        .first::<String>(&mut pool)
                        .optional()
                        .unwrap_or(None);
                    if let Some(album_uuid) = album_uuid_option {
                        Option::from(album_uuid)
                    } else {
                        let album_uuid: String = uuid::Uuid::new_v4().to_string();
                        let mut has_cover: i16 = 0;

                        if let Some(cover) = file_infos.cover {
                            let decode_cover_result = base64::decode(cover);
                            if let Ok(decode_cover) = decode_cover_result {
                                let albums_covers_dir: PathBuf = dirs::data_dir()
                                    .unwrap()
                                    .join("celeria")
                                    .join("cover")
                                    .join("albums");
                                std::fs::create_dir_all(&albums_covers_dir).ok();
                                std::fs::write(
                                    albums_covers_dir.join(format!("{}.png", album_uuid)),
                                    decode_cover,
                                )
                                .ok();
                                has_cover = 1;
                            }
                        }

                        let album = Album {
                            uuid: album_uuid.clone(),
                            name: album_name,
                            artist: file_infos.artist,
                            year: file_infos.year,
                            cover: has_cover,
                        };

                        diesel::insert_into(albums_dsl::albums)
                            .values(album)
                            .execute(&mut pool)
                            .map_err(|err| log::error!("{:?}", err))
                            .ok();
                        Option::from(album_uuid)
                    }
                } else {
                    None
                };

                let track = Track {
                    uuid: uuid::Uuid::new_v4().to_string(),
                    title: file_infos.title,
                    album: album_uuid,
                    duration: if let Some(duration) = file_infos.duration {
                        duration
                    } else {
                        0
                    },
                    file_path: file_path.to_str().unwrap().to_owned(),
                };

                diesel::insert_into(tracks_dsl::tracks)
                    .values(track)
                    .execute(&mut pool)
                    .map_err(|err| log::error!("{:?}", err))
                    .ok();
                true
            } else {
                false
            };
        }
        false
    }

    pub async fn scan_all(&self) -> i64 {
        let libraries: Vec<String> = self
            .config_manager
            .0
            .lock()
            .await
            .get_config()
            .librairies_path;
        let mut added_counter: i64 = 0;
        for library in libraries {
            for entry in WalkDir::new(library)
                .follow_links(true)
                .into_iter()
                .filter_entry(|e| !is_hidden(e))
                .filter_map(|e| e.ok())
            {
                let scan_file: bool = self.scan_file(entry.path());
                if scan_file {
                    added_counter += 1;
                }
            }
        }
        added_counter
    }

    pub fn get_pool(&self) -> SqlitePool {
        self.pool.clone()
    }

    pub fn get_all_albums(&self) -> Vec<Album> {
        let mut pool: SqlitePooled = self.pool.get().unwrap();
        albums_dsl::albums
            .load::<Album>(&mut pool)
            .unwrap_or_default()
    }

    pub fn get_album_by_uuid(&self, album_uuid: &str) -> Option<(Album, Vec<Track>)> {
        let mut pool: SqlitePooled = self.pool.get().unwrap();

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
        let mut pool: SqlitePooled = self.pool.get().unwrap();

        let track: Track = tracks_dsl::tracks
            .filter(tracks_dsl::uuid.eq(track_uuid))
            .first::<Track>(&mut pool)
            .optional()
            .unwrap_or_default()?;

        let album_option: Option<Album> = if let Some(album) = &track.album {
            albums_dsl::albums
                .filter(albums_dsl::uuid.eq(album))
                .first::<Album>(&mut pool)
                .optional()
                .unwrap_or_default()
        } else {
            None
        };

        Option::from((track, album_option))
    }
}