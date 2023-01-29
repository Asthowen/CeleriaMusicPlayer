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
pub async fn pause(music_manager: State<'_, MusicManagerStruct>) -> Result<bool, ()> {
    Ok(music_manager.0.lock().await.pause().await)
}

#[tauri::command]
pub async fn resume(music_manager: State<'_, MusicManagerStruct>) -> Result<bool, ()> {
    Ok(music_manager.0.lock().await.resume().await)
}

#[tauri::command]
pub async fn next(music_manager: State<'_, MusicManagerStruct>) -> Result<bool, ()> {
    Ok(music_manager.0.lock().await.next().await)
}

#[tauri::command]
pub async fn previous(music_manager: State<'_, MusicManagerStruct>) -> Result<bool, ()> {
    Ok(music_manager.0.lock().await.previous().await)
}

#[tauri::command]
pub async fn set_volume(
    volume: f64,
    music_manager: State<'_, MusicManagerStruct>,
) -> Result<bool, ()> {
    Ok(music_manager.0.lock().await.set_volume(volume).await)
}

#[tauri::command]
pub async fn set_progress(
    time: f64,
    music_manager: State<'_, MusicManagerStruct>,
) -> Result<bool, ()> {
    Ok(music_manager.0.lock().await.set_progress(time).await)
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
