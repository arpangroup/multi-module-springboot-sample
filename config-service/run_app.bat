@echo off
REM Set path to your Spring Boot application JAR
set JAR_FILE=target\config-service-0.0.1-SNAPSHOT.jar

REM Optional: Java options
set JAVA_OPTS=-Xms512m -Xmx1024m

REM Check if JAR file exists
if exist "%JAR_FILE%" (
    echo Starting Spring Boot application on port 8888...
     start "Spring Boot App" java %JAVA_OPTS% -jar "%JAR_FILE%" --server.port=8888 > app.log 2>&1
) else (
    echo ERROR: JAR file not found at %JAR_FILE%
    exit /b 1
)
