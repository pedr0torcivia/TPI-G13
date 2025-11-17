-- ===============================================
-- DATOS DE ESTADO (Requeridos por servicio-contenedores)
-- ===============================================
INSERT INTO contenedor_estado (id, nombre) VALUES (1, 'disponible') ON CONFLICT (id) DO NOTHING;
INSERT INTO contenedor_estado (id, nombre) VALUES (2, 'asignado')   ON CONFLICT (id) DO NOTHING;
INSERT INTO contenedor_estado (id, nombre) VALUES (3, 'en_transito')ON CONFLICT (id) DO NOTHING;
INSERT INTO contenedor_estado (id, nombre) VALUES (4, 'entregado')  ON CONFLICT (id) DO NOTHING;

INSERT INTO solicitud_estado (id, nombre) VALUES (1, 'borrador')    ON CONFLICT (id) DO NOTHING;
INSERT INTO solicitud_estado (id, nombre) VALUES (2, 'programada')  ON CONFLICT (id) DO NOTHING;
INSERT INTO solicitud_estado (id, nombre) VALUES (3, 'en_transito') ON CONFLICT (id) DO NOTHING;
INSERT INTO solicitud_estado (id, nombre) VALUES (4, 'entregada')   ON CONFLICT (id) DO NOTHING;
INSERT INTO solicitud_estado (id, nombre) VALUES (5, 'cancelada')   ON CONFLICT (id) DO NOTHING;

-- ===============================================
-- DATOS DE EJEMPLO (Para pruebas iniciales)
-- ===============================================

-- CLIENTES
INSERT INTO clientes (id, nombre, telefono, email, direccion) 
VALUES (1, 'Cliente de Prueba SA', '351123456', 'test@cliente.com', 'Calle Falsa 123') 
ON CONFLICT (id) DO NOTHING;

-- CIUDADES / DEPÓSITOS
INSERT INTO ciudades (id, direccion, provincia, pais) 
VALUES (100, 'Av. Circunvalación 123', 'Córdoba', 'Argentina') 
ON CONFLICT (id) DO NOTHING;

INSERT INTO depositos (identificacion, nombre, id_ubicacion, costo_estadia) 
VALUES (1, 'Depósito Córdoba', 100, 1500) 
ON CONFLICT (identificacion) DO NOTHING;

-- CAMIONES (transporte)
INSERT INTO transportistas (id, nombre) 
VALUES (1, 'Transportista Veloz') 
ON CONFLICT (id) DO NOTHING;

INSERT INTO camiones (dominio, id_transportista, capacidad_peso_kg, capacidad_volumen_m3, disponibilidad, costo_km, consumo_combustible_km) 
VALUES ('ABC123', 1, 5000, 20, TRUE, 150.0, 0.35) 
ON CONFLICT (dominio) DO NOTHING;

-- TARIFAS (tarifa)
INSERT INTO tarifas (id, nombre, valor_litro_combustible, costo_base_km) 
VALUES (1, 'Tarifa General 2025', 1100.50, 200.0) 
ON CONFLICT (id) DO NOTHING;

-- ===============================================
-- CONTENEDOR 36 (EL QUE USA RUTA TENTATIVA)
-- ===============================================
INSERT INTO contenedores (identificacion, peso_kg, volumen_m3, id_estado, cliente_id)
VALUES (36, 1000.0, 10.0, 1, 1)
ON CONFLICT (identificacion) DO NOTHING;
