
set search_path TO public;

CREATE TABLE user_type (
    type_name    VARCHAR      NOT NULL PRIMARY KEY
);

INSERT INTO user_type (type_name) VALUES ('REGISTERED');
INSERT INTO user_type (type_name) VALUES ('GUEST');

CREATE TABLE users (
    id             BIGSERIAL    NOT NULL PRIMARY KEY,
    username       VARCHAR(255) NOT NULL UNIQUE,
    email          VARCHAR(255) NOT NULL DEFAULT '',
    display_name   VARCHAR(255) NOT NULL DEFAULT '',
    enabled        BOOLEAN      NOT NULL DEFAULT FALSE,
    registration   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    favorite_color VARCHAR    NOT NULL DEFAULT '',
    height         VARCHAR    NOT NULL DEFAULT '',
    type_name      VARCHAR    NOT NULL DEFAULT 'REGISTERED' REFERENCES user_type (type_name)
);
