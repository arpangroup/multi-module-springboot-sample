-- App database (already created via MYSQL_DATABASE env, but safe to double-check)
CREATE DATABASE IF NOT EXISTS trustai_db;

-- Config server database
CREATE DATABASE IF NOT EXISTS configdb;

-- Grant privileges to the main user
GRANT ALL PRIVILEGES ON `trustai_db`.* TO '${DB_USER}'@'%';
GRANT ALL PRIVILEGES ON `configdb`.* TO '${DB_USER}'@'%';

FLUSH PRIVILEGES;