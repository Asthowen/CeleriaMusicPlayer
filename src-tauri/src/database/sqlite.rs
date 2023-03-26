use diesel::prelude::*;
use diesel::r2d2::{ConnectionManager, Pool};
use r2d2::PooledConnection;
use std::path::{Path, PathBuf};

pub type SqlitePool = Pool<ConnectionManager<SqliteConnection>>;
pub type SqlitePooled = PooledConnection<ConnectionManager<SqliteConnection>>;

pub fn get_pool<P: AsRef<Path>>(db_path: P) -> SqlitePool {
    let db_path: PathBuf = db_path.as_ref().to_owned();

    let manager: ConnectionManager<SqliteConnection> =
        ConnectionManager::<SqliteConnection>::new(db_path.to_str().unwrap());
    Pool::builder()
        .max_size(10)
        .connection_timeout(std::time::Duration::from_secs(1))
        .build(manager)
        .map_err(|err| {
            log::error!("{}", err.to_string());
            std::process::exit(9);
        })
        .unwrap()
}
