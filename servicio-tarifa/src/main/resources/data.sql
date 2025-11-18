-- ===============================================
-- TARIFAS
-- ===============================================
INSERT INTO tarifas (id, nombre, valor_litro_combustible, costo_base_km, costo_gestion_tramo, vigente_desde, vigente_hasta)
VALUES (1, 'Tarifa Básica', 9.99, 1.0, 5.0, '2023-01-01', '2023-12-31')
ON CONFLICT (id) DO NOTHING;

INSERT INTO tarifas (id, nombre, valor_litro_combustible, costo_base_km, costo_gestion_tramo, vigente_desde, vigente_hasta)
VALUES (2, 'Tarifa Premium', 19.99, 0.8, 3.0, '2023-01-01', '2023-12-31')
ON CONFLICT (id) DO NOTHING;

INSERT INTO tarifas (id, nombre, valor_litro_combustible, costo_base_km, costo_gestion_tramo, vigente_desde, vigente_hasta)
VALUES (3, 'Tarifa Familiar', 29.99, 0.6, 2.0, '2023-01-01', '2023-12-31')
ON CONFLICT (id) DO NOTHING;

-- ===============================================
-- REINICIO DE SECUENCIAS
-- ===============================================
SELECT setval(pg_get_serial_sequence('tarifas', 'id'), coalesce(max(id), 0) + 1, false) FROM tarifas;