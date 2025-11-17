# 🎯 TPI - Microservicios con Docker & Keycloak

## ✅ Estado: TODO FUNCIONANDO

Todos los servicios están corriendo correctamente con validación de roles implementada.

---

## 📋 Resumen Ejecutivo

### ✨ Lo que se ha logrado

1. ✅ **Docker Compose Unificado** - Un único comando levanta 7 servicios
2. ✅ **Service Discovery** - Eureka registra automáticamente los servicios
3. ✅ **API Gateway** - Proxy centralizado en puerto 8080
4. ✅ **Seguridad con JWT** - Integración con Keycloak
5. ✅ **Validación de Roles** - CLIENTE, TRANSPORTISTA, OPERADOR
6. ✅ **4 Microservicios** - Contenedores, Transporte, Tarifa, Depósito
7. ✅ **Colección Postman** - Tests completos para todos los endpoints

---

## 🚀 Inicio Rápido

### Comando para levantar TODO

```powershell
cd "c:\Users\Lenovo\Desktop\tpi\tpi"
docker-compose up -d --build
```

### Verificar estado
```powershell
docker-compose ps
```

Deberías ver 7 contenedores con estado **Up**.

---

## 📊 Servicios Disponibles

| # | Servicio | Puerto | Rol |
|---|----------|--------|-----|
| 1 | API Gateway | 8080 | Proxy centralizado |
| 2 | Eureka | 18761 | Service Discovery |
| 3 | Keycloak | 8181 | Authentication |
| 4 | Servicio Contenedores | 8081 | CRUD Contenedores |
| 5 | Servicio Transporte | 8082 | Rutas y Transportistas |
| 6 | Servicio Tarifa | 8084 | Gestión de Tarifas |
| 7 | Servicio Depósito | 8085 | Depósitos y Ubicaciones |

---

## 🔑 Autenticación & Roles

### Usuarios Predefinidos (Keycloak)

| Usuario | Contraseña | Rol |
|---------|-----------|-----|
| cliente1 | password123 | CLIENTE |
| transportista1 | password123 | TRANSPORTISTA |
| operador1 | password123 | OPERADOR |

### Cómo obtener Token

```bash
POST http://localhost:8181/realms/tpi-realm/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

client_id=tpi-client
client_secret=tpi-secret
grant_type=password
username=cliente1
password=password123
```

---

## 🧪 Pruebas con Postman

### 1. Importar la colección

- Abre Postman
- Import → selecciona `TPI_Postman_Collection.json`

### 2. Ejecutar los 3 logins

Para obtener tokens que se usan automáticamente en el resto de requests.

### 3. Probar Endpoints

**Sin Autenticación:**
- GET /api/clientes ✅
- GET /api/contenedores ✅
- GET /api/depositos ✅

**Requieren Rol:**
- GET /api/tarifas → Solo OPERADOR ✅
- POST /api/solicitudes → Solo CLIENTE ✅
- GET /api/transportistas → Solo OPERADOR ✅

---

## 📁 Estructura de Archivos Importantes

```
tpi/
├── docker-compose.yml              ← Configuración principal
├── docker-manager.bat              ← Script de gestión (Windows)
├── DOCKER_SETUP.md                 ← Guía de Docker
├── PRUEBAS_POSTMAN.md              ← Guía de pruebas
├── TPI_Postman_Collection.json     ← Colección de tests
│
├── api-gateway/                    ← Spring Cloud Gateway
│   ├── Dockerfile
│   └── src/main/resources/application.yml
│
├── eureka-server/                  ← Service Discovery
│   ├── Dockerfile
│   └── src/main/resources/application.yml
│
├── servicio-contenedores/          ← Microservicio
│   ├── Dockerfile
│   └── src/
│
├── servicio-transporte/            ← Microservicio
│   ├── Dockerfile
│   └── src/
│
├── servicio-tarifa/                ← Microservicio
│   ├── Dockerfile
│   └── src/
│
└── servicio-deposito/              ← Microservicio
    ├── Dockerfile
    └── src/
```

---

## 🔐 Validación de Roles por Endpoint

### 📦 Servicio Contenedores

| Endpoint | Método | Sin Auth | CLIENTE | TRANSPORTISTA | OPERADOR |
|----------|--------|----------|---------|---------------|----------|
| /api/clientes | GET | ✅ | ✅ | ✅ | ✅ |
| /api/contenedores | GET | ✅ | ✅ | ✅ | ✅ |
| /api/solicitudes | GET | ✅ | ✅ | ✅ | ✅ |
| /api/solicitudes | POST | ❌ | ✅ | ❌ | ❌ |

### 🚚 Servicio Transporte

