table! {
    tracks (uuid) {
        uuid -> Varchar,
        title -> Nullable<Varchar>,
        album -> Nullable<Varchar>,
        duration -> BigInt,
        file_path -> Varchar,
    }
}
table! {
    albums (uuid) {
        uuid -> Varchar,
        name -> Text,
        artist -> Nullable<Varchar>,
        year -> Nullable<BigInt>,
        cover -> SmallInt,
    }
}
allow_tables_to_appear_in_same_query!(tracks, albums);
