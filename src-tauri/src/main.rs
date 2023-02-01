#![cfg_attr(
    all(not(debug_assertions), target_os = "windows"),
    windows_subsystem = "windows"
)]

use celeria::commands;
use celeria::core::music::library_manager::{LibraryManager, LibraryManagerStruct};
use celeria::core::music::music_manager::{MusicManager, MusicManagerStruct};
use celeria::database::sqlite::SqlitePooled;
use celeria::util::config_manager::{ConfigManager, ConfigManagerStruct};
use diesel_migrations::{embed_migrations, EmbeddedMigrations, MigrationHarness};
use std::path::PathBuf;
use std::process::exit;
use std::sync::Arc;
use std::time::Instant;
#[cfg(debug_assertions)]
use tauri::Manager;
use tauri::Menu;
use tokio::sync::Mutex;

pub const MIGRATIONS: EmbeddedMigrations = embed_migrations!();

#[tokio::main]
async fn main() {
    let start_time: Instant = Instant::now();
    std::env::set_var("RUST_LOG", "debug");
    celeria::util::logger::init_logger();

    log::info!("Starting Celeria...");

    let celeria_data_dir: PathBuf = dirs::data_dir().unwrap_or_else(|| {
        log::error!("An error occurred while retrieving the configuration files folder, please open an issue at: https://github.com/Asthowen/CeleriaMusicPlayer/issues/new so that we can solve your issue.");
        exit(9);
    }).join("celeria");
    std::fs::create_dir_all(celeria_data_dir).unwrap_or_else(|e| {
        log::error!("An error occurred while creating the configuration files: {}", e.to_string());
        exit(9);
    });

    let celeria_config_dir: PathBuf = dirs::config_dir().unwrap_or_else(|| {
        log::error!("An error occurred while retrieving the configuration files folder, please open an issue at: https://github.com/Asthowen/CeleriaMusicPlayer/issues/new so that we can solve your issue.");
        exit(9);
    }).join("celeria");
    let config_manager: ConfigManager = ConfigManager::init(&celeria_config_dir, "config.json");
    let config_manager_struct: ConfigManagerStruct =
        ConfigManagerStruct(Arc::from(Mutex::from(config_manager)));
    let library_manager: LibraryManager =
        LibraryManager::init(&celeria_config_dir, config_manager_struct.clone());

    let mut conn: SqlitePooled = match library_manager.get_pool().get() {
        Ok(pool) => pool,
        Err(error) => {
            log::error!(
                "An error occurred during the creation of the database: {}",
                error.to_string()
            );
            exit(9)
        }
    };
    conn.run_pending_migrations(MIGRATIONS)
        .unwrap_or_else(|error| {
            log::error!(
                "An error occurred when deploying migrations: {}",
                error.to_string()
            );
            exit(1);
        });
    drop(conn);

    let library_manager_clone: LibraryManager = library_manager.clone();
    tokio::spawn(async move {
        let start_time: Instant = Instant::now();
        log::info!("Starting the indexing of audio library files...");
        let added_counter: i64 = library_manager_clone.scan_all().await;
        if added_counter == 0 {
            log::info!(
                "Indexing of audio libraries completed in {}s, no new titles found!",
                start_time.elapsed().as_secs()
            );
        } else if added_counter == 1 {
            log::info!(
                "Indexing of audio libraries completed in {}s, one title added!",
                start_time.elapsed().as_secs()
            );
        } else {
            log::info!(
                "Indexing of audio libraries completed in {}s, {} titles added!",
                start_time.elapsed().as_secs(),
                added_counter
            );
        }
    });

    let library_manager_struct =
        LibraryManagerStruct(Arc::from(Mutex::from(library_manager.clone())));

    log::debug!("Starting music manager...");
    let mut music_manager: MusicManager = MusicManager::init(library_manager_struct.clone());
    music_manager.start_queue_manager().await;
    log::debug!("Music manager started!");

    let app_result = tauri::Builder::default()
        .plugin(tauri_plugin_window_state::Builder::default().build())
        .menu(Menu::new())
        .manage(MusicManagerStruct(Arc::from(Mutex::from(music_manager))))
        .manage(config_manager_struct)
        .manage(library_manager_struct)
        .invoke_handler(tauri::generate_handler![
            commands::infos::infos,
            commands::musics::play_sound,
            commands::musics::sound_infos,
            commands::musics::pause,
            commands::musics::resume,
            commands::musics::previous,
            commands::musics::next,
            commands::musics::set_volume,
            commands::musics::set_progress,
            commands::library::list_albums::list_albums,
            commands::library::album_infos::album_infos
        ])
        .build(tauri::generate_context!());
    let app = match app_result {
        Ok(app) => app,
        Err(error) => {
            log::error!(
                "An error occurred during the launch of Celeria: {}",
                error.to_string()
            );
            exit(101);
        }
    };

    app.run(move |app, event| {
        if let tauri::RunEvent::Ready = event {
            log::info!("Celeria started in {}ms!", start_time.elapsed().as_millis());

            #[cfg(debug_assertions)]
            if let Some(window) = app.get_window("main") {
                window.maximize().unwrap_or_default();
                window.open_devtools();
            }
        }
    })
}
