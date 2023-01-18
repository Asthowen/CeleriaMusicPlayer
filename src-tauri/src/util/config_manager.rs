use crate::util::utils::{read_json_file, save_json_to_file};
use serde::{Deserialize, Serialize};
use std::path::{Path, PathBuf};
use std::sync::Arc;
use tokio::sync::Mutex;

#[derive(Clone)]
pub struct ConfigManagerStruct(pub Arc<Mutex<ConfigManager>>);

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct ConfigRepresentation {
    pub language: String,
    pub librairies_path: Vec<String>,
}

impl Default for ConfigRepresentation {
    fn default() -> Self {
        let audio_dir_option: Option<PathBuf> = dirs::audio_dir();
        let librairies_path: Vec<String> = if let Some(audio_dir) = audio_dir_option {
            vec![audio_dir.to_str().unwrap().to_owned()]
        } else {
            Vec::new()
        };

        Self {
            language: "auto".to_owned(),
            librairies_path,
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
            read_json_file(&file_path)
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
            read_json_file(&self.config_path)
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
}
