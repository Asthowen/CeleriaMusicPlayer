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
) -> Result<ListAlbums, ()> {
    let library_manager: tokio::sync::MutexGuard<'_, LibraryManager> =
        library_manager.0.lock().await;

    Ok(ListAlbums {
        list: library_manager.get_all_albums(),
        pictures_path: dirs::data_dir()
            .unwrap()
            .join("celeria")
            .join("cover")
            .join("albums")
            .to_str()
            .unwrap()
            .to_string(),
    })
}
