FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S cicd_deploy && adduser -S cicd_deploy -G cicd_deploy
COPY target/*.jar config-service.jar
USER cicd_deploy
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "/app/config-service.jar"]
