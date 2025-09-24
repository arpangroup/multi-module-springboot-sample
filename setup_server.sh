#!/bin/bash
set -e

# -----------------------------
# User-configurable variables
# -----------------------------
DOMAIN="trustai.co.in"
SUBDOMAIN="admin.${DOMAIN}"
API_DOMAIN="api.${DOMAIN}"
DOC_ROOT="/var/www"
DEPLOY_USER="cicd_deploy"
DEPLOY_HOME="/home/${DEPLOY_USER}"
NGINX_CONF_DIR="/etc/nginx/sites-available"
EMAIL="admin@${DOMAIN}"

DEPLOY_PATH="${DEPLOY_HOME}/trustaiapp"

BACKEND_HOST="localhost"
BACKEND_PORT="8080"
BACKEND_URL="http://${BACKEND_HOST}:${BACKEND_PORT}"

JKS_PASSWORD="changeit"
ALIAS="trustai"
JKS_OUTPUT="/root/${DOMAIN}.jks"

# -----------------------------
# Update & install prerequisites (non-interactive)
# -----------------------------
export DEBIAN_FRONTEND=noninteractive

apt-get update -y

# Upgrade packages without prompts and keep local config files
apt-get -o Dpkg::Options::="--force-confdef" \
        -o Dpkg::Options::="--force-confold" \
        upgrade -y

# Install required packages without prompts
apt-get install -y --no-install-recommends \
    software-properties-common \
    curl \
    gnupg \
    lsb-release \
    apt-transport-https \
    unzip \
    ca-certificates \
    nginx \
    certbot \
    python3-certbot-nginx \

systemctl enable nginx
systemctl start nginx


# -----------------------------
# Create deploy user if not exists
# -----------------------------
if ! id -u ${DEPLOY_USER} >/dev/null 2>&1; then
    useradd -m -s /bin/bash ${DEPLOY_USER}
    echo "${DEPLOY_USER} ALL=(ALL) NOPASSWD:ALL" > /etc/sudoers.d/${DEPLOY_USER}
fi



# -----------------------------
# Setup SSH key for deploy user
# -----------------------------
SSH_KEY="ssh-rsa <YOUR_KEY>"

# Create .ssh directory if it does not exist
mkdir -p ${DEPLOY_HOME}/.ssh

# Add the public key to authorized_keys
echo "${SSH_KEY}" > ${DEPLOY_HOME}/.ssh/authorized_keys

# -----------------------------
# Permissions:
# 700 -> .ssh directory: only the user can read, write, and enter the directory
# 600 -> authorized_keys file: only the user can read and write the file
# These are required by SSH to accept the key, otherwise authentication will fail
# -----------------------------
chmod 700 ${DEPLOY_HOME}/.ssh
chmod 600 ${DEPLOY_HOME}/.ssh/authorized_keys

# Set ownership to the deploy user
chown -R ${DEPLOY_USER}:${DEPLOY_USER} ${DEPLOY_HOME}/.ssh

# Reload SSH to apply new keys immediately (optional)
systemctl reload ssh



# -----------------------------
# Prepare directories & permissions
# -----------------------------
# Create domain directories under DOC_ROOT
mkdir -p "${DOC_ROOT}/${DOMAIN}" "${DOC_ROOT}/${SUBDOMAIN}" "${DOC_ROOT}/${API_DOMAIN}"

# Set ownership to deploy user and group to www-data so Nginx can read files
chown -R "${DEPLOY_USER}:www-data" "${DOC_ROOT}"
chmod -R 750 "${DOC_ROOT}"

# Create deploy path and other necessary directories
mkdir -p "${DEPLOY_PATH}" "${DEPLOY_HOME}/logs" "${DEPLOY_HOME}/uploads"

# Set ownership for deployment related directories
chown -R "${DEPLOY_USER}:${DEPLOY_USER}" "${DEPLOY_PATH}" "${DEPLOY_HOME}/logs"
chown -R "${DEPLOY_USER}:www-data" "${DEPLOY_HOME}/uploads"

