use crate::core::music::library_manager::{LibraryManager, LibraryManagerStruct};
use crate::database::models::albums::Album;
use tauri::State;

#[tauri::command]
pub async fn list_albums(
    library_manager: State<'_, LibraryManagerStruct>,
) -> Result<Vec<Album>, ()> {
    let library_manager: tokio::sync::MutexGuard<'_, LibraryManager> =
        library_manager.0.lock().await;

    Ok(library_manager.get_all_albums())
}
