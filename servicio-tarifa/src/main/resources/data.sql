-- Opcional: limpiás lo que hubiera antes
-- DELETE FROM tarifas;

MERGE INTO tarifas (id, nombre, valor_litro_combustible, costo_base_km, costo_gestion_tramo, vigente_desde, vigente_hasta)
KEY(id)
VALUES (1, 'Tarifa Básica', 9.99, 1.0, 5.0, '2023-01-01', '2023-12-31');

MERGE INTO tarifas (id, nombre, valor_litro_combustible, costo_base_km, costo_gestion_tramo, vigente_desde, vigente_hasta)
KEY(id)
VALUES (2, 'Tarifa Premium', 19.99, 0.8, 3.0, '2023-01-01', '2023-12-31');

MERGE INTO tarifas (id, nombre, valor_litro_combustible, costo_base_km, costo_gestion_tramo, vigente_desde, vigente_hasta)
KEY(id)
VALUES (3, 'Tarifa Familiar', 29.99, 0.6, 2.0, '2023-01-01', '2023-12-31');
