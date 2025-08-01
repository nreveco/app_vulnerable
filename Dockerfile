# VULNERABLE Dockerfile with security issues
FROM openjdk:11-jre-slim

# VULNERABLE: Running as root user
USER root

# VULNERABLE: Installing unnecessary packages
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    telnet \
    netcat \
    && rm -rf /var/lib/apt/lists/*

# VULNERABLE: Exposing sensitive information
LABEL maintainer="admin@vulnerable-app.com"
LABEL version="1.0.0"
LABEL description="Vulnerable Spring Boot Application for Security Testing"

# VULNERABLE: Hardcoded credentials in environment
ENV DB_PASSWORD=admin123
ENV SECRET_KEY=vulnerable-secret-key-2024
ENV ADMIN_TOKEN=admin-token-12345

# Create application directory
WORKDIR /app

# Copy the JAR file
COPY target/vulnerable-spring-app-1.0.0.jar app.jar

# VULNERABLE: Exposing multiple ports
EXPOSE 8080 8081 8082

# VULNERABLE: Running with excessive privileges
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# Health check (VULNERABLE: Exposes internal endpoints)
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1
