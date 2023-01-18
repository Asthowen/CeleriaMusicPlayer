use crate::database::schemas::albums;
use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, Insertable, Deserialize, Queryable, Serialize)]
#[diesel(table_name = albums)]
pub struct Album {
    pub uuid: String,
    pub name: String,
    pub artist: Option<String>,
    pub year: Option<i64>,
    pub cover: i16,
}
