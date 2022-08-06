CREATE TABLE movie (
    id identity primary key,
    uuid uuid not null,
    release_date date,
    rank int,
    title varchar(255),
    revenue numeric(19,2)
);