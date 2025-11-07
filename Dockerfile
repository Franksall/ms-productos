# Java 17.
FROM eclipse-temurin:17-jdk-alpine


LABEL maintainer="Franksall"

# --- CONFIGURACIÓN ---

WORKDIR /app

# --- COPIAR EL CÓDIGO ---

COPY build/libs/*.jar app.jar

# --- EXPOSICIÓN ---
# uerto 8081 de ms-productos.
EXPOSE 8081

# --- EJECUCIÓN ---

ENTRYPOINT ["java", "-jar", "/app/app.jar"]