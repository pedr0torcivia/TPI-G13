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
-- DATOS DE UBICACIONES (TEMPORALMENTE DESHABILITADO)
-- ===============================================
-- MERGE INTO ubicaciones (id, direccion, provincia, pais) KEY(id)
-- VALUES (10, 'Puerto de Rosario', 'Santa Fe', 'Argentina');
-- 
-- MERGE INTO ubicaciones (id, direccion, provincia, pais) KEY(id)
-- VALUES (15, 'Cliente Origen (Mendoza)', 'Mendoza', 'Argentina');
-- 
-- MERGE INTO ubicaciones (id, direccion, provincia, pais) KEY(id)
-- VALUES (20, 'Depósito Córdoba', 'Córdoba', 'Argentina');
-- 
-- MERGE INTO ubicaciones (id, direccion, provincia, pais) KEY(id)
-- VALUES (30, 'Terminal Buenos Aires (Destino)', 'Buenos Aires', 'Argentina');
-- 
-- MERGE INTO ubicaciones (id, direccion, provincia, pais) KEY(id)
-- VALUES (40, 'Parque Industrial Mendoza (Destino)', 'Mendoza', 'Argentina');

-- ===============================================
-- RUTAS Y TRAMOS DE EJEMPLO (TEMPORALMENTE DESHABILITADO)
-- ===============================================

-- MERGE INTO rutas (id, solicitud_id, cantidad_tramos, cantidad_depositos) KEY(id)
-- VALUES (1, 101, 2, 1);
-- 
-- MERGE INTO rutas (id, solicitud_id, cantidad_tramos, cantidad_depositos) KEY(id)
-- VALUES (2, 102, 1, 0);
-- 
-- MERGE INTO tramos (id, ruta_id, origen_id, destino_id, tipo_id, estado_id, fecha_hora_inicio_aprox, fecha_hora_fin_aprox, camion_dominio) KEY(id)
-- VALUES (1, 1, 10, 20, 1, 1, '2025-11-11T08:00:00', '2025-11-11T16:00:00', NULL);
-- 
-- MERGE INTO tramos (id, ruta_id, origen_id, destino_id, tipo_id, estado_id, fecha_hora_inicio_aprox, fecha_hora_fin_aprox, camion_dominio) KEY(id)
-- VALUES (2, 1, 20, 30, 3, 1, '2025-11-12T08:00:00', '2025-11-12T14:00:00', NULL);
-- 
-- MERGE INTO tramos (id, ruta_id, origen_id, destino_id, tipo_id, estado_id, fecha_hora_inicio_aprox, fecha_hora_fin_aprox, camion_dominio) KEY(id)
-- VALUES (3, 2, 15, 40, 4, 1, '2025-11-13T08:00:00', '2025-11-13T22:00:00', NULL);