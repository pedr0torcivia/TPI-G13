# 🧪 Guía de Pruebas - Postman con Validación de Roles

## 📊 Estado de los Servicios

Todos los servicios están corriendo con éxito:

| Servicio | Puerto | Estado | URL |
|----------|--------|--------|-----|
| API Gateway | 8080 | ✅ Up | http://localhost:8080 |
| Eureka | 18761 | ✅ Up | http://localhost:18761 |
| Keycloak | 8181 | ✅ Up | http://localhost:8181 |
| Servicio Contenedores | 8081 | ✅ Up | http://localhost:8081 |
| Servicio Transporte | 8082 | ✅ Up | http://localhost:8082 |
| Servicio Tarifa | 8084 | ✅ Up | http://localhost:8084 |
| Servicio Depósito | 8085 | ✅ Up | http://localhost:8085 |

## 🚀 Cómo Usar la Colección de Postman

### Paso 1: Importar la Colección

1. Abre **Postman**
2. Haz click en **Import** (parte superior izquierda)
3. Selecciona el archivo `TPI_Postman_Collection.json` del proyecto
4. La colección se importará automáticamente

### Paso 2: Ejecutar Autenticación (IMPORTANTE - Hacer primero)

Antes de probar cualquier endpoint, debes obtener los tokens. Ejecuta en este orden:

1. **🔑 Autenticación > Login - Cliente**
   - Obtiene token con rol `CLIENTE`
   - Se guarda automáticamente en `{{token_cliente}}`

2. **🔑 Autenticación > Login - Transportista**
   - Obtiene token con rol `TRANSPORTISTA`
   - Se guarda automáticamente en `{{token_transportista}}`

3. **🔑 Autenticación > Login - Operador**
   - Obtiene token con rol `OPERADOR`
   - Se guarda automáticamente en `{{token_operador}}`

**Verificación:** En Postman, ve a **Variables** y confirma que los 3 tokens están poblados.

### Paso 3: Probar Endpoints

#### ✅ Endpoints SIN Autenticación (Deben funcionar)

```
GET /api/clientes              → 200 OK
GET /api/contenedores          → 200 OK
GET /api/provincias            → 200 OK
GET /api/ciudades              → 200 OK
GET /api/depositos             → 200 OK
GET /api/ubicaciones           → 200 OK
GET /api/camiones              → 200 OK
GET /api/rutas                 → 200 OK
GET /api/solicitudes           → 200 OK
```

#### 🔐 Endpoints CON Validación de Roles

**Solo OPERADOR (403 si no eres OPERADOR):**
```
GET  /api/tarifas              → 200 (OPERADOR)
GET  /api/tarifas/1            → 200 (OPERADOR)
POST /api/tarifas              → 201 (OPERADOR)
PUT  /api/tarifas/1            → 200 (OPERADOR)
DELETE /api/tarifas/1          → 204 (OPERADOR)
GET  /api/transportistas       → 200 (OPERADOR)
```

**Solo CLIENTE:**
```
POST /api/solicitudes          → 201 (CLIENTE)
```

**Solo TRANSPORTISTA:**
```
GET  /api/tramos               → 200 (TRANSPORTISTA)
```

**Cualquier usuario autenticado:**
```
GET  /api/tarifas/valor-combustible  → 200 (cualquier rol)
```

## 🧪 Casos de Prueba Detallados

### Test 1: GET /api/clientes (Sin Autenticación)

```
✅ ESPERADO: HTTP 200 - Retorna lista de clientes
NO NECESITA: Authorization header
```

### Test 2: GET /api/tarifas (Requiere OPERADOR)

**Con token OPERADOR:**
```
✅ ESPERADO: HTTP 200 - Retorna lista de tarifas
HEADER: Authorization: Bearer {{token_operador}}
```

**Con token CLIENTE:**
```
❌ ESPERADO: HTTP 403 - Forbidden (no tiene permiso)
HEADER: Authorization: Bearer {{token_cliente}}
```

**Sin token:**
```
❌ ESPERADO: HTTP 401 - Unauthorized
SIN HEADER Authorization
```

### Test 3: POST /api/solicitudes (Solo CLIENTE)

**Con token CLIENTE:**
```
✅ ESPERADO: HTTP 201 - Solicitud creada
HEADER: Authorization: Bearer {{token_cliente}}
BODY: {
  "clienteId": 1,
  "descripcion": "Solicitud de transporte"
}
```

**Con token OPERADOR:**
```
❌ ESPERADO: HTTP 403 - Forbidden
HEADER: Authorization: Bearer {{token_operador}}
```

