FROM gradle:7.6 AS builder
WORKDIR /usr/src/app

# Copy source files and build.gradle
COPY src ./src
COPY build.gradle .

# Build the Spring Boot JAR file
RUN ["gradle", "bootJar"]

EXPOSE 8080

# Create a new image with OpenJDK 17
FROM openjdk:17
ARG JAR_FILE=build/libs/*.jar

# Copy the built JAR file from the builder stage
COPY --from=builder /usr/src/app/${JAR_FILE} app.jar

# Install curl and jq
RUN microdnf update && \
    microdnf install -y curl jq

# Set the default command to run the Spring Boot app
ENTRYPOINT ["java", "-jar", "/app.jar"]
