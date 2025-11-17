FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos pom y código
COPY pom.xml .
COPY src ./src

# 🔴 IMPORTANTE: generamos un jar ejecutable de Spring Boot
RUN mvn -DskipTests clean package spring-boot:repackage

# ---- Runtime ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiamos SOLO el jar final
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]
