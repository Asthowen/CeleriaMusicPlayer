use crate::core::music::library_manager::{LibraryManager, LibraryManagerStruct};
use crate::database::models::albums::Album;
use serde::Serialize;
use tauri::State;

#[derive(Serialize)]
pub struct ListAlbums {
    pub list: Vec<Album>,
    pub pictures_path: String,
}

#[tauri::command]
pub async fn list_albums(
    library_manager: State<'_, LibraryManagerStruct>,
) -> Result<Vec<Album>, ()> {
    let library_manager: tokio::sync::MutexGuard<'_, LibraryManager> =
        library_manager.0.lock().await;

    Ok(library_manager.get_all_albums())
}
