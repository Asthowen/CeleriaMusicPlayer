CREATE TABLE "tracks"
(
    uuid      varchar(36) not null
        unique,
    title     varchar,
    album     varchar(36),
    duration  integer     not null,
    file_path varchar     not null
);