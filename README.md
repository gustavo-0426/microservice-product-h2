<br>

# 🚀 microservice-product-h2

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

**Aplicación** para proyecto Spring Boot con conexión a base de datos H2. Estructura mínima lista para desarrollar tu aplicación.

## 📋 Tabla de Contenidos

- [🚀 Características](#características)
- [📋 Requisitos Previos](#requisitos-previos)
- [🐳 Despliegue Local con Docker Compose](#inicio-rapido)
- [☁️ Despliegue en AWS Elastic Beanstalk](#despliegue-aws)
- [📚 API Documentation](#api-documentation)
- [📞 Contacto](#contacto)

---
<br>

## <a id="características"></a>🚀 Características

- ✅ **Aplicación** Spring Boot 3.4.1 + Java 21
- 💾 **Soporte base de datos** H2
- 🐳 **Docker Compose** configurado para orquestación de servicios
- 🔧 **Variables de entorno** para configuración sensible y mantenible
- 📦 **Dockerfile** optimizado con multi-stage build

---
<br>

## <a id="requisitos-previos"></a>📋 Requisitos Previos

- **Spring Boot 3.4.1**
- **Java 21**
- **Maven 3.8+**
- **Docker** y **Docker Compose**
- **Git**

---
<br>

## <a id="inicio-rapido"></a>🐳 Despliegue Local con Docker Compose

### 1️⃣ Variables de Entorno

Crear y configurar el archivo de variables de entorno:
```bash
cp docker-compose/env.example docker-compose/.env
```

### 2️⃣ Ejecutar Aplicación con Docker Compose

#### Construir y ejecutar:

```bash
docker-compose -f docker-compose/compose.yml up -d
```

#### Verificar contenedores activos:
```bash
docker-compose -f docker-compose/compose.yml ps
```

#### Ver logs en tiempo real:
```bash
docker-compose -f docker-compose/compose.yml logs -f
```

---
<br>

## <a id="despliegue-aws"></a>☁️ Despliegue en AWS Elastic Beanstalk

### 1️⃣ Construir y Subir la Imagen Docker

#### Construir la imagen desde la raíz del proyecto:
```bash
docker build -f docker-compose\Dockerfile -t gustavo0426/microservice-product-h2:latest .
```

#### Login a Docker Hub (solo la primera vez):
```bash
docker login
```

#### Subir la imagen a Docker Hub:
```bash
docker push gustavo0426/microservice-product-h2:latest
```

### 2️⃣ Crear el Paquete de Despliegue

#### Comprimir los archivos necesarios:
```bash
Compress-Archive -Path Dockerrun.aws.json,.ebextensions -DestinationPath deploy.zip -Force
```

**Archivos incluidos en `deploy.zip`:**
- `Dockerrun.aws.json` - Configuración del contenedor (imagen, puertos)
- `.ebextensions/01_cloudwatch.config` - Logs de CloudWatch

### 3️⃣ Desplegar en AWS Console

1. Ir a [AWS Elastic Beanstalk Console](https://console.aws.amazon.com/elasticbeanstalk)
2. Click en **"Create application"**
3. **Configure environment:**
   - Environment tier: **Web server environment**
4. **Application information:**
   - Application name: `microservice-product` (nombre de tu app)
5. **Environment information:**
   - Environment name: Se genera automáticamente o personalízalo
6. **Platform:**
   - Platform: **Docker**
   - Platform branch: **Docker running on 64bit Amazon Linux 2023**
   - Platform version: Usar la recomendada
7. **Application code:**
   - Seleccionar: **Upload your code**
   - Version label: `v1.0.0` (o tu versión)
   - Click **"Local file"** -> **"Choose file"** y subir `deploy.zip`
8. Click en **"Next"** hasta llegar a **"Configure updates, monitoring, and logging"**
9. En **Environment properties**, agregar las variables de entorno:
   - `SERVER_PORT` = `5000` (no cambiar, requerido por Elastic Beanstalk)
   - `DB_NAME` = `nombre_base_de_datos`
   - `DB_USERNAME` = `usuario_base_de_datos`
   - `DB_PASSWORD` = `contraseña_base_de_datos`
10. Click en **"Next"** y luego **"Submit"**
11. Esperar 3-5 minutos mientras AWS crea todo automáticamente

### 4️⃣ Verificar el Despliegue

Una vez desplegado, tu aplicación estará disponible en:
```
http://tu-aplicacion.elasticbeanstalk.com
```

**Probar los endpoints:**

#### Health check:
```bash
curl http://tu-aplicacion.elasticbeanstalk.com/actuator/health
```

#### Swagger UI:
```
http://tu-aplicacion.elasticbeanstalk.com/v1/product/swagger-ui/index.html
```

#### API con autenticación:
```bash
curl -u usuario_base_de_datos:contraseña_base_de_datos http://tu-aplicacion.elasticbeanstalk.com/v1/microservice/product
```

> **💡 Tip:** Las variables de entorno se configuran en AWS Console (Configuration → Software → Environment properties). Puedes modificarlas sin reconstruir la imagen Docker.

---
<br>

## <a id="api-documentation"></a>📚 API Documentation

### 📖 Swagger UI

**Entorno Local (Docker Compose):**
- **Swagger UI:** [http://localhost:9092/v1/product/swagger-ui/index.html](http://localhost:9092/v1/product/swagger-ui/index.html)
- **OpenAPI JSON:** [http://localhost:9092/v1/product/api-docs](http://localhost:9092/v1/product/api-docs)

**AWS Elastic Beanstalk:**
- **Swagger UI:** `http://tu-aplicacion.elasticbeanstalk.com/v1/product/swagger-ui/index.html`
- **OpenAPI JSON:** `http://tu-aplicacion.elasticbeanstalk.com/v1/product/api-docs`

### 🗄️ Administración de Base de Datos H2

La aplicación incluye H2 Console para gestionar y visualizar la base de datos en memoria.

#### Acceso a H2 Console

**Entorno Local (Docker Compose):**
- **URL:** [http://localhost:9092/h2-console](http://localhost:9092/h2-console)
- **Configuración:** Los valores se obtienen del archivo `docker-compose/.env`

**AWS Elastic Beanstalk:**
- **URL:** `http://tu-aplicacion.elasticbeanstalk.com/h2-console`
- **Configuración:** Los valores se obtienen de las Environment properties configuradas en AWS Console

#### Configuración de Conexión

| Campo | Valor |
|-------|-------|
| **Driver Class** | `org.h2.Driver` |
| **JDBC URL** | `jdbc:h2:mem:${DB_NAME}` |
| **User Name** | `${DB_USERNAME}` |
| **Password** | `${DB_PASSWORD}` |

> **Nota:** H2 es una base de datos en memoria, los datos se perderán al reiniciar la aplicación.

#### Credenciales de Autenticación de la API

Para probar los endpoints REST protegidos, las credenciales de usuario se encuentran configuradas en el archivo `src/main/resources/import.sql`.

---
<br>

## <a id="contacto"></a>📞 Contacto 


### Gustavo Castro

**Ingeniero de Sistemas**  
**Especialista en Ingeniería de Software**  
**Desarrollador Backend Senior, Spring Boot, Node.js, Arquitectura Cloud (AWS)**  
**GitHub:** [github.com/gustavo-0426](https://github.com/gustavo-0426)  
**LinkedIn:** [linkedin.com/in/gustavo-castro-prasca](https://linkedin.com/in/gustavo-castro-prasca)

---