# Set permissions:
# 750 - owner full, group read+exec, others no access
chmod -R 750 "${DEPLOY_HOME}" "${DEPLOY_PATH}" "${DEPLOY_HOME}/logs"

# 770 - owner and group full access (uploads folder, allowing web server to write)
chmod 770 "${DEPLOY_HOME}/uploads"

echo "✅ Deploy path created at ${DEPLOY_PATH} with correct permissions"


if [ -f "${DEPLOY_HOME}/docker-compose.yml" ]; then
    chown ${DEPLOY_USER}:${DEPLOY_USER} ${DEPLOY_HOME}/docker-compose.yml
fi


# -----------------------------
# Install Docker & Compose v2
# -----------------------------
if ! command -v docker >/dev/null 2>&1; then
    install -m 0755 -d /etc/apt/keyrings
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
    echo \
      "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] \
      https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" \
      > /etc/apt/sources.list.d/docker.list

    apt update -y
    apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

    systemctl enable docker
    systemctl start docker
fi

usermod -aG docker ${DEPLOY_USER}



# -----------------------------
# Setup Nginx server blocks (HTTP only initially)
# -----------------------------
mkdir -p ${NGINX_CONF_DIR}

for SITE in ${DOMAIN} ${SUBDOMAIN} ${API_DOMAIN}; do
cat > ${NGINX_CONF_DIR}/${SITE}.conf <<EOL
server {
    listen 80;
    server_name ${SITE} www.${SITE};

    root ${DOC_ROOT}/${SITE};
    index index.html;

    location / { try_files \$uri /index.html; }

    location /api/ {
        proxy_pass ${BACKEND_URL};
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }
}
EOL
# Enable site
ln -sf ${NGINX_CONF_DIR}/${SITE}.conf /etc/nginx/sites-enabled/
done

ln -sf ${NGINX_CONF_DIR}/${DOMAIN}.conf /etc/nginx/sites-enabled/
ln -sf ${NGINX_CONF_DIR}/${SUBDOMAIN}.conf /etc/nginx/sites-enabled/
ln -sf ${NGINX_CONF_DIR}/${API_DOMAIN}.conf /etc/nginx/sites-enabled/

# Test and reload Nginx
nginx -t
systemctl reload nginx


# -----------------------------
# Obtain SSL certificates (Certbot will auto-configure Nginx for HTTPS)
# -----------------------------
certbot --nginx --agree-tos --non-interactive -m ${EMAIL} \
    -d ${DOMAIN} -d www.${DOMAIN} \
    -d ${SUBDOMAIN} -d www.${SUBDOMAIN} \
    -d ${API_DOMAIN} -d www.${API_DOMAIN} \
    --redirect

# -----------------------------
# Generate JKS & P12 from SSL (optional)
# -----------------------------
#if [ ! -f "${JKS_OUTPUT}" ]; then
#    openssl pkcs12 -export \
#            -in /etc/letsencrypt/live/${DOMAIN}/fullchain.pem \
#            -inkey /etc/letsencrypt/live/${DOMAIN}/privkey.pem \
#            -out /tmp/${DOMAIN}.p12 -name ${ALIAS} -password pass:${JKS_PASSWORD}
#
#    keytool -importkeystore \
#        -deststorepass ${JKS_PASSWORD} \
#        -destkeypass ${JKS_PASSWORD} \
#        -destkeystore ${JKS_OUTPUT} \
#        -srckeystore /tmp/${DOMAIN}.p12 \
#        -srcstoretype PKCS12 \
#        -srcstorepass ${JKS_PASSWORD} \
#        -alias ${ALIAS}
#fi

echo "✅ Setup complete!"
echo "🌐 Nginx running with SSL"
echo "📦 Docker installed"
echo "🔐 Java Keystore generated at ${JKS_OUTPUT}"
