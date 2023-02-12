use crate::util::config_manager::{ConfigManager, ConfigManagerStruct, ConfigRepresentation};
use serde_json::Value;
use tauri::State;
use tokio::sync::MutexGuard;

#[tauri::command]
pub async fn get_settings(
    config_manager: State<'_, ConfigManagerStruct>,
) -> Result<ConfigRepresentation, ()> {
    let mut config_manager: MutexGuard<'_, ConfigManager> = config_manager.0.lock().await;
    config_manager.reload_config();
    Ok(config_manager.get_config())
}

#[tauri::command]
pub async fn set_setting(
    value: Value,
    config_manager: State<'_, ConfigManagerStruct>,
) -> Result<Option<Value>, ()> {
    if let Some(property) = value.get("property") {
        if let Some(new_value) = value.get("value") {
            let mut config_manager: MutexGuard<'_, ConfigManager> = config_manager.0.lock().await;
            match property.as_str().unwrap_or_default() {
                "window_custom_titlebar" => {
                    if let Some(new_value) = new_value.as_bool() {
                        config_manager.get_config_mut().window.custom_titlebar = new_value;
                    }
                }
                "window_keep_running_background" => {
                    if let Some(new_value) = new_value.as_bool() {
                        config_manager
                            .get_config_mut()
                            .window
                            .keep_running_background = new_value;
                    }
                }
                "library_paths_delete" => {
                    if let Some(new_value) = new_value.as_str() {
                        let mut paths: Vec<String> = config_manager.get_config().library.paths;
                        let index_opt: Option<usize> = paths.iter().position(|r| *r == new_value);
                        if let Some(index) = index_opt {
                            paths.remove(index);
                            config_manager.get_config_mut().library.paths = paths.clone();
                            config_manager.save_config();
                        }
                    }
                }
                "library_paths_add" => {
                    if let Some(new_value) = new_value.as_str() {
                        let mut paths: Vec<String> = config_manager.get_config().library.paths;
                        paths.push(new_value.to_owned());
                        config_manager.get_config_mut().library.paths = paths.clone();
                        config_manager.save_config();

                        return Ok(Option::from(
                            serde_json::to_value(paths).unwrap_or_default(),
                        ));
                    }
                }
                "library_show_playlists" => {
                    if let Some(new_value) = new_value.as_bool() {
                        config_manager.get_config_mut().library.show_playlists = new_value;
                    }
                }
                &_ => {}
            }
            config_manager.save_config();
        }
    }
    Ok(None)
}
