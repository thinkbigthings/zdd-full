
set search_path TO public;

ALTER TABLE app_user ADD COLUMN password VARCHAR DEFAULT '';

INSERT INTO app_user (username, email, display_name, password, enabled)
    VALUES ('admin', 'admin@example.com', 'admin', '{bcrypt}$2a$10$WhdPCuRTRbiQlwLj6x3Z7em2SEIVmbOSYmd8uch4NjQ3FagH4zI5C', TRUE);