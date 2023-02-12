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
use kira::manager::MainPlaybackState;
use std::path::PathBuf;
use std::process::exit;
use std::sync::Arc;
use std::time::Instant;
use tauri::Manager;
use tauri::{
    CustomMenuItem, Menu, SystemTray, SystemTrayEvent, SystemTrayMenu, SystemTrayMenuItem,
    WindowUrl,
};
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
        log::error!(
            "An error occurred while creating the configuration files: {}",
            e.to_string()
        );
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
    let music_manager_arc: Arc<Mutex<MusicManager>> = Arc::from(Mutex::from(music_manager));
    let music_manager_arc_1: Arc<Mutex<MusicManager>> = music_manager_arc.clone();
    log::debug!("Music manager started!");

    let tray_menu: SystemTrayMenu = SystemTrayMenu::new()
        .add_item(CustomMenuItem::new("open", "Ouvrir Celeria"))
        .add_item(CustomMenuItem::new("play", "Play/Pause"))
        .add_native_item(SystemTrayMenuItem::Separator)
        .add_item(CustomMenuItem::new("github", "GitHub"))
        .add_native_item(SystemTrayMenuItem::Separator)
        .add_item(CustomMenuItem::new("leave", "Quitter Celeria"));
    let system_tray: SystemTray = SystemTray::new().with_menu(tray_menu);

    let app_result = tauri::Builder::default()
        .plugin(tauri_plugin_window_state::Builder::default().build())
        .plugin(tauri_plugin_single_instance::init(|_, _, _| {}))
        .menu(Menu::new())
        .system_tray(system_tray)
        .on_system_tray_event(move |app, event| {
            if let SystemTrayEvent::MenuItemClick { id, .. } = event {
                match id.as_str() {
                    "open" => {
                        if let Some(window) = app.get_window("main") {
                            window.show().ok();
                        } else if tauri::WindowBuilder::new(
                            app,
                            "main",
                            WindowUrl::App(PathBuf::from("index.html")),
                        )
                        .build()
                        .is_err()
                        {
                            app.restart();
                        }
                    }
                    "play" => {
                        let music_manager_arc_clone = music_manager_arc_1.clone();
                        tokio::runtime::Handle::current().spawn(async move {
                            let mut music_manager = music_manager_arc_clone.lock().await;
                            if music_manager.player_state().await == MainPlaybackState::Playing {
                                music_manager.pause().await;
                            } else {
                                music_manager.resume().await;
                            }
                        });
                    }
                    "github" => {
                        open::that("https://github.com/Asthowen/CeleriaMusicPlayer").ok();
                    }
                    "leave" => {
                        exit(0);
                    }
                    _ => {}
                }
            }
        })
        .manage(MusicManagerStruct(music_manager_arc))
        .manage(config_manager_struct.clone())
        .manage(library_manager_struct)
        .invoke_handler(tauri::generate_handler![
            commands::utils::infos,
            commands::musics::play_sound,
            commands::musics::sound_infos,
            commands::musics::pause,
            commands::musics::resume,
            commands::musics::previous,
            commands::musics::next,
            commands::musics::set_volume,
            commands::musics::set_progress,
            commands::library::list_albums::list_albums,
            commands::library::list_tracks::list_tracks,
            commands::library::album_infos::album_infos,
            commands::settings::get_settings,
            commands::settings::set_setting,
            commands::utils::open_in_folder,
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

    app.run(move |app, event| match event {
        tauri::RunEvent::ExitRequested { api, .. } => {
            api.prevent_exit();
            let config_manager_struct_clone = config_manager_struct.clone();
            tokio::runtime::Handle::current().spawn(async move {
                if !config_manager_struct_clone
                    .0
                    .lock()
                    .await
                    .get_config()
                    .window
                    .keep_running_background
                {
                    exit(0);
                }
            });
        }
        tauri::RunEvent::Ready {} => {
            log::info!("Celeria started in {}ms!", start_time.elapsed().as_millis());

            #[cfg(debug_assertions)]
            if let Some(window) = app.get_window("main") {
                window.maximize().unwrap_or_default();
                window.open_devtools();
            }
        }
        _ => {}
    })
}
