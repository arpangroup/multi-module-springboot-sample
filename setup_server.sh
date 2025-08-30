#!/bin/bash
set -e

# -----------------------------
# User-configurable variables
# -----------------------------
DOMAIN="trustai.co.in"
SUBDOMAIN="admin.${DOMAIN}"
API_DOMAIN="api.${DOMAIN}"
DOC_ROOT="/var/www"
NGINX_CONF_DIR="/etc/nginx/sites-available"
CERT_DIR="/etc/letsencrypt/live/${DOMAIN}"
JKS_PASSWORD="CHANGEIT"
ALIAS="trustai"
JKS_OUTPUT="/root/${DOMAIN}.jks"
EMAIL="test@test.com"

DB_NAME="nft"
DB_USER="root"
DB_PASSWORD="password"
DB_ROOT_PASSWORD="supertest"

# -----------------------------
# Update & install prerequisites
# -----------------------------
apt update -y
apt upgrade -y
apt install -y software-properties-common curl gnupg lsb-release apt-transport-https unzip

# -----------------------------
# Install Nginx
# -----------------------------
apt install -y nginx
systemctl enable nginx
systemctl start nginx

# -----------------------------
# Install Certbot (Let’s Encrypt)
# -----------------------------
apt install -y certbot python3-certbot-nginx

# -----------------------------
# Install Java 21 (OpenJDK)
# -----------------------------
sudo apt update
sudo apt install -y openjdk-21-jdk
java -version

# -----------------------------
# Install MySQL non-interactively
# -----------------------------
export DEBIAN_FRONTEND=noninteractive
apt install -y mysql-server
#mysql --execute="ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '${DB_ROOT_PASSWORD}'; FLUSH PRIVILEGES;"
#mysql --execute="CREATE DATABASE IF NOT EXISTS ${DB_NAME};"
#mysql --execute="CREATE USER IF NOT EXISTS '${DB_USER}'@'localhost' IDENTIFIED BY '${DB_PASSWORD}';"
#mysql --execute="GRANT ALL PRIVILEGES ON ${DB_NAME}.* TO '${DB_USER}'@'localhost'; FLUSH PRIVILEGES;"

mysql --execute="
CREATE DATABASE IF NOT EXISTS ${DB_NAME};
CREATE USER IF NOT EXISTS '${DB_USER}'@'localhost' IDENTIFIED BY '${DB_PASSWORD}';
GRANT ALL PRIVILEGES ON ${DB_NAME}.* TO '${DB_USER}'@'localhost';
FLUSH PRIVILEGES;
"

# Secure root with password (modern MySQL 8+ way, no plugin issues)
mysql --execute="ALTER USER 'root'@'localhost' IDENTIFIED BY '${DB_ROOT_PASSWORD}'; FLUSH PRIVILEGES;"

#mysql <<EOF
#ALTER USER 'root'@'localhost' IDENTIFIED BY '${DB_ROOT_PASSWORD}';
#FLUSH PRIVILEGES;
#CREATE DATABASE IF NOT EXISTS ${DB_NAME};
#CREATE USER IF NOT EXISTS '${DB_USER}'@'localhost' IDENTIFIED BY '${DB_PASSWORD}';
#GRANT ALL PRIVILEGES ON ${DB_NAME}.* TO '${DB_USER}'@'localhost';
#FLUSH PRIVILEGES;
#EOF


# -----------------------------
# Setup Nginx server block
# -----------------------------
mkdir -p ${DOC_ROOT}/${DOMAIN}
cat > ${NGINX_CONF_DIR}/${DOMAIN}.conf <<EOL
server {
    listen 80;
    server_name ${DOMAIN} www.${DOMAIN};

    # Redirect all HTTP -> HTTPS
    return 301 https://\$host\$request_uri;
}

server {
    listen 443 ssl;
    server_name ${DOMAIN} www.${DOMAIN};

    # SSL certificates (reuse Let’s Encrypt ones from Apache)
    ssl_certificate     ${CERT_DIR}/fullchain.pem;
    ssl_certificate_key ${CERT_DIR}/privkey.pem;
    include             /etc/letsencrypt/options-ssl-nginx.conf;
    ssl_dhparam         /etc/letsencrypt/ssl-dhparams.pem;

    root ${DOC_ROOT}/${DOMAIN};
    index index.html;

    # React SPA fallback
    location / {
        try_files \$uri /index.html;
    }

    # Proxy API calls
    location /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }
}
EOL

