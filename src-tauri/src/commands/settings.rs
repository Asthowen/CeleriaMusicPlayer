use crate::util::config_manager::{ConfigManager, ConfigManagerStruct, ConfigRepresentation};
use std::path::Path;
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
pub async fn set_window_custom_titlebar(
    value: bool,
    config_manager: State<'_, ConfigManagerStruct>,
) -> Result<(), ()> {
    let mut config_manager: MutexGuard<'_, ConfigManager> = config_manager.0.lock().await;
    config_manager.get_config_mut().window.custom_titlebar = value;
    config_manager.save_config();
    Ok(())
}

#[tauri::command]
pub async fn set_window_keep_running_background(
    value: bool,
    config_manager: State<'_, ConfigManagerStruct>,
) -> Result<(), ()> {
    let mut config_manager: MutexGuard<'_, ConfigManager> = config_manager.0.lock().await;
    config_manager
        .get_config_mut()
        .window
        .keep_running_background = value;
    config_manager.save_config();
    Ok(())
}

#[tauri::command]
pub async fn set_library_paths(
    value: String,
    delete: bool,
    config_manager: State<'_, ConfigManagerStruct>,
) -> Result<Vec<String>, ()> {
    let mut config_manager: MutexGuard<'_, ConfigManager> = config_manager.0.lock().await;
    let mut paths: Vec<String> = config_manager.get_config().library.paths;

    if delete {
        let index_opt: Option<usize> = paths.iter().position(|r| r == &value);
        if let Some(index) = index_opt {
            paths.remove(index);
            config_manager.get_config_mut().library.paths = paths;
            config_manager.save_config();
        }
    } else if Path::new(&value).exists() && !paths.contains(&value) {
        paths.push(value);
        config_manager.get_config_mut().library.paths = paths;
        config_manager.save_config();
    }

    Ok(config_manager.get_config().library.paths)
}

#[tauri::command]
pub async fn set_library_show_playlists(
    value: bool,
    config_manager: State<'_, ConfigManagerStruct>,
) -> Result<(), ()> {
    let mut config_manager: MutexGuard<'_, ConfigManager> = config_manager.0.lock().await;
    config_manager.get_config_mut().library.show_playlists = value;
    config_manager.save_config();
    Ok(())
}
