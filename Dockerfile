# Use a multi-stage build
# Stage 1: Build the application
FROM openjdk:24-jdk-slim AS builder

# Set the working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn .mvn

# Copy source code
COPY src ./src

# Make Maven wrapper executable
RUN chmod +x mvnw

# Build the application
RUN ./mvnw clean package -DskipTests

# Stage 2: Create the runtime image
FROM openjdk:24-jre-slim

# Set the working directory
WORKDIR /app

# Create a non-root user
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Copy the JAR file from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Change ownership of the app directory
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose the application port
EXPOSE 13000

# Set environment variables
ENV JAVA_OPTS=""

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:13000/actuator/health || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
