-- ===============================================
-- TIPOS DE TRAMO
-- ===============================================
INSERT INTO tramo_tipo (id, nombre) VALUES (1, 'origen-deposito')  ON CONFLICT (id) DO NOTHING;
INSERT INTO tramo_tipo (id, nombre) VALUES (2, 'deposito-deposito') ON CONFLICT (id) DO NOTHING;
INSERT INTO tramo_tipo (id, nombre) VALUES (3, 'deposito-destino')  ON CONFLICT (id) DO NOTHING;
INSERT INTO tramo_tipo (id, nombre) VALUES (4, 'origen-destino')    ON CONFLICT (id) DO NOTHING;
INSERT INTO tramo_tipo (id, nombre) VALUES (5, 'principal')         ON CONFLICT (id) DO NOTHING;

-- ===============================================
-- ESTADOS DE TRAMO
-- ===============================================
INSERT INTO tramo_estado (id, nombre) VALUES (1, 'estimado')   ON CONFLICT (id) DO NOTHING;
INSERT INTO tramo_estado (id, nombre) VALUES (2, 'asignado')   ON CONFLICT (id) DO NOTHING;
INSERT INTO tramo_estado (id, nombre) VALUES (3, 'iniciado')   ON CONFLICT (id) DO NOTHING;
INSERT INTO tramo_estado (id, nombre) VALUES (4, 'finalizado') ON CONFLICT (id) DO NOTHING;

-- ===============================================
-- TRANSPORTISTAS
-- ===============================================
INSERT INTO transportistas (id, id_keycloak, nombre, telefono, direccion)
VALUES (1, 'keycloak-1', 'Transporte Veloz SRL', '3512223344', 'Av. Logística 123, Córdoba')
ON CONFLICT (id) DO NOTHING;

INSERT INTO transportistas (id, id_keycloak, nombre, telefono, direccion)
VALUES (2, 'keycloak-2', 'Rutas del Sur SA', '3515556677', 'Ruta 9 km 20, Rosario')
ON CONFLICT (id) DO NOTHING;

-- ===============================================
-- CAMIONES (PK es String 'dominio', no tiene secuencia)
-- ===============================================
INSERT INTO camiones (dominio, id_transportista, capacidad_peso_kg, capacidad_volumen_m3, disponibilidad, costo_km, consumo_combustible_km)
VALUES ('AAA111', 1, 8000, 25, TRUE, 180.0, 0.32)
ON CONFLICT (dominio) DO NOTHING;

INSERT INTO camiones (dominio, id_transportista, capacidad_peso_kg, capacidad_volumen_m3, disponibilidad, costo_km, consumo_combustible_km)
VALUES ('BBB222', 1, 12000, 35, TRUE, 220.0, 0.40)
ON CONFLICT (dominio) DO NOTHING;

INSERT INTO camiones (dominio, id_transportista, capacidad_peso_kg, capacidad_volumen_m3, disponibilidad, costo_km, consumo_combustible_km)
VALUES ('CCC333', 2, 6000, 20, TRUE, 150.0, 0.30)
ON CONFLICT (dominio) DO NOTHING;

-- ===============================================
-- REINICIO DE SECUENCIAS
-- ===============================================
SELECT setval(pg_get_serial_sequence('tramo_tipo', 'id'), coalesce(max(id), 0) + 1, false) FROM tramo_tipo;
SELECT setval(pg_get_serial_sequence('tramo_estado', 'id'), coalesce(max(id), 0) + 1, false) FROM tramo_estado;
SELECT setval(pg_get_serial_sequence('transportistas', 'id'), coalesce(max(id), 0) + 1, false) FROM transportistas;
-- Nota: Rutas y Tramos no tienen inserts activos, pero si descomentas el código viejo, agrega aquí sus reinicios de secuencia.