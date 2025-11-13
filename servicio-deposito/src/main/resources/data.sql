-- ===============================================
-- DATOS INICIALES PARA servicio-deposito
-- ===============================================

-- PROVINCIAS
MERGE INTO provincias (id, nombre) VALUES (1, 'La Pampa');
MERGE INTO provincias (id, nombre) VALUES (2, 'Córdoba');

-- CIUDADES
MERGE INTO ciudades (id, nombre, cod_postal, id_provincia)
VALUES (1, 'Santa Rosa', '6300', 1);
MERGE INTO ciudades (id, nombre, cod_postal, id_provincia)
VALUES (2, 'Villa María', '5900', 2);

-- UBICACIONES
MERGE INTO ubicaciones (id, direccion, lat, lng, id_ciudad)
VALUES (1, 'Av. San Martín 1234', -36.6208, -64.2902, 1);
MERGE INTO ubicaciones (id, direccion, lat, lng, id_ciudad)
VALUES (2, 'Bv. España 500', -32.4075, -63.2406, 2);

-- DEPOSITOS
MERGE INTO depositos (id, nombre, costo_estadia, id_ciudad, id_ubicacion)
VALUES (1, 'Depósito Pampa', 1500.0, 1, 1);
MERGE INTO depositos (id, nombre, costo_estadia, id_ciudad, id_ubicacion)
VALUES (2, 'Depósito Córdoba', 1800.0, 2, 2);

-- ESTADIAS
MERGE INTO estadias (id, fecha_entrada, fecha_salida, id_contenedor, id_deposito, estado)
VALUES (1, DATE '2025-11-01', DATE '2025-11-10', 101, 1, 'ACTIVA');
MERGE INTO estadias (id, fecha_entrada, fecha_salida, id_contenedor, id_deposito, estado)
VALUES (2, DATE '2025-11-05', DATE '2025-11-09', 102, 2, 'FINALIZADA');