mkdir -p ${DOC_ROOT}/${SUBDOMAIN}
cat > ${NGINX_CONF_DIR}/${SUBDOMAIN}.conf <<EOL
server {
    listen 80;
    server_name ${SUBDOMAIN} www.${SUBDOMAIN};

    # Redirect all HTTP -> HTTPS
    return 301 https://\$host\$request_uri;
}

server {
    listen 443 ssl;
    server_name ${SUBDOMAIN} www.${SUBDOMAIN};

    # SSL certificates (reuse Let’s Encrypt ones from Apache)
    ssl_certificate     ${CERT_DIR}/fullchain.pem;
    ssl_certificate_key ${CERT_DIR}/privkey.pem;
    include             /etc/letsencrypt/options-ssl-nginx.conf;
    ssl_dhparam         /etc/letsencrypt/ssl-dhparams.pem;

    root ${DOC_ROOT}/${SUBDOMAIN};
    index index.html;

    # React SPA fallback
    location / {
        try_files \$uri /index.html;
    }

    # Proxy API calls
    location /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }
}
EOL

mkdir -p ${DOC_ROOT}/${API_DOMAIN}
cat > ${NGINX_CONF_DIR}/${API_DOMAIN}.conf <<EOL
server {
    listen 80;
    server_name ${API_DOMAIN} www.${API_DOMAIN};

    # Redirect all HTTP -> HTTPS
    return 301 https://\$host\$request_uri;
}

server {
    listen 443 ssl;
    server_name ${API_DOMAIN} www.${API_DOMAIN};

    # SSL certificates (reuse Let’s Encrypt ones from Apache)
    ssl_certificate     ${CERT_DIR}/fullchain.pem;
    ssl_certificate_key ${CERT_DIR}/privkey.pem;
    include             /etc/letsencrypt/options-ssl-nginx.conf;
    ssl_dhparam         /etc/letsencrypt/ssl-dhparams.pem;

    # Proxy API calls
    location / {
        proxy_pass http://127.0.0.1:8080/;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }
}
EOL

# creates symbolic link
ln -sf ${NGINX_CONF_DIR}/${DOMAIN}.conf /etc/nginx/sites-enabled/
ln -sf ${NGINX_CONF_DIR}/${SUBDOMAIN}.conf /etc/nginx/sites-enabled/
ln -sf ${NGINX_CONF_DIR}/${API_DOMAIN}.conf /etc/nginx/sites-enabled/
nginx -t
systemctl reload nginx

# -----------------------------
# Obtain SSL certificates
# -----------------------------
#certbot --nginx -d ${DOMAIN} -d www.${DOMAIN} --non-interactive --agree-tos -m ${EMAIL} --redirect
#certbot --nginx \
#  -d ${DOMAIN} -d www.${DOMAIN} \
#  -d ${SUBDOMAIN} -d www.${SUBDOMAIN} \
#  -d ${API_DOMAIN} -d www.${API_DOMAIN} \
#  --non-interactive --agree-tos -m ${EMAIL} --redirect
sudo certbot --nginx -d trustai.co.in -d www.trustai.co.in -d admin.trustai.co.in -d api.trustai.co.in --expand

# -----------------------------
# Generate Java Keystore from SSL (optional)
# -----------------------------
openssl pkcs12 -export -in ${CERT_DIR}/fullchain.pem -inkey ${CERT_DIR}/privkey.pem \
    -out /tmp/${DOMAIN}.p12 -name ${ALIAS} -password pass:${JKS_PASSWORD}
keytool -importkeystore -deststorepass ${JKS_PASSWORD} -destkeypass ${JKS_PASSWORD} \
    -destkeystore ${JKS_OUTPUT} -srckeystore /tmp/${DOMAIN}.p12 -srcstoretype PKCS12 -srcstorepass ${JKS_PASSWORD} -alias ${ALIAS}

echo "✅ Setup complete!"
echo "🌐 Nginx running with SSL"
echo "📦 Java 21 installed"
echo "🛢️  MySQL setup complete"
echo "🔐 Java Keystore generated at ${JKS_OUTPUT}"




sudo apt-get update
sudo apt-get install -y docker.io docker-compose
sudo systemctl enable docker
sudo systemctl start docker

sudo usermod -aG docker $USER

docker --version
docker-compose --version  # if you’re using classic compose
docker compose version    # if using Compose v2 (plugin)