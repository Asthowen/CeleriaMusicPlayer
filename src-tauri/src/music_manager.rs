use crate::util::utils::unix_time;
use audiotags::Tag;
use kira::manager::{
    backend::cpal::CpalBackend, AudioManager, AudioManagerSettings, MainPlaybackState,
};
use kira::sound::static_sound::{StaticSoundData, StaticSoundHandle, StaticSoundSettings};
use kira::tween::Tween;
use serde::Serialize;
use std::sync::Arc;
use std::time::Duration;
use tokio::sync::{Mutex, RwLock};

pub struct MusicManagerStruct(pub Arc<Mutex<MusicManager>>);

#[derive(Clone, Debug, Serialize)]
pub struct MusicElement {
    duration: Duration,
    started_at: Duration,
    file_infos: Option<MusicFileInfos>,
}

#[derive(Clone, Debug, Serialize)]
pub struct MusicFileInfos {
    title: Option<String>,
    cover: Option<String>,
    artist: Option<String>,
    album: Option<String>,
}

#[derive(Clone, Debug, Serialize)]
pub struct MusicElementComplete {
    duration: Duration,
    started_at: Duration,
    progress: Duration,
    remain_time: Duration,
    file_infos: Option<MusicFileInfos>,
}

pub struct MusicManager {
    manager: Arc<Mutex<AudioManager>>,
    musics_elements: Arc<RwLock<Vec<MusicElement>>>,
    musics_queue: Arc<RwLock<Vec<String>>>,
    current_sound: Arc<Mutex<Option<StaticSoundHandle>>>,
    pause_data: Arc<Mutex<Duration>>,
}

fn get_file_infos(file_path: &str) -> Option<MusicFileInfos> {
    let tag_result = Tag::new().read_from_path(file_path);

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
            } else {
                None
            },
            album: if let Some(album_title) = tag.album_title() {
                Option::from(album_title.to_owned())
            } else {
                None
            },
        });
    }
    None
}

impl MusicManager {
    pub fn init() -> Self {
        let manager: AudioManager =
            AudioManager::<CpalBackend>::new(AudioManagerSettings::default()).unwrap();

        Self {
            manager: Arc::new(Mutex::new(manager)),
            musics_elements: Arc::new(RwLock::new(Vec::new())),
            musics_queue: Arc::new(Default::default()),
            current_sound: Arc::new(Default::default()),
            pause_data: Arc::new(Default::default()),
        }
    }

    pub async fn start_queue_manager(&mut self) {
        let musics_elements = self.musics_elements.clone();
        let manager = self.manager.clone();
        let musics_queue = self.musics_queue.clone();
        let current_sound = self.current_sound.clone();

        tokio::spawn(async move {
            loop {
                let music_element = musics_elements.read().await;

                if music_element.len() > 0
                    && manager.lock().await.state() == MainPlaybackState::Playing
                {
                    let time: Duration = unix_time();
                    if music_element[0].started_at.as_secs() + music_element[0].duration.as_secs()
                        <= time.as_secs()
                    {
                        drop(music_element);
                        musics_elements.clone().write().await.remove(0);

                        let mut music_queue = musics_queue.write().await;
                        if music_queue.len() != 0 {
                            let sound_data_result = StaticSoundData::from_file(
                                music_queue[0].clone(),
                                StaticSoundSettings::new(),
                            );
                            if let Ok(sound_data) = sound_data_result {
                                let music_element: MusicElement = MusicElement {
                                    duration: sound_data.duration(),
                                    started_at: unix_time(),
                                    file_infos: get_file_infos(music_queue[0].as_str()),
                                };
                                musics_elements.clone().write().await.push(music_element);
                                let sound: StaticSoundHandle =
                                    manager.lock().await.play(sound_data).unwrap();
                                *current_sound.lock().await = Option::from(sound);
                            }
                            music_queue.remove(0);
                        }
                        drop(music_queue);
                    }
                }

                tokio::time::sleep(Duration::from_millis(250)).await;
            }
        });
    }

    pub async fn append_file_to_queue(&mut self, file_path: &str) -> Result<(), ()> {
        if self.musics_elements.read().await.len() == 0 {
            let sound_data_result =
                StaticSoundData::from_file(file_path, StaticSoundSettings::new());
            if let Ok(sound_data) = sound_data_result {
                let music_element: MusicElement = MusicElement {
                    duration: sound_data.duration(),
                    started_at: unix_time(),
                    file_infos: get_file_infos(file_path),
                };
                self.musics_elements.write().await.push(music_element);
                self.manager.lock().await.play(sound_data).unwrap();
            } else {
                return Err(());
            }
        } else {
            self.musics_queue.write().await.push(file_path.to_owned());
        }
        Ok(())
    }

    pub async fn resume(&mut self) {
        if self.player_state().await != MainPlaybackState::Playing {
            self.manager.lock().await.resume(Tween::default()).unwrap();
            let pause_data = self.pause_data.lock().await;
            let mut music_queue = self.musics_elements.write().await;
            music_queue[0].started_at = music_queue[0].started_at + (unix_time() - *pause_data);
            drop(music_queue);
            drop(pause_data);
        }
    }

    pub async fn pause(&mut self) {
        if self.player_state().await == MainPlaybackState::Playing {
            self.manager.lock().await.pause(Tween::default()).unwrap();
            *self.pause_data.lock().await = unix_time();
        }
    }

    pub async fn get_current_track_basic(&mut self) -> Option<MusicElement> {
        self.musics_elements.read().await.get(0).cloned()
    }

    pub async fn get_current_track_complete(&self) -> Option<MusicElementComplete> {
        if let Some(music_element) = self.musics_elements.read().await.get(0).cloned() {
            let time: Duration = unix_time();
            let music_element_complete: MusicElementComplete = MusicElementComplete {
                duration: music_element.duration,
                started_at: music_element.started_at,
                progress: Duration::from_millis(
                    (time.as_millis() - music_element.started_at.as_millis()) as u64,
                ),
                remain_time: Duration::from_millis(
                    ((music_element.started_at.as_millis() + music_element.duration.as_millis())
                        - time.as_millis()) as u64,
                ),
                file_infos: music_element.file_infos,
            };
            return Option::from(music_element_complete);
        }
        None
    }

    pub async fn player_state(&mut self) -> MainPlaybackState {
        self.manager.lock().await.state()
    }
}
