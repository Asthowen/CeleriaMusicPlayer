use crate::core::music::library_manager::{LibraryManager, LibraryManagerStruct};
use crate::database::models::tracks::Track;
use tauri::State;

#[tauri::command]
pub async fn list_tracks(
    library_manager: State<'_, LibraryManagerStruct>,
) -> Result<Vec<Track>, ()> {
    let library_manager: tokio::sync::MutexGuard<'_, LibraryManager> =
        library_manager.0.lock().await;

    Ok(library_manager.get_all_tracks())
}
