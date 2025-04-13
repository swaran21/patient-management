# === Stage 1: Build ===
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Pre-fetch dependencies (helps with caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Now copy the source and build the app
COPY src ./src
RUN mvn clean package

# === Stage 2: Run ===
FROM openjdk:21-jdk AS runner

WORKDIR /app

COPY --from=builder /app/target/analytics-service-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 4002

ENTRYPOINT ["java", "-jar", "app.jar"]
