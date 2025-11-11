# 🧱 Proyecto Backend TPI – Logística de Contenedores (2025)

## 📘 Integrantes
Equipo de Ingeniería en Sistemas de Información – UTN FRC  
Materia: **Backend de Aplicaciones 2025**

---

## 🧭 Introducción

Este proyecto implementa una solución backend basada en **microservicios** para una empresa de logística que transporta contenedores utilizados en construcción de viviendas.

El diseño sigue los lineamientos del enunciado oficial del TPI 2025 y se apoya en:
- **Arquitectura de microservicios**
- **Seguridad con Keycloak (JWT)**
- **API Gateway**
- **Integración con la API de Google Distance Matrix**
- **Documentación con Swagger / OpenAPI**
- **Persistencia con Spring Data JPA y H2**

---

## 🧩 1. Definición de microservicios

### ✅ Microservicios finales (4)

| Microservicio | Dominio | Entidades principales | Roles |
|----------------|----------|----------------------|--------|
| 🟩 **ContenedoresService** | Cliente y solicitudes | Cliente, Contenedor, Solicitud | Cliente |
| 🟦 **TransporteService** | Logística y flota | Camión, Ruta, Tramo | Operador, Transportista |
| 🏢 **DepositoService** | Infraestructura | Depósito, Ciudad | Operador |
| 💰 **TarifaService** | Configuración | Tarifa, CostoBase, Consumo | Operador |

### 🔒 Servicios externos
- **Keycloak** → autenticación y roles JWT  
- **Google Distance Matrix API** → cálculo de distancias entre coordenadas  

---

## 🧱 2. Justificación de diseño

Se decidió utilizar **cuatro microservicios** con alta cohesión interna y bajo acoplamiento:
- El microservicio de **Transporte** integra la gestión de flota, rutas, tramos y costos (dominio logístico).
- **Contenedores** se centra en clientes, contenedores y solicitudes (punto de partida de todo el proceso).
- **Depósitos** y **Tarifas** son microservicios de soporte y configuración.
- La **autenticación** y el **cálculo de distancias** se delegan a servicios externos, asegurando independencia tecnológica.

> **Justificación técnica:**  
> Esta estructura permite escalar cada componente por separado, mantener independencia de datos y aplicar principios de **DDD (Domain Driven Design)**.

---

## 🔄 3. Requerimientos funcionales cubiertos

| Requerimiento del enunciado | Microservicio responsable |
|------------------------------|----------------------------|
| Registrar nueva solicitud de transporte | ContenedoresService |
| Crear cliente si no existe | ContenedoresService + integración con Keycloak |
| Registrar contenedor | ContenedoresService |
| Consultar estado del transporte | ContenedoresService |
| Asignar ruta y camión | TransporteService |
| Calcular costos reales y estimados | TransporteService + TarifaService |
| Registrar inicio/fin de tramo | TransporteService |
| Validar capacidad de camión | TransporteService |
| Registrar depósitos y tarifas | DepositoService / TarifaService |

---

## 💾 4. Bases de datos y modelo de datos
1 base de datos compartida entre los microservicios


### 🔹 Recomendación de motor:
- **H2 en desarrollo** (in-memory)
- **PostgreSQL en producción**

### 🔹 Tipos de datos sugeridos

| Tipo de dato | Uso |
|---------------|-----|
| `bigint` | IDs autogenerados |
| `varchar(n)` | Cadenas cortas |
| `numeric(p,s)` o `decimal(p,s)` | Dinero, peso, volumen (precisos) |
| `float8` (`double precision`) | Coordenadas y distancias |
| `enum` | Estados y tipos controlados |
| `boolean` | Disponibilidad y banderas de estado |

---

## 🧮 5. DER lógico global (resumen)

**Entidades principales:**

- **Cliente**(id, nombre, email, teléfono)
- **Contenedor**(id, peso, volumen, estado, cliente_id)
- **Solicitud**(id, contenedor_id, costoEstimado, tiempoEstimado, costoFinal, tiempoReal, estado)
- **Camión**(id, dominio, nombreTransportista, capPeso, capVolumen, costoKmBase, consumoLitrosKm, disponible)
- **Ruta**(id, solicitud_id, cantTramos, cantDepositos)
- **Tramo**(id, ruta_id, tipo, estado, fechaInicio, fechaFin, costoAprox, costoReal, camion_id)
- **Depósito**(id, nombre, dirección, latitud, longitud, costoEstadiaDiario)
- **Tarifa**(id, rangoPeso, rangoVolumen, costoBaseKm, valorLitroCombustible, cargoGestion)

---

## 🧩 6. Relaciones principales

