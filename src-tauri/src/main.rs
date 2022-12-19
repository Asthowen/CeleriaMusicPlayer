#![cfg_attr(
    all(not(debug_assertions), target_os = "windows"),
    windows_subsystem = "windows"
)]

use celeria::commands;
use celeria::music_manager::{MusicManager, MusicManagerStruct};
use celeria::util::utils::unix_time;
use std::sync::Arc;
use tauri::Menu;
use tokio::sync::Mutex;

#[tokio::main]
async fn main() {
    let start_time: u128 = unix_time().as_millis();

    std::env::set_var("RUST_LOG", "debug");
    celeria::util::logger::init_logger();

    log::info!("Starting Celeria...");
    log::debug!("Starting music manager...");
    let mut music_manager: MusicManager = MusicManager::init();
    music_manager.start_queue_manager().await;
    log::debug!("Music manager started!");

    tauri::Builder::default()
        .menu(Menu::new())
        .manage(MusicManagerStruct(Arc::from(Mutex::from(music_manager))))
        .invoke_handler(tauri::generate_handler![
            commands::musics::play_sound,
            commands::musics::sound_infos,
            commands::musics::pause,
            commands::musics::resume
        ])
        .on_page_load(move |_, _| {
            let now: u128 = unix_time().as_millis();
            log::info!("Celeria started in {}ms!", now - start_time);
        })
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
