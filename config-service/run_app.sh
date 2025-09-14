#!/bin/bash

# Path to your Spring Boot application jar
JAR_FILE="target/config-service-0.0.1-SNAPSHOT.jar"

# Optional: Java options
JAVA_OPTS="-Xms512m -Xmx1024m"

# Run the Spring Boot application
#java $JAVA_OPTS -jar "$JAR_FILE"

if [[ -f "$JAR_FILE" ]]; then
    echo "Starting Spring Boot app..."
    java $JAVA_OPTS -jar "$JAR_FILE"
else
    echo "Error: JAR file not found at $JAR_FILE"
    exit 1
fi