### Test 4: GET /api/transportistas (Solo OPERADOR)

**Con token OPERADOR:**
```
✅ ESPERADO: HTTP 200 - Retorna lista de transportistas
HEADER: Authorization: Bearer {{token_operador}}
```

**Con token CLIENTE:**
```
❌ ESPERADO: HTTP 403 - Forbidden
HEADER: Authorization: Bearer {{token_cliente}}
```

## 🔄 Flujo Completo de Pruebas (Recomendado)

### Fase 1: Autenticación (5 minutos)
1. Ejecuta los 3 logins en orden
2. Verifica que los tokens se guardaron en Variables

### Fase 2: Endpoints Públicos (5 minutos)
1. Prueba todos los GET sin Authorization
2. Verifica que respondan con 200 OK

### Fase 3: Validación de Roles (10 minutos)
1. Prueba cada endpoint protegido con el rol CORRECTO
   - Debe ser 200/201 ✅
2. Prueba con roles INCORRECTOS
   - Debe ser 403 ❌
3. Prueba SIN autenticación
   - Debe ser 401 ❌

### Fase 4: Flujo de Negocio (10 minutos)
1. CLIENTE crea una solicitud
2. OPERADOR lista las solicitudes
3. OPERADOR gestiona tarifas

## 📊 Matriz de Permisos Esperados

| Endpoint | GET | POST | PUT | DELETE | CLIENTE | TRANSPORTISTA | OPERADOR |
|----------|-----|------|-----|--------|---------|---------------|----------|
| /api/clientes | 200 | 200 | - | - | ✅ | ✅ | ✅ |
| /api/contenedores | 200 | - | - | - | ✅ | ✅ | ✅ |
| /api/solicitudes | 200 | 201* | - | 204** | ✅ | ✅ | ✅ |
| /api/tarifas | 200*** | 201*** | 200*** | 204*** | ❌ | ❌ | ✅ |
| /api/transportistas | 200*** | - | - | - | ❌ | ❌ | ✅ |
| /api/tramos | 200 | - | - | - | ✅ | ✅ | ✅ |

**Leyenda:**
- ✅ = Tiene permiso
- ❌ = SIN permiso (espera 403)
- \* Solo CLIENTE
- \** Solo OPERADOR
- \*** Requiere rol OPERADOR

## 🆘 Solución de Problemas

### Error: "Unexpected token < in JSON"
**Causa:** Keycloak no está respondiendo  
**Solución:** Verifica que Keycloak esté corriendo: `docker-compose logs tpi-keycloak`

### Error: 401 Unauthorized
**Causa:** Token ausente o expirado  
**Solución:** Vuelve a ejecutar los logins en la carpeta de Autenticación

### Error: 403 Forbidden
**Causa:** El usuario no tiene el rol requerido  
**Esperado:** Esto es correcto para los tests de validación de roles

### Error: 404 Not Found
**Causa:** El endpoint no existe o el microservicio no está registrado en Eureka  
**Solución:** Verifica que el servicio esté corriendo: `docker ps`

### Error: Connection refused
**Causa:** API Gateway o algún servicio no está disponible  
**Solución:** Espera 1-2 minutos a que todos los servicios inicien

## 📝 Notas Importantes

1. **Tokens expiran** - Si un test falla por expiración, vuelve a ejecutar los logins
2. **Variables de Postman** - Los tokens se guardan en la colección, no necesitas copiarlos manualmente
3. **Transacciones en H2** - La base de datos se resetea cuando se reinician los contenedores
4. **Red interna** - Los servicios se comunican internamente por nombre de contenedor (tpi-net)
5. **API Gateway es proxy** - Todas las peticiones van al puerto 8080

## ✅ Confirmación de Éxito

Si ves esto, todo está funcionando correctamente:

- ✅ 7 contenedores corriendo
- ✅ Eureka registra todos los servicios
- ✅ Tokens se obtienen exitosamente
- ✅ Endpoints públicos responden 200
- ✅ Endpoints protegidos responden 200 con token correcto
- ✅ Endpoints protegidos responden 403 con token incorrecto
- ✅ Endpoints protegidos responden 401 sin token

## 🎯 Conclusión

La arquitectura de microservicios está completamente funcional:
- **Service Discovery** (Eureka) ✅
- **API Gateway** ✅
- **Seguridad con JWT** (Keycloak) ✅
- **Validación de Roles** ✅
- **Todos los microservicios** ✅
