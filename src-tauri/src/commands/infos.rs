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
