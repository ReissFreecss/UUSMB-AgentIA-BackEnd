# --- Etapa de Construcción (Build Stage) ---
# Usamos una imagen de Maven con Eclipse Temurin 21 (OpenJDK) para compilar el proyecto.
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Establecemos el directorio de trabajo dentro del contenedor.
WORKDIR /app

# Copiamos primero el pom.xml para aprovechar el cache de capas de Docker.
COPY pom.xml .
# Descargamos las dependencias.
RUN mvn dependency:go-offline

# Copiamos el resto del código fuente.
COPY src ./src

# Compilamos la aplicación, generando el archivo .jar. Se omiten los tests.
RUN mvn package -DskipTests

# --- Etapa de Ejecución (Run Stage) ---
# Usamos una imagen ligera de Eclipse Temurin 21 (OpenJDK) solo con el JRE para ejecutar la aplicación.
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copiamos el .jar generado en la etapa anterior.
COPY --from=build /app/target/*.jar app.jar
# Exponemos el puerto 8080, que es el puerto por defecto de Spring Boot.
EXPOSE 8080
# Comando para iniciar la aplicación cuando el contenedor se inicie.
ENTRYPOINT ["java", "-jar", "app.jar"]