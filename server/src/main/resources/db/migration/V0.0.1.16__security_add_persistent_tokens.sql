
set search_path TO public;

create table persistent_logins (
    series VARCHAR(64) PRIMARY KEY,
    username VARCHAR (255) NOT NULL REFERENCES app_user (username),
    token VARCHAR(64) NOT NULL,
    last_used TIMESTAMP NOT NULL
);
