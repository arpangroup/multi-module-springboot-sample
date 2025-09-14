-- App database (already created via MYSQL_DATABASE env, but safe to double-check)
CREATE DATABASE IF NOT EXISTS `${DB_NAME}`;

-- Config server database
CREATE DATABASE IF NOT EXISTS configdb;

-- Grant privileges to the main user
GRANT ALL PRIVILEGES ON `${DB_NAME}`.* TO '${DB_USER}'@'%';
GRANT ALL PRIVILEGES ON `configdb`.* TO '${DB_USER}'@'%';

FLUSH PRIVILEGES;