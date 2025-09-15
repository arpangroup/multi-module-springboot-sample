#!/bin/bash
set -e

# Required environment variables
: "${REMOTE_USER:?Need to set REMOTE_USER}"
: "${REMOTE_HOST:?Need to set REMOTE_HOST}"
: "${REMOTE_SSH_PORT:?Need to set REMOTE_SSH_PORT}"
: "${DEPLOY_PATH:?Need to set DEPLOY_PATH}"
: "${REGISTRY:?Need to set REGISTRY}"
: "${IMAGE_NAME:?Need to set IMAGE_NAME}"
: "${IMAGE_TAG:?Need to set IMAGE_TAG}"
: "${DB_NAME:?Need to set DB_NAME}"
: "${DB_USER:?Need to set DB_USER}"
: "${DB_PASSWORD:?Need to set DB_PASSWORD}"
: "${DB_ROOT_PASSWORD:?Need to set DB_ROOT_PASSWORD}"
: "${CONFIG_SERVER_HOST:?Need to set CONFIG_SERVER_HOST}"
: "${GHCR_USER:?Need to set GHCR_USER}"
: "${GHCR_PAT:?Need to set GHCR_PAT}"

echo "Deploying image tag: $IMAGE_TAG"

ssh -p "$REMOTE_SSH_PORT" -o StrictHostKeyChecking=no "$REMOTE_USER@$REMOTE_HOST" << EOF
set -e

echo "📂 Switching to deploy directory: $DEPLOY_PATH"
cd "$DEPLOY_PATH"

echo "📄 Updating .env variables"
cat > .env << EOT
REGISTRY=$REGISTRY
IMAGE_NAME=$IMAGE_NAME
IMAGE_TAG=$IMAGE_TAG
DB_NAME=$DB_NAME
DB_USER=$DB_USER
DB_PASSWORD=$DB_PASSWORD
DB_ROOT_PASSWORD=$DB_ROOT_PASSWORD
CONFIG_SERVER_HOST=$CONFIG_SERVER_HOST
EOT

echo "🔐 Logging in to GHCR..."
echo "$GHCR_PAT" | docker login ghcr.io -u "$GHCR_USER" --password-stdin

echo "⬇️ Pulling latest image"
docker compose -f docker-compose.app.yml pull

echo "🔄 Restarting services"
docker compose -f docker-compose.app.yml up -d

echo "🧹 Cleaning up unused Docker images"
docker image prune -f
EOF

echo "✅ Deployment finished."
