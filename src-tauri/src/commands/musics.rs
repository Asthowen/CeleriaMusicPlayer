use crate::core::music::music_manager::{MusicElementComplete, MusicManagerStruct};
use tauri::State;

#[tauri::command]
pub async fn play_sound(
    track_uuid: String,
    music_manager: State<'_, MusicManagerStruct>,
) -> Result<(), ()> {
    let _ = music_manager
        .0
        .lock()
        .await
        .append_track_to_queue(&track_uuid)
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
) -> Result<Option<MusicElementComplete>, ()> {
    Ok(music_manager
        .0
        .lock()
        .await
        .get_current_track_complete()
        .await)
}
