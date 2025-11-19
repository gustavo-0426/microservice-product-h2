# 🚀 Guía de Deployment Profesional - AWS Elastic Beanstalk

## 📋 Arquitectura

```
┌─────────────────────────────────────────────────────────┐
│                    Internet Gateway                      │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│              Application Load Balancer                   │
│                    (Port 80)                             │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│              EC2 Instance (t3.micro)                     │
│  ┌──────────────────────────────────────────────────┐  │
│  │          Docker Container                         │  │
│  │    gustavo0426/microservice-product-h2:3.0.0     │  │
│  │              Port 5000 → 80                       │  │
│  │  ┌────────────────────────────────────────────┐  │  │
│  │  │      Spring Boot Application               │  │  │
│  │  │      - ProductController API               │  │  │
│  │  │      - H2 Database (in-memory)            │  │  │
│  │  │      - Spring Security                     │  │  │
│  │  └────────────────────────────────────────────┘  │  │
│  └──────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│                   CloudWatch Logs                        │
│       /aws/elasticbeanstalk/microservice-product        │
└─────────────────────────────────────────────────────────┘
```

## 🛠️ Estructura del Proyecto

```
microservice-product-h2/
├── Dockerrun.aws.json              ← Configuración AWS nativa
├── .ebextensions/                  ← Configuraciones de EB
│   ├── 00_environment.config       ← Variables de entorno
│   ├── 01_cloudwatch.config        ← CloudWatch Logs
│   ├── 02_healthcheck.config       ← Health checks
│   ├── 03_instance.config          ← Configuración EC2
│   └── 04_monitoring.config        ← Monitoreo avanzado
├── .ebignore                       ← Archivos excluidos del ZIP
├── deploy.ps1                      ← Script de deployment
├── docker/                         ← Docker local (dev)
│   ├── docker-compose.yml
│   ├── Dockerfile
│   └── env.example
├── pom.xml                         ← Maven
└── src/                            ← Código fuente
```

## 🔧 Workflow de Deployment

### 1️⃣ **Build y Push de Docker Image**

```powershell
# Build de la imagen
docker build -t gustavo0426/microservice-product-h2:3.0.0 .

# Login a Docker Hub
docker login

# Push de la imagen
docker push gustavo0426/microservice-product-h2:3.0.0
```

### 2️⃣ **Deployment Automatizado**

```powershell
# Ejecutar el script de deployment
.\deploy.ps1 -Version "3.0.0" -Environment "production" -Region "us-east-1"
```

El script realiza:
- ✅ Valida que la imagen existe en Docker Hub
- ✅ Actualiza `Dockerrun.aws.json` con la versión
- ✅ Crea el ZIP de deployment con todos los archivos necesarios
- ✅ Muestra el contenido del paquete
- ✅ Proporciona instrucciones para el upload manual

### 3️⃣ **Upload a AWS Elastic Beanstalk**

1. Abre la consola de AWS Elastic Beanstalk
2. Selecciona tu aplicación: `microservice-product-test-4`
3. Haz clic en **"Upload and Deploy"**
4. Sube el archivo: `microservice-eb-3.0.0.zip`
5. Version label: `3.0.0`
6. Haz clic en **"Deploy"**

### 4️⃣ **Verificación del Deployment**

```powershell
# Health check
curl http://microservice-product-test-4-env.eba-2i4muemc.us-east-1.elasticbeanstalk.com/actuator/health

# API endpoint
curl http://microservice-product-test-4-env.eba-2i4muemc.us-east-1.elasticbeanstalk.com/v1/microservice/product

# Con autenticación
curl -u gcastro:Admin123! http://microservice-product-test-4-env.eba-2i4muemc.us-east-1.elasticbeanstalk.com/v1/microservice/product
```

## 📊 Configuraciones Profesionales

### **Dockerrun.aws.json**
- AWSEBDockerrunVersion 3 (ECS task definition)
- Container con 512 MB de memoria
- Port mapping: 80 (host) → 5000 (container)
- CloudWatch logs habilitados
- Variables de entorno separadas en `.ebextensions`

### **.ebextensions/00_environment.config**
```yaml
option_settings:
  aws:elasticbeanstalk:application:environment:
    SERVER_PORT: 5000
    DB_NAME: product
    DB_USERNAME: sa
    DB_PASSWORD: ""
```

### **.ebextensions/01_cloudwatch.config**
- Stream de logs habilitado
- Retención: 7 días
- Health streaming habilitado

