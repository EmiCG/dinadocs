# # Etapa 1: Construcción (Usa una imagen con Maven)
# FROM maven:3.9-eclipse-temurin-21 AS build
# WORKDIR /app
# COPY . .
# # Compilamos saltando tests para ahorrar tiempo y memoria
# RUN mvn clean package -DskipTests

# # Etapa 2: Ejecución (Usa una imagen ligera solo con Java)
# FROM eclipse-temurin:21-jre-alpine
# WORKDIR /app
# # Copiamos solo el JAR generado en la etapa anterior
# COPY --from=build /app/target/*.jar app.jar

# # Configuración vital para Render Free Tier:
# # Limitamos la memoria de Java a 400MB (dejando 112MB para el sistema operativo del contenedor)
# ENV JAVA_OPTS="-Xmx400m"

# EXPOSE 8080
# ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Etapa 1: Construcción
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (Optimizada para plan gratis)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENV JAVA_OPTS="-Xmx400m"
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]