CREATE DATABASE IF NOT EXISTS ${DATABASE_NAME}
  COMMENT 'ENTRADA database'
  LOCATION '${DB_LOC}'
  WITH DBPROPERTIES ('creator'='ENTRADA', 'developer.'='www.sidnlabs.nl');