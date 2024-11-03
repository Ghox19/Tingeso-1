# Etapa de construcción
FROM --platform=linux/amd64 maven:3.8.6-openjdk-18-slim AS build
WORKDIR /app

# Copiar el archivo POM primero
COPY pom.xml .

# Copiar el código fuente
COPY src ./src

# Ejecutar la construcción con logging detallado
RUN mvn clean package -DskipTests && \
    ls -la target/

# Etapa de ejecución
FROM --platform=linux/amd64 openjdk:17-jdk-slim
WORKDIR /app

# Copiar el JAR usando el nuevo nombre definido en pom.xml
COPY --from=build /app/target/backend.jar app.jar

ENV PORT=8080 \
    DB_HOST=104.41.28.230 \
    DB_USER=postgres \
    DB_PASSWORD=admin 

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]