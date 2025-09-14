# ---------- Build Stage ----------
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace

# Install Maven
RUN apk add --no-cache maven

# Copy whole repo (adjust .dockerignore to skip target/, .git, etc.)
COPY . .

# Build both modules
RUN #./mvnw -B clean package -DskipTests
RUN mvn -B clean package -DskipTests

# ---------- Config Service ----------
FROM eclipse-temurin:21-jre-alpine AS config-service
WORKDIR /app
COPY --from=build /workspace/config-service/target/*.jar config-server.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "/app/config-server.jar"]

# ---------- Aggregator App ----------
FROM eclipse-temurin:21-jre-alpine AS aggregator
WORKDIR /app
COPY --from=build /workspace/aggregator/target/*.jar app.jar

RUN addgroup -S cicd_deploy && adduser -S cicd_deploy -G cicd_deploy
RUN mkdir -p /logs && chmod -R 777 /logs
USER cicd_deploy

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
