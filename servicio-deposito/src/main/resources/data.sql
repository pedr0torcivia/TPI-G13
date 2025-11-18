-- ===============================================
-- PROVINCIAS
-- ===============================================
INSERT INTO provincias (id, nombre) VALUES (1, 'La Pampa') ON CONFLICT (id) DO NOTHING;
INSERT INTO provincias (id, nombre) VALUES (2, 'Córdoba')  ON CONFLICT (id) DO NOTHING;

-- ===============================================
-- CIUDADES
-- ===============================================
INSERT INTO ciudades (id, nombre, cod_postal, id_provincia)
VALUES (1, 'Santa Rosa', '6300', 1) ON CONFLICT (id) DO NOTHING;

INSERT INTO ciudades (id, nombre, cod_postal, id_provincia)
VALUES (2, 'Villa María', '5900', 2) ON CONFLICT (id) DO NOTHING;

-- ===============================================
-- UBICACIONES
-- ===============================================
INSERT INTO ubicaciones (id, direccion, lat, lng, id_ciudad)
VALUES (1, 'Av. San Martín 1234', -36.6208, -64.2902, 1) ON CONFLICT (id) DO NOTHING;

INSERT INTO ubicaciones (id, direccion, lat, lng, id_ciudad)
VALUES (2, 'Bv. España 500', -32.4075, -63.2406, 2) ON CONFLICT (id) DO NOTHING;

-- ===============================================
-- DEPOSITOS
-- ===============================================
INSERT INTO depositos (id, nombre, costo_estadia, id_ciudad, id_ubicacion)
VALUES (1, 'Depósito Pampa', 1500.0, 1, 1) ON CONFLICT (id) DO NOTHING;

INSERT INTO depositos (id, nombre, costo_estadia, id_ciudad, id_ubicacion)
VALUES (2, 'Depósito Córdoba', 1800.0, 2, 2) ON CONFLICT (id) DO NOTHING;

-- ===============================================
-- ESTADIAS
-- ===============================================
INSERT INTO estadias (id, fecha_entrada, fecha_salida, id_contenedor, id_deposito, estado)
VALUES (1, '2025-11-01', '2025-11-10', 101, 1, 'ACTIVA') ON CONFLICT (id) DO NOTHING;

INSERT INTO estadias (id, fecha_entrada, fecha_salida, id_contenedor, id_deposito, estado)
VALUES (2, '2025-11-05', '2025-11-09', 102, 2, 'FINALIZADA') ON CONFLICT (id) DO NOTHING;

-- ===============================================
-- REINICIO DE SECUENCIAS (CRÍTICO)
-- ===============================================
SELECT setval(pg_get_serial_sequence('provincias', 'id'), coalesce(max(id), 0) + 1, false) FROM provincias;
SELECT setval(pg_get_serial_sequence('ciudades', 'id'), coalesce(max(id), 0) + 1, false) FROM ciudades;
SELECT setval(pg_get_serial_sequence('ubicaciones', 'id'), coalesce(max(id), 0) + 1, false) FROM ubicaciones;
SELECT setval(pg_get_serial_sequence('depositos', 'id'), coalesce(max(id), 0) + 1, false) FROM depositos;
SELECT setval(pg_get_serial_sequence('estadias', 'id'), coalesce(max(id), 0) + 1, false) FROM estadias;