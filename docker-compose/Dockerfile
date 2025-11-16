FROM eclipse-temurin:21-jdk AS build

LABEL org.opencontainers.image.authors="gustavoprasca@hotmail.com"

# Instalar Maven
RUN apt-get update && \
    apt-get install -y maven ca-certificates && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* && \
    update-ca-certificates

WORKDIR /app/test-product-microservice-h2

# Copiar solo pom.xml para cachear dependencias
COPY ../pom.xml .

# Descargar todas las dependencias (se cachea si pom.xml no cambia)
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY ../src ./src

# Compilar usando dependencias ya descargadas
RUN mvn clean package -DskipTests -B


FROM eclipse-temurin:21-jre-alpine

# Variable de entorno para puerto
ENV SERVER_PORT=

# Crear usuario no-root para seguridad
RUN addgroup -g 1001 -S product-user && \
    adduser -S -D -H -u 1001 -s /sbin/nologin -G product-user product-user

WORKDIR /home/test-product-microservice-h2

# Copiar JAR y cambiar permisos
COPY --from=build /app/test-product-microservice-h2/target/*.jar ./test-product-microservice-h2.jar
RUN chown product-user:product-user test-product-microservice-h2.jar

# Cambiar a usuario no-root
USER product-user

# Exponer el puerto configurado
EXPOSE ${SERVER_PORT}

ENTRYPOINT ["java","-jar","./test-product-microservice-h2.jar"]