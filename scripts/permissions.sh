DOMAIN="trustai.co.in"
SUBDOMAIN="admin.${DOMAIN}"
API_DOMAIN="api.${DOMAIN}"
DOC_ROOT="/var/www"
DEPLOY_USER="cicd_deploy"
DEPLOY_HOME="/home/${DEPLOY_USER}"
NGINX_CONF_DIR="/etc/nginx/sites-available"
EMAIL="admin@${DOMAIN}"
DEPLOY_PATH="${DEPLOY_HOME}/trustaiapp"


# -----------------------------
# Create deploy user if not exists
# -----------------------------
if ! id -u ${DEPLOY_USER} >/dev/null 2>&1; then
    useradd -m -s /bin/bash ${DEPLOY_USER}
    echo "${DEPLOY_USER} ALL=(ALL) NOPASSWD:ALL" > /etc/sudoers.d/${DEPLOY_USER}
fi


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
