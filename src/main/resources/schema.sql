CREATE TABLE movie (
    id identity primary key,
    uuid uuid not null,
    launch_date date,
    rank int,
    title varchar(255),
    revenue numeric(19,2)
);