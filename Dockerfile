# syntax=docker/dockerfile:1

# -----------------------------------------------------------------------------
# Stage 1: Builder
# -----------------------------------------------------------------------------
FROM eclipse-temurin:25-jdk AS builder

WORKDIR /app

# Copy Gradle wrapper and build scripts first for dependency caching
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle

# Download dependencies (cached unless build scripts change)
RUN ./gradlew dependencies --no-daemon --configuration runtimeClasspath

# Copy source and build the application
COPY src ./src
RUN ./gradlew bootJar -x test --no-daemon

# -----------------------------------------------------------------------------
# Stage 2: Runtime
# -----------------------------------------------------------------------------
FROM eclipse-temurin:25-jre AS runtime

# Install curl for health checks
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Create non-root user
RUN groupadd --system appgroup && useradd --system --gid appgroup --no-create-home appuser

WORKDIR /app

# Copy only the built JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Set ownership and switch to non-root user
RUN chown -R appuser:appgroup /app
USER appuser

# JVM options optimized for containers
ENV JAVA_OPTS="\
    -XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -Djava.security.egd=file:/dev/./urandom \
    -Dspring.backgroundpreinitializer.ignore=true \
    -Dspring.jmx.enabled=false \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath=/tmp \
    "

# Application ports
EXPOSE 8080
EXPOSE 10103

# Health check using actuator
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -fsS http://localhost:10103/actuator/health || exit 1

# Use exec form so signals are forwarded to the JVM process
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
