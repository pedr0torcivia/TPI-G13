# 🐳 Docker Setup - Levantar Servicios

## 📋 Contenedores Incluidos

El `docker-compose.yml` levanta los siguientes servicios con un **único comando**:

1. **Eureka Server** - Service Discovery (Puerto 18761)
2. **Keycloak** - Identity & Access Management (Puerto 8181)
3. **API Gateway** - Spring Cloud Gateway (Puerto 8080)
4. **Servicio Contenedores** - CRUD contenedores (Puerto 8081)
5. **Servicio Transporte** - Rutas y transportistas (Puerto 8082)
6. **Servicio Tarifa** - Gestión de tarifas (Puerto 8084)
7. **Servicio Depósito** - Depósitos y ubicaciones (Puerto 8085)

## 🚀 Comando para Levantar TODO

```powershell
cd "c:\Users\Lenovo\Desktop\tpi\tpi"
docker-compose up -d --build
```

**Explicación:**
- `up` - Levanta los contenedores
- `-d` - Modo detachado (background)
- `--build` - Construye las imágenes si es necesario

## ✅ Verificar que todos los servicios están corriendo

```powershell
docker-compose ps
```

Deberías ver 7 contenedores con estado "Up".

## 🔴 Detener todos los servicios

```powershell
docker-compose down
```

## 📊 Ver logs de un servicio específico

```powershell
docker-compose logs -f api-gateway
docker-compose logs -f eureka
docker-compose logs -f tpi-keycloak
```

## 🌐 Accesos Rápidos

| Servicio | URL |
|----------|-----|
| **API Gateway** | http://localhost:8080 |
| **Eureka** | http://localhost:18761 |
| **Keycloak** | http://localhost:8181 |
| **Servicio Contenedores** | http://localhost:8081 |
| **Servicio Transporte** | http://localhost:8082 |
| **Servicio Tarifa** | http://localhost:8084 |
| **Servicio Depósito** | http://localhost:8085 |

## 🔑 Credenciales Keycloak (Predeterminadas)

```
Admin User: admin
Admin Password: admin123
```

Acceder a: http://localhost:8181/admin

## 🧪 Pruebas con Postman

### 1. Importar la Colección

- Abre Postman
- Click en **Import**
- Selecciona el archivo: `TPI_Postman_Collection.json`

### 2. Ejecutar Tests

La colección incluye:

#### 🔐 **Autenticación (Ejecutar primero)**
- `Login - Cliente` - Obtiene token para rol CLIENTE
- `Login - Transportista` - Obtiene token para rol TRANSPORTISTA
- `Login - Operador` - Obtiene token para rol OPERADOR

#### ✅ **Endpoints sin Autenticación**
- GET `/api/clientes`
- GET `/api/contenedores`
- GET `/api/provincias`
- GET `/api/ciudades`
- GET `/api/depositos`
- GET `/api/ubicaciones`
- GET `/api/camiones`
- GET `/api/rutas`

#### 🔐 **Endpoints con Validación de Roles**

**Solo OPERADOR:**
- GET `/api/tarifas`
- POST `/api/tarifas`
- PUT `/api/tarifas/{id}`
- DELETE `/api/tarifas/{id}`
- GET `/api/transportistas`

**Solo CLIENTE:**
- POST `/api/solicitudes`

**TRANSPORTISTA:**
- GET `/api/tramos`

**Autenticado (cualquier rol):**
- GET `/api/tarifas/valor-combustible`

## 🔄 Flujo de Pruebas Recomendado

1. **Ejecuta primero las 3 requests de Autenticación** para obtener tokens
2. **Prueba endpoints sin autenticación** (deben responder con éxito)
3. **Prueba endpoints protegidos** con los tokens correspondientes
4. **Verifica errores 403** cuando intentes acceder con rol incorrecto

## 📝 Configuración de Seguridad

### API Gateway
```yml
application.yml:
  eureka.client.service-url.defaultZone: http://eureka:8761/eureka/
  spring.security.oauth2.resourceserver.jwt.issuer-uri: http://keycloak:8080/realms/tpi-realm
  spring.security.oauth2.resourceserver.jwt.jwk-set-uri: http://keycloak:8080/realms/tpi-realm/protocol/openid-connect/certs
```

### Microservicios
Todos apuntan a Keycloak dentro de la red de Docker para validar JWT.

## ⚠️ Notas Importantes

1. **Todos los contenedores usan la red `tpi-net`** para comunicarse internamente por nombres (no IPs)
2. **API Gateway actúa como proxy** - las peticiones llegan al puerto 8080 y se enrutan a los microservicios
3. **Keycloak usa volumen `keycloak_data`** para persistir datos
4. **H2 en memoria** - base de datos en cada microservicio (resetea cuando se reinicia)
5. **OSRM (opcional)** - actualmente deshabilitado, requiere archivo de datos

## 🆘 Troubleshooting

### Los servicios tardan mucho en iniciar
Espera 1-2 minutos. Los servicios Java necesitan tiempo para arrancar y registrarse en Eureka.

### Login falla
Verifica que Keycloak esté corriendo: `docker logs tpi-keycloak`

### Acceso denegado (403) en endpoints protegidos
Verifica que:
1. El token sea válido (aún no expirado)
2. El rol del usuario coincida con lo requerido
3. El header Authorization sea: `Bearer {token}`

### Contenedor no inicia
```powershell
docker-compose logs servicio-nombre
```

## 📚 Documentación Adicional

- Spring Cloud Gateway: https://spring.io/projects/spring-cloud-gateway
- Keycloak: https://www.keycloak.org/documentation
- Eureka: https://github.com/Netflix/eureka
- Spring Security: https://spring.io/projects/spring-security
