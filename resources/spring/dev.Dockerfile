# Stage 1: Build the application
FROM gradle:8.6-jdk21 AS builder

WORKDIR /prod-stack

# Copy Gradle wrapper and build files
COPY gradle gradle
COPY gradlew .
COPY build.gradle settings.gradle ./

# Make gradlew executable
RUN chmod +x gradlew

# Copy source code
COPY src src

# Build the JAR file
RUN ./gradlew bootJar --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:21-jre

WORKDIR /prod-stack

# Copy the built JAR from builder stage
COPY --from=builder /prod-stack/build/libs/*.jar app.jar

# Expose application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
