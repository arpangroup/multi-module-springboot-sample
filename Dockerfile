# ---------- Build Stage ----------
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace

# Install Maven
RUN apk add --no-cache maven

# Copy whole repo (adjust .dockerignore to skip target/, .git, etc.)
COPY . .

# Build both modules
RUN mvn -B clean package -DskipTests

# ---------- Config Service ----------
FROM eclipse-temurin:21-jre-alpine AS config-service
WORKDIR /app

# Copy built jar
COPY --from=build /workspace/config-service/target/*.jar config-server.jar

# Create non-root user
RUN addgroup -S cicd_deploy && adduser -S cicd_deploy -G cicd_deploy

# Create logs directory and give ownership to non-root user
RUN mkdir -p /app/logs \
    && chown -R cicd_deploy:cicd_deploy /app/logs \
    && chmod 755 /app/logs

USER cicd_deploy

EXPOSE 8888
ENTRYPOINT ["java", "-jar", "/app/config-server.jar"]

# ---------- Aggregator App ----------
FROM eclipse-temurin:21-jre-alpine AS aggregator
WORKDIR /app

# Copy built jar
COPY --from=build /workspace/aggregator/target/*.jar app.jar

# Create non-root user
RUN addgroup -S cicd_deploy && adduser -S cicd_deploy -G cicd_deploy

# Create logs directory and give ownership to non-root user
# Create logs directory and give ownership to non-root user
RUN mkdir -p /app/logs \
    && chown -R cicd_deploy:cicd_deploy /app/logs \
    && chmod 755 /app/logs

USER cicd_deploy

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
