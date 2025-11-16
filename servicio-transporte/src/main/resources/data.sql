-- TIPOS DE TRAMO
MERGE INTO tramo_tipo (id, nombre) KEY(id) VALUES (1, 'origen-deposito');
MERGE INTO tramo_tipo (id, nombre) KEY(id) VALUES (2, 'deposito-deposito');
MERGE INTO tramo_tipo (id, nombre) KEY(id) VALUES (3, 'deposito-destino');
MERGE INTO tramo_tipo (id, nombre) KEY(id) VALUES (4, 'origen-destino');

-- ESTADOS DE TRAMO
MERGE INTO tramo_estado (id, nombre) KEY(id) VALUES (1, 'estimado');
MERGE INTO tramo_estado (id, nombre) KEY(id) VALUES (2, 'asignado');
MERGE INTO tramo_estado (id, nombre) KEY(id) VALUES (3, 'iniciado');
MERGE INTO tramo_estado (id, nombre) KEY(id) VALUES (4, 'finalizado');

-- ===============================================
-- TRANSPORTISTAS Y CAMIONES
-- ===============================================

MERGE INTO transportistas (id, id_keycloak, nombre, telefono, direccion) KEY(id)
VALUES (1, 'keycloak-1', 'Transporte Veloz SRL', '3512223344', 'Av. Logística 123, Córdoba');

MERGE INTO transportistas (id, id_keycloak, nombre, telefono, direccion) KEY(id)
VALUES (2, 'keycloak-2', 'Rutas del Sur SA', '3515556677', 'Ruta 9 km 20, Rosario');

-- CAMIONES
MERGE INTO camiones (dominio, id_transportista, capacidad_peso_kg, capacidad_volumen_m3, disponibilidad, costo_km, consumo_combustible_km) KEY(dominio)
VALUES ('AAA111', 1, 8000, 25, TRUE, 180.0, 0.32);

MERGE INTO camiones (dominio, id_transportista, capacidad_peso_kg, capacidad_volumen_m3, disponibilidad, costo_km, consumo_combustible_km) KEY(dominio)
VALUES ('BBB222', 1, 12000, 35, TRUE, 220.0, 0.40);

MERGE INTO camiones (dominio, id_transportista, capacidad_peso_kg, capacidad_volumen_m3, disponibilidad, costo_km, consumo_combustible_km) KEY(dominio)
VALUES ('CCC333', 2, 6000, 20, TRUE, 150.0, 0.30);

-- ===============================================
-- RUTAS (cada tramo pertenece a una)
-- ===============================================
MERGE INTO rutas (id, solicitud_id, cantidad_tramos, cantidad_depositos) KEY(id)
VALUES (1, 100, 2, 1);

MERGE INTO rutas (id, solicitud_id, cantidad_tramos, cantidad_depositos) KEY(id)
VALUES (2, 101, 1, 0);

-- ===============================================
-- TRAMOS (sin camión asignado aún)
-- ===============================================
-- Tramo 1: pendiente (estado 1 = estimado)
MERGE INTO tramos (id, ruta_id, origen_id, destino_id, tipo_id, estado_id)
KEY(id)
VALUES (1, 1, 10, 20, 1, 1);

-- Tramo 2: pendiente también
MERGE INTO tramos (id, ruta_id, origen_id, destino_id, tipo_id, estado_id)
KEY(id)
VALUES (2, 1, 20, 30, 2, 1);