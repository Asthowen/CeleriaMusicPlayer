use crate::core::music::library_manager::{LibraryManager, LibraryManagerStruct};
use crate::database::models::albums::Album;
use crate::database::models::tracks::Track;
use tauri::State;

#[tauri::command]
pub async fn album_infos(
    uuid: &str,
    library_manager: State<'_, LibraryManagerStruct>,
) -> Result<(Album, Vec<Track>), ()> {
    let library_manager: tokio::sync::MutexGuard<'_, LibraryManager> =
        library_manager.0.lock().await;

    library_manager
        .get_album_by_uuid(uuid)
        .map(Ok)
        .unwrap_or(Err(()))
}
