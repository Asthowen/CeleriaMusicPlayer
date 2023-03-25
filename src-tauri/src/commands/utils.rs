use serde::Serialize;

#[derive(Clone, Debug, Serialize)]
pub struct Infos {
    covers_path: String,
}

#[tauri::command]
pub async fn infos() -> Result<Infos, ()> {
    Ok(Infos {
        covers_path: dirs::data_dir()
            .unwrap()
            .join("celeria")
            .join("cover")
            .join("albums")
            .to_str()
            .unwrap()
            .to_string(),
    })
}

#[tauri::command]
pub fn open_in_folder(path: String) {
    opener::open(path).ok();
}
