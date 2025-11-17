# ==========
# Etapa de build (compila el proyecto con Maven y JDK 21)
# ==========
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos el pom y las fuentes
COPY pom.xml .
COPY src ./src

# Compilamos y generamos el JAR (sin tests para que sea más rápido)
RUN mvn -DskipTests clean package

# ==========
# Etapa de runtime (solo JRE para correr el jar)
# ==========
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiamos el jar que se generó en la etapa de build
COPY --from=build /app/target/*.jar app.jar

# El puerto interno lo define cada aplicación en application.yml
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
