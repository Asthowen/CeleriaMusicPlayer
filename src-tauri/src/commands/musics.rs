use crate::music_manager::{MusicElementComplete, MusicManagerStruct};
use tauri::State;

#[tauri::command]
pub async fn play_sound(
    file: String,
    music_manager: State<'_, MusicManagerStruct>,
) -> Result<(), ()> {
    let _ = music_manager
        .0
        .lock()
        .await
        .append_file_to_queue(&file)
        .await;
    Ok(())
}

#[tauri::command]
pub async fn pause(music_manager: State<'_, MusicManagerStruct>) -> Result<(), ()> {
    music_manager.0.lock().await.pause().await;
    Ok(())
}

#[tauri::command]
pub async fn resume(music_manager: State<'_, MusicManagerStruct>) -> Result<(), ()> {
    music_manager.0.lock().await.resume().await;
    Ok(())
}

#[tauri::command]
pub async fn sound_infos(
    music_manager: State<'_, MusicManagerStruct>,
) -> Result<MusicElementComplete, ()> {
    let music_infos_opt: Option<MusicElementComplete> = music_manager
        .0
        .lock()
        .await
        .get_current_track_complete()
        .await;
    if let Some(music_infos) = music_infos_opt {
        Ok(music_infos)
    } else {
        Err(())
    }
}
