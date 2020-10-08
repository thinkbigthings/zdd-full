
-- this is a non-transactional statement, best to not mix transactional statements in your migration

CREATE INDEX CONCURRENTLY index_persistent_logins_username ON persistent_logins(username);
