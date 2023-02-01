use serde::Serialize;
use std::path::PathBuf;
use std::time::Duration;

pub fn unix_time() -> Duration {
    std::time::SystemTime::now()
        .duration_since(std::time::UNIX_EPOCH)
        .unwrap_or(Duration::from_secs(0))
}

pub fn save_json_to_file<T>(json: &T, path: &PathBuf)
where
    T: ?Sized + Serialize,
{
    std::fs::write(path, serde_json::to_string_pretty(&json).unwrap()).unwrap_or_else(|error| {
        log::error!(
            "An error occurred while writing to the {} file ({}).",
            path.clone().into_os_string().into_string().unwrap(),
            error
        );
    });
}

pub fn read_json_file<T>(path: &PathBuf) -> T
where
    for<'a> T: serde::Deserialize<'a>,
{
    serde_json::from_str(&std::fs::read_to_string(path).unwrap_or_else(|_| "{}".to_owned()))
        .unwrap()
}
