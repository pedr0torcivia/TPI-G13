-- ===============================================
-- ESTADOS (Catálogos)
-- ===============================================
INSERT INTO contenedor_estado (id, nombre) VALUES (1, 'disponible') ON CONFLICT (id) DO NOTHING;
INSERT INTO contenedor_estado (id, nombre) VALUES (2, 'asignado')   ON CONFLICT (id) DO NOTHING;
INSERT INTO contenedor_estado (id, nombre) VALUES (3, 'en_transito')ON CONFLICT (id) DO NOTHING;
INSERT INTO contenedor_estado (id, nombre) VALUES (4, 'entregado')  ON CONFLICT (id) DO NOTHING;

INSERT INTO solicitud_estado (id, nombre) VALUES (1, 'borrador')    ON CONFLICT (id) DO NOTHING;
INSERT INTO solicitud_estado (id, nombre) VALUES (2, 'programada')  ON CONFLICT (id) DO NOTHING;
INSERT INTO solicitud_estado (id, nombre) VALUES (3, 'en_transito') ON CONFLICT (id) DO NOTHING;
INSERT INTO solicitud_estado (id, nombre) VALUES (4, 'entregada')   ON CONFLICT (id) DO NOTHING;
INSERT INTO solicitud_estado (id, nombre) VALUES (5, 'cancelada')   ON CONFLICT (id) DO NOTHING;

-- ===============================================
-- CLIENTES
-- ===============================================
INSERT INTO clientes (id, nombre, telefono, email, direccion) 
VALUES (1, 'Cliente de Prueba SA', '351123456', 'test@cliente.com', 'Calle Falsa 123') 
ON CONFLICT (id) DO NOTHING;

-- ===============================================
-- CONTENEDORES
-- ===============================================
INSERT INTO contenedores (identificacion, peso_kg, volumen_m3, id_estado, cliente_id)
VALUES (36, 1000.0, 10.0, 1, 1)
ON CONFLICT (identificacion) DO NOTHING;

-- ===============================================
-- REINICIO DE SECUENCIAS
-- ===============================================
SELECT setval(pg_get_serial_sequence('clientes', 'id'), coalesce(max(id), 0) + 1, false) FROM clientes;
-- Usa 'identificacion' en vez de 'id' para contenedores
SELECT setval(pg_get_serial_sequence('contenedores', 'identificacion'), coalesce(max(identificacion), 0) + 1, false) FROM contenedores;
-- Si agregas solicitudes de prueba, recuerda reiniciar 'solicitudes_numero_seq'