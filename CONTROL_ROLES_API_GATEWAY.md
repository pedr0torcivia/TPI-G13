# 🔐 Control de Roles - API Gateway

## 📊 Matriz de Permisos por Rol

### 👤 CLIENTE

**Permisos:**
- ✅ Registrar un pedido de traslado de contenedor
- ✅ Consultar estado actual de su contenedor (seguimiento)
- ✅ Ver costo y tiempo estimado de entrega

**Endpoints Permitidos:**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/contenedores` | Listar contenedores |
| GET | `/api/contenedores/{id}` | Obtener detalle de contenedor |
| GET | `/api/solicitudes` | Ver sus solicitudes |
| **POST** | **`/api/solicitudes`** | **Crear solicitud (REQUERIDO)** |
| GET | `/api/clientes` | Consultar información de clientes |
| GET | `/api/ciudades` | Ver ciudades disponibles |
| GET | `/api/depositos` | Ver depósitos |
| GET | `/api/provincias` | Ver provincias |

---

### 👨‍💼 OPERADOR

**Permisos:**
- ✅ Cargar y actualizar ciudades, depósitos, tarifas, camiones, contenedores
- ✅ Asignar camiones a tramos de traslado
- ✅ Modificar parámetros de tarifación
- ✅ Gestionar transportistas

**Endpoints Permitidos:**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/tarifas` | Listar tarifas |
| GET | `/api/tarifas/{id}` | Obtener tarifa |
| **POST** | **`/api/tarifas`** | **Crear tarifa** |
| **PUT** | **`/api/tarifas/{id}`** | **Actualizar tarifa** |
| **DELETE** | **`/api/tarifas/{id}`** | **Eliminar tarifa** |
| GET | `/api/transportistas` | Listar transportistas |
| **POST** | **`/api/transportistas`** | **Crear transportista** |
| **PUT** | **`/api/transportistas/{id}`** | **Actualizar transportista** |
| **DELETE** | **`/api/transportistas/{id}`** | **Eliminar transportista** |
| **POST** | **`/api/rutas`** | **Crear ruta** |
| **PUT** | **`/api/rutas/{id}`** | **Actualizar ruta** |
| **DELETE** | **`/api/rutas/{id}`** | **Eliminar ruta** |
| GET | `/api/rutas` | Listar rutas |
| GET | `/api/camiones` | Listar camiones |
| GET | `/api/ciudades` | Ver/Actualizar ciudades |
| GET | `/api/depositos` | Ver/Actualizar depósitos |
| **PUT** | **`/api/solicitudes/{id}`** | **Modificar solicitud** |
| **DELETE** | **`/api/solicitudes/{id}`** | **Eliminar solicitud** |
| GET | `/api/solicitudes` | Ver solicitudes |

---

### 🚚 TRANSPORTISTA

**Permisos:**
- ✅ Ver tramos asignados
- ✅ Registrar inicio de tramo
- ✅ Registrar fin de tramo

**Endpoints Permitidos:**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/tramos` | Listar tramos asignados |
| GET | `/api/tramos/{id}` | Ver detalle del tramo |
| **POST** | **`/api/tramos`** | **Registrar inicio de tramo** |
| **PUT** | **`/api/tramos/{id}`** | **Registrar fin de tramo** |
| GET | `/api/contenedores` | Consultar contenedores |
| GET | `/api/rutas` | Ver rutas |

---

## 🔒 Validación de Seguridad

### Flujo de Autenticación

1. **Login en Keycloak** → Obtener JWT Token
2. **Token incluye roles:** `CLIENTE`, `TRANSPORTISTA`, `OPERADOR`
3. **API Gateway valida:** Cada request va contra la matriz de permisos
4. **Respuesta:**
   - ✅ 200-201: Autorizado
   - ❌ 403: Forbidden (rol insuficiente)
   - ❌ 401: Unauthorized (sin token)

### Headers Requeridos

```
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

---

## 🧪 Ejemplos de Requests

### 1. CLIENTE Crear Solicitud ✅

```bash
POST http://localhost:8080/api/solicitudes
Authorization: Bearer {token_cliente}
Content-Type: application/json

{
  "clienteId": 1,
  "descripcion": "Traslado de contenedores"
}
```

**Respuesta esperada:** 201 Created

---

### 2. OPERADOR Crear Tarifa ✅

```bash
POST http://localhost:8080/api/tarifas
Authorization: Bearer {token_operador}
Content-Type: application/json

{
  "valorKm": 50.0,
  "valorLitroCombustible": 280.0
}
```

**Respuesta esperada:** 201 Created

---

### 3. TRANSPORTISTA Ver Tramos ✅

```bash
GET http://localhost:8080/api/tramos
Authorization: Bearer {token_transportista}
```

**Respuesta esperada:** 200 OK - Array de tramos

---

### 4. CLIENTE Intentar Crear Tarifa ❌

```bash
POST http://localhost:8080/api/tarifas
Authorization: Bearer {token_cliente}
Content-Type: application/json

{
  "valorKm": 50.0
}
```

**Respuesta esperada:** 403 Forbidden
```json
{
  "error": "Access Denied",
  "message": "User does not have required role: OPERADOR"
}
```

---

## 🔑 Resumen de Roles

| Rol | Funcionalidad Principal | Endpoints Claves |
|-----|------------------------|-------------------|
| **CLIENTE** | Crear y seguir solicitudes | POST `/api/solicitudes` |
| **OPERADOR** | Gestión y administración | GET/POST/PUT/DELETE `/api/tarifas`, `/api/transportistas` |
| **TRANSPORTISTA** | Gestión de traslados | GET/PUT `/api/tramos` |

---

## ⚙️ Configuración en API Gateway

El archivo `SecurityConfig.java` contiene:

- ✅ Validación de JWT
- ✅ Mapeo de roles desde Keycloak
- ✅ Matriz de permisos por endpoint
- ✅ Manejo de CORS
- ✅ Responses 401/403 automáticos

**Ubicación:** `api-gateway/src/main/java/com/backend/tpi_backend/api_gateway/config/SecurityConfig.java`

---

## 🚀 Compilación e Implementación

```bash
# Compilar API Gateway
cd api-gateway
./mvnw clean package -DskipTests

# Levantar todo con Docker
cd ..
docker-compose up -d --build
```

El API Gateway valida automáticamente roles en cada request.

---

## 📝 Notas Importantes

1. **Los roles vienen del JWT** - Keycloak los genera al autenticarse
2. **Validación en Gateway** - No en cada microservicio (patrón correctamente implementado)
3. **CORS habilitado** - Para frontends en localhost:3000, 4200, 8080
4. **Sin autenticación** - Solo GET en recursos públicos (clientes, contenedores, ciudades, etc.)