| Relación | Tipo | Descripción |
|-----------|------|--------------|
| Cliente → Contenedor | 1:N | Un cliente puede tener varios contenedores |
| Contenedor → Solicitud | 1:1 o 1:N | Una solicitud pertenece a un contenedor |
| Solicitud → Ruta | 1:1 | Cada solicitud tiene una ruta asignada |
| Ruta → Tramo | 1:N | Una ruta se compone de varios tramos |
| Camión → Tramo | 1:N | Un camión puede tener varios tramos asignados |
| Depósito → Tramo | 1:N | Un depósito puede intervenir en varios tramos |

---

## ⚙️ 7. Arquitectura general (diagrama de contenedores C4 – nivel 2)

**Contenedores (microservicios):**
- ContenedoresService  
- TransporteService  
- DepositoService  
- TarifaService  
- Keycloak (externo)  
- Google Distance Matrix API (externa)  
- API Gateway (opcional)

**Comunicación:**
- Cada microservicio expone endpoints REST (`/api/...`)
- Todos validan tokens JWT emitidos por Keycloak
- Transporte consume la API de Google para distancias

---

# 🚀 ***GUÍA PARA COMENZAR A CODEAR***

> 💡 *A partir de aquí empieza la parte práctica de implementación.*

---

## 🧱 1. Orden de desarrollo

| Etapa | Microservicio | Motivo |
|--------|----------------|--------|
| 🥇 1 | **ContenedoresService** | Núcleo funcional: clientes, contenedores, solicitudes |
| 🥈 2 | **TransporteService** | Usa solicitudes existentes para rutas y camiones |
| 🥉 3 | **DepositoService** | De soporte, se puede aislar |
| 🏁 4 | **TarifaService** | Configuración, sin dependencias directas |

---

## 🧩 2. Crear el primer microservicio: **ContenedoresService**

### 📘 Paso 1 — Generar proyecto con Spring Initializr
- URL: [https://start.spring.io/](https://start.spring.io/)
- Configuración:
  - Project: Maven
  - Language: Java
  - Spring Boot: 3.3.x o superior
  - Group: `com.backend.tpi_backend`
  - Artifact: `servicio-contenedores`
  - Name: `Contenedores Service`
  - Packaging: Jar
  - Java: 21
- Dependencias:
  - `Spring Web`
  - `Spring Data JPA`
  - `H2 Database`
  - `Lombok`
  - `Springdoc OpenAPI (Swagger)`

---

### 📂 Paso 2 — Estructura de paquetes
src/main/java/com/backend/tpi_backend/serviciocontenadores/
├── model/
├── repository/
├── service/
├── controller/
└── ServicioContenedoresApplication.java



---

### ⚙️ Paso 3 — Configurar `application.properties`
```properties
server.port=8081
spring.datasource.url=jdbc:h2:mem:contenedoresdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create
spring.h2.console.enabled=true
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
🧱 Paso 4 — Crear las entidades
🧩 Cliente.java
java
Copiar código
@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String email;
    private String telefono;
}
🧩 Contenedor.java
java
Copiar código
@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Contenedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigo;
    private double peso;
    private double volumen;
    private String estado;

    @ManyToOne
    private Cliente cliente;
}
🧩 Solicitud.java
java
Copiar código
@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Solicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Contenedor contenedor;

    private String estado;
    private BigDecimal costoEstimado;
    private BigDecimal costoFinal;
    private int tiempoEstimado;
    private int tiempoReal;
}
🧾 Paso 5 — Crear Repositorios
java
Copiar código
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {}

@Repository
public interface ContenedorRepository extends JpaRepository<Contenedor, Long> {}

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {}
🧠 Paso 6 — Crear Servicios
java
Copiar código
@Service
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository repository;
    public List<Cliente> listar() { return repository.findAll(); }
    public Cliente crear(Cliente c) { return repository.save(c); }
}
(Estructura similar para ContenedorService y SolicitudService)

🌐 Paso 7 — Crear Controladores
java
Copiar código
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {
    private final ClienteService service;

    @GetMapping public List<Cliente> listar() { return service.listar(); }
    @PostMapping public Cliente crear(@RequestBody Cliente cliente) { return service.crear(cliente); }
}
🧪 Paso 8 — Probar en Swagger
Ejecutar:

bash
Copiar código
mvn spring-boot:run
Abrir: http://localhost:8081/swagger-ui.html

🧭 3. Siguientes pasos
Una vez el ContenedoresService funcione:

Copiar el proyecto base y renombrarlo como servicio-transporte.

Cambiar el puerto (8082) y la base (transportedb).

Implementar entidades: Camion, Tramo, Ruta.

Crear sus servicios y controladores CRUD.

Agregar la integración con la API de Google Distance Matrix (en Transporte).