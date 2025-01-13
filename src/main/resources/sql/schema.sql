--DROP TABLE IF EXISTS login_user;
--DROP TABLE IF EXISTS login_log;

CREATE TABLE IF NOT EXISTS login_user (
  id bigserial primary key,
  username VARCHAR(254) NOT NULL,
  password VARCHAR(255) NOT NULL,
  is_active BOOLEAN NOT NULL,
  creation_date TIMESTAMP WITH TIME ZONE NOT NULL,
  deletion_date TIMESTAMP WITH TIME ZONE
);