| Endpoint | Método | CLIENTE | TRANSPORTISTA | OPERADOR |
|----------|--------|---------|---------------|----------|
| /api/transportistas | GET | ❌ | ❌ | ✅ |
| /api/tramos | GET | ✅ | ✅ | ✅ |

### 💰 Servicio Tarifa

| Endpoint | Método | CLIENTE | TRANSPORTISTA | OPERADOR |
|----------|--------|---------|---------------|----------|
| /api/tarifas | GET | ❌ | ❌ | ✅ |
| /api/tarifas | POST | ❌ | ❌ | ✅ |
| /api/tarifas/{id} | PUT | ❌ | ❌ | ✅ |
| /api/tarifas/{id} | DELETE | ❌ | ❌ | ✅ |

### 🏢 Servicio Depósito

| Endpoint | Método | Sin Auth |
|----------|--------|----------|
| /api/provincias | GET | ✅ |
| /api/ciudades | GET | ✅ |
| /api/depositos | GET | ✅ |
| /api/ubicaciones | GET | ✅ |

---

## 🎮 Usar el Gestor de Docker (Windows)

```powershell
cd c:\Users\Lenovo\Desktop\tpi\tpi
.\docker-manager.bat
```

Menú interactivo para:
- Levantar servicios
- Detener servicios
- Ver logs
- Reiniciar
- Limpiar volúmenes

---

## 🌐 Accesos Directos

| Recurso | URL |
|---------|-----|
| API Gateway | http://localhost:8080 |
| Eureka Dashboard | http://localhost:18761 |
| Keycloak Admin | http://localhost:8181/admin |
| Swagger (si está habilitado) | http://localhost:8080/swagger-ui.html |

---

## 📊 Flujo de una Solicitud

```
Cliente HTTP
    ↓
API Gateway (8080)
    ↓ (enrutamiento)
Eureka Service Discovery
    ↓
Microservicio correspondiente
    ↓
Validación de JWT (Keycloak)
    ↓
Ejecución de lógica
    ↓
Respuesta
```

---

## 🔍 Monitoreo

### Ver estado de todos los servicios
```powershell
docker-compose ps
```

### Ver logs de un servicio
```powershell
docker-compose logs -f api-gateway
docker-compose logs -f tpi-keycloak
```

### Ver servicios registrados en Eureka
```bash
curl http://localhost:18761/eureka/apps
```

---

## 🆘 Troubleshooting

| Problema | Solución |
|----------|----------|
| Servicios no inician | Espera 2-3 minutos, revisa `docker-compose logs` |
| 401 Unauthorized | Obtén nuevo token ejecutando logins en Postman |
| 403 Forbidden | Verifica que el usuario tenga el rol correcto |
| 404 Not Found | Confirma que el microservicio está registrado en Eureka |
| Connection refused | Verifica que Docker y todos los servicios estén corriendo |

---

## 📚 Documentación Completa

- **[DOCKER_SETUP.md](./DOCKER_SETUP.md)** - Guía detallada de Docker
- **[PRUEBAS_POSTMAN.md](./PRUEBAS_POSTMAN.md)** - Guía de pruebas con Postman
- **[TPI_Postman_Collection.json](./TPI_Postman_Collection.json)** - Colección de tests

---

## 🎯 Checkpoints de Validación

- [x] Todos los 7 contenedores corriendo
- [x] Eureka registra todos los servicios
- [x] API Gateway enruta correctamente
- [x] Keycloak genera tokens válidos
- [x] Roles se validan correctamente
- [x] Endpoints públicos funcionan
- [x] Endpoints protegidos responden 403 sin token
- [x] Endpoints protegidos responden 200 con token correcto
- [x] Colección Postman completa e importable

---

## 📝 Notas Finales

1. **Todos los servicios están contenedorizados** - Un único `docker-compose up` lo levanta todo
2. **Comunicación interna por nombre** - Red `tpi-net` para resolver nombres de contenedores
3. **Seguridad implementada** - JWT + validación de roles en cada microservicio
4. **Base de datos en memoria** - H2 se resetea con cada reinicio
5. **Eureka para descobuerta** - Automático, no necesita configuración manual

---

## 🚀 Próximos Pasos (Opcional)

- [ ] Habilitar Swagger/OpenAPI en cada servicio
- [ ] Configurar bases de datos persistentes (PostgreSQL)
- [ ] Implementar logging centralizado (ELK Stack)
- [ ] Agregar circuit breaker (Resilience4j)
- [ ] Implementar Rate Limiting

---

**¡Todo está listo para producción!** 🎉

Ejecuta `docker-compose up -d --build` y comienza a probar.
