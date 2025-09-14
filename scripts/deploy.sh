#!/bin/bash

set -e

echo "🔐 Logging in to GHCR..."
echo "$GHCR_PAT" | docker login ghcr.io -u "$GHCR_USER" --password-stdin

echo "📂 Switching to deploy directory"
cd "$DEPLOY_PATH"

echo "📄 Creating .env file"
cat > .env <<EOL
REGISTRY=$REGISTRY
IMAGE_NAME=$IMAGE_NAME
IMAGE_TAG=$IMAGE_TAG
DB_NAME=$DB_NAME
DB_USER=$DB_USER
DB_PASSWORD=$DB_PASSWORD
DB_ROOT_PASSWORD=$DB_ROOT_PASSWORD
EOL

echo "⬇️ Pulling latest images"
docker compose pull

echo "🔄 Restarting services"
docker compose up -d --remove-orphans

echo "⏳ Waiting for config-service to become healthy..."
retries=15
until [ "$(docker inspect -f '{{.State.Health.Status}}' config-service)" = "healthy" ] || [ $retries -eq 0 ]; do
  echo "Waiting for config-service..."
  sleep 5
  retries=$((retries-1))
done

if [ $retries -eq 0 ]; then
  echo "❌ config-service failed to become healthy"
  docker compose logs config-service --tail 20
  exit 1
fi

echo "🧹 Cleaning up unused Docker images"
docker image prune -f

echo "✅ Deployment completed successfully"
