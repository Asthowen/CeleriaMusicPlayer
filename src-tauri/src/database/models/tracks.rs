use crate::database::schemas::tracks;
use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, Insertable, Serialize, Deserialize, Queryable)]
#[diesel(table_name = tracks)]
pub struct Track {
    pub uuid: String,
    pub title: Option<String>,
    pub album: Option<String>,
    pub duration: i64,
    pub file_path: String,
}
