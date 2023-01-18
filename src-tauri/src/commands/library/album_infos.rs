use crate::core::music::library_manager::{LibraryManager, LibraryManagerStruct};
use crate::database::models::albums::Album;
use crate::database::models::tracks::Track;
use tauri::State;

#[tauri::command]
pub async fn album_infos(
    uuid: &str,
    library_manager: State<'_, LibraryManagerStruct>,
) -> Result<(Album, Vec<Track>, String), ()> {
    let library_manager: tokio::sync::MutexGuard<'_, LibraryManager> =
        library_manager.0.lock().await;

    if let Some(album_infos) = library_manager.get_album_by_uuid(uuid) {
        Ok((
            album_infos.0,
            album_infos.1,
            dirs::data_dir()
                .unwrap()
                .join("celeria")
                .join("cover")
                .join("albums")
                .to_str()
                .unwrap()
                .to_string(),
        ))
    } else {
        Err(())
    }
}
