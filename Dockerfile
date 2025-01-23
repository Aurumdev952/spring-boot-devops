# Build stage with all the development dependencies
FROM maven:3.8.4-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
# Cache dependencies
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package

## Security scanning stage
#FROM aquasec/trivy:latest AS security
#COPY --from=builder /app/target/*.jar /app/application.jar
#RUN trivy fs --no-progress /app

# Final minimal runtime
FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser:appgroup
COPY --from=builder --chown=appuser:appgroup /app/target/*.jar /app/application.jar
# Configure security options
RUN chmod 400 /app/application.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/application.jar"]