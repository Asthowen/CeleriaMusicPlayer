use crate::util::utils::{read_json_file, save_json_to_file};
use serde::{Deserialize, Serialize};
use std::path::{Path, PathBuf};
use std::sync::Arc;
use tokio::sync::Mutex;

#[derive(Clone)]
pub struct ConfigManagerStruct(pub Arc<Mutex<ConfigManager>>);

fn default_to_true() -> bool {
    true
}

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct ConfigWindow {
    #[serde(default = "default_to_true")]
    pub custom_titlebar: bool,
    #[serde(default = "default_to_true")]
    pub keep_running_background: bool,
}

impl Default for ConfigWindow {
    fn default() -> Self {
        Self {
            custom_titlebar: true,
            keep_running_background: true,
        }
    }
}

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct ConfigLibrary {
    #[serde(default)]
    pub paths: Vec<String>,
    #[serde(default = "default_to_true")]
    pub show_playlists: bool,
}

impl Default for ConfigLibrary {
    fn default() -> Self {
        let audio_dir_option: Option<PathBuf> = dirs::audio_dir();
        let librairies_path: Vec<String> = if let Some(audio_dir) = audio_dir_option {
            vec![audio_dir.to_str().unwrap().to_owned()]
        } else {
            Vec::new()
        };

        Self {
            paths: librairies_path,
            show_playlists: true,
        }
    }
}

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct ConfigRepresentation {
    #[serde(default)]
    pub language: String,
    #[serde(default)]
    pub library: ConfigLibrary,
    #[serde(default)]
    pub window: ConfigWindow,
}

impl Default for ConfigRepresentation {
    fn default() -> Self {
        Self {
            language: "auto".to_owned(),
            library: Default::default(),
            window: Default::default(),
        }
    }
}

pub struct ConfigManager {
    config: ConfigRepresentation,
    config_path: PathBuf,
}

impl ConfigManager {
    pub fn init<P: AsRef<Path>>(config_path: P, file_name: &str) -> Self {
        let config_path: PathBuf = config_path.as_ref().to_owned();
        std::fs::create_dir_all(&config_path).unwrap();
        let file_path: PathBuf = config_path.join(file_name);

        let config: ConfigRepresentation = if file_path.exists() {
            let config = read_json_file(&file_path);
            save_json_to_file(&config, &file_path);
            config
        } else {
            let config: ConfigRepresentation = ConfigRepresentation::default();
            save_json_to_file(&config, &file_path);
            config
        };

        Self {
            config,
            config_path: file_path,
        }
    }

    pub fn reload_config(&mut self) -> ConfigRepresentation {
        self.config = if self.config_path.exists() {
            let config = read_json_file(&self.config_path);
            save_json_to_file(&config, &self.config_path);
            config
        } else {
            let config: ConfigRepresentation = ConfigRepresentation::default();
            save_json_to_file(&config, &self.config_path);
            config
        };
        self.config.clone()
    }

    pub fn get_config(&self) -> ConfigRepresentation {
        self.config.clone()
    }

    pub fn get_config_mut(&mut self) -> &mut ConfigRepresentation {
        &mut self.config
    }

    pub fn save_config(&self) {
        save_json_to_file(&self.config, &self.config_path);
    }
}