### **.ebextensions/02_healthcheck.config**
- Path: `/actuator/health`
- Intervalo: 30 segundos
- Timeout: 5 segundos
- Healthy threshold: 3
- Unhealthy threshold: 5

### **.ebextensions/03_instance.config**
- Instance type: `t3.micro`
- Auto Scaling: Min 1, Max 2
- Load Balancer: Application Load Balancer

### **.ebextensions/04_monitoring.config**
- Enhanced health reporting
- CloudWatch metrics
- X-Ray deshabilitado (opcional)

## 🔐 Seguridad

### **Credenciales**
- ❌ **NO** incluir credenciales en `Dockerrun.aws.json`
- ✅ Usar `.ebextensions/00_environment.config` para variables
- ✅ Mejor: AWS Secrets Manager + SDK en el código
- ✅ Mejor aún: AWS Systems Manager Parameter Store

### **Roles IAM**
```
aws-elasticbeanstalk-ec2-role
├── AmazonEC2ContainerRegistryReadOnly
├── AWSElasticBeanstalkWebTier
├── AWSElasticBeanstalkWorkerTier
└── CloudWatchLogsFullAccess
```

## 📈 Monitoreo

### **CloudWatch Logs**
```
Log Group: /aws/elasticbeanstalk/microservice-product
Stream: i-xxxxxxxxx (Instance ID)
```

### **Métricas Importantes**
- CPU Utilization
- Network In/Out
- Request Count
- HTTP 5xx/4xx errors
- Target Response Time

### **Alarmas Recomendadas**
```powershell
# Crear alarma de CPU
aws cloudwatch put-metric-alarm `
  --alarm-name eb-high-cpu `
  --comparison-operator GreaterThanThreshold `
  --evaluation-periods 2 `
  --metric-name CPUUtilization `
  --namespace AWS/EC2 `
  --period 300 `
  --statistic Average `
  --threshold 80.0 `
  --alarm-description "Alarma cuando CPU > 80%" `
  --dimensions Name=InstanceId,Value=i-xxxxxxxxx
```

## 🔄 Rollback

### **Desde la consola**
1. Ir a **Application Versions**
2. Seleccionar versión anterior
3. Clic en **Deploy**

### **Desde AWS CLI**
```powershell
# Listar versiones
aws elasticbeanstalk describe-application-versions `
  --application-name microservice-product-test-4

# Rollback a versión anterior
aws elasticbeanstalk update-environment `
  --environment-name microservice-product-test-4-env `
  --version-label "2.0.0"
```

## 🧪 Testing

### **Local (Docker Compose)**
```powershell
cd docker
docker-compose up
curl http://localhost/v1/microservice/product
```

### **Staging (Elastic Beanstalk)**
```powershell
curl http://microservice-product-test-4-env.eba-2i4muemc.us-east-1.elasticbeanstalk.com/actuator/health
```

### **Production**
```powershell
# Health check
curl http://prod-microservice-product-env.elasticbeanstalk.com/actuator/health

# Smoke test
curl -u gcastro:Admin123! http://prod-microservice-product-env.elasticbeanstalk.com/v1/microservice/product
```

## 📚 Recursos

- [AWS Elastic Beanstalk - Docker](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/docker.html)
- [Dockerrun.aws.json v3](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/create_deploy_docker_v2config.html)
- [.ebextensions Configuration Files](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/ebextensions.html)
- [CloudWatch Logs](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/WhatIsCloudWatchLogs.html)

## 🎯 Checklist de Deployment

- [ ] ✅ Imagen Docker buildeada y pusheada a Docker Hub
- [ ] ✅ `Dockerrun.aws.json` actualizado con la versión correcta
- [ ] ✅ Variables de entorno configuradas en `.ebextensions/00_environment.config`
- [ ] ✅ Health check configurado en `/actuator/health`
- [ ] ✅ CloudWatch Logs habilitados
- [ ] ✅ Security Groups configurados (HTTP 80, SSH 22)
- [ ] ✅ ZIP de deployment creado con `deploy.ps1`
- [ ] ✅ Deployment ejecutado en AWS EB Console
- [ ] ✅ Verificación del endpoint: `curl .../actuator/health`
- [ ] ✅ Smoke test: `curl -u user:pass .../v1/microservice/product`

---

**Autor:** Gustavo Castro  
**Versión:** 3.0.0  
**Fecha:** 2024  
**AWS Region:** us-east-1  
**Docker Hub:** gustavo0426/microservice-product-h2
