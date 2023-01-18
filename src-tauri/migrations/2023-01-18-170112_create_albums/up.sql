CREATE TABLE "albums"
(
    uuid   varchar(36)       not null
        unique,
    name   text              not null,
    artist varchar,
    year   int,
    cover  integer default 0 not null
);