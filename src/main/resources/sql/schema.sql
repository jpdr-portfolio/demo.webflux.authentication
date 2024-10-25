--DROP TABLE IF EXISTS login_user;
--DROP TABLE IF EXISTS login_log;

CREATE TABLE IF NOT EXISTS login_user (
  id int AUTO_INCREMENT primary key,
  username VARCHAR(254),
  password VARCHAR(255),
  is_active BOOLEAN,
  creation_date TIMESTAMP WITH TIME ZONE,
  deletion_date TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS login_log (
  id int AUTO_INCREMENT primary key,
  login_id int not null,
  login_date TIMESTAMP WITH TIME ZONE not null,
  expiration_date TIMESTAMP WITH TIME ZONE not null
);