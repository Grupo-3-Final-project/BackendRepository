UPDATE offers
SET title = 'Pack Familiar Puerta Negra',
    description = 'Hotel + entradas familiares para cruzar la puerta con una estancia completa junto al parque.',
    hotel_id = (SELECT id FROM hotels WHERE name = 'Hotel Puerta Negra' LIMIT 1),
    board_type = 'FULL_BOARD',
    included_tickets = 4,
    total_price = 399.99,
    image_url = 'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778758628/offers/Pack_Familiar_Puerta_Negra_ivhhzf.png'
WHERE title = 'Escapada Familiar Magic Park'
  AND NOT EXISTS (
      SELECT 1 FROM (
          SELECT id FROM offers WHERE title = 'Pack Familiar Puerta Negra'
      ) existing_offer
  )
  AND EXISTS (
      SELECT 1 FROM hotels WHERE name = 'Hotel Puerta Negra'
  );

UPDATE offers
SET description = 'Hotel + entradas familiares para cruzar la puerta con una estancia completa junto al parque.',
    hotel_id = (SELECT id FROM hotels WHERE name = 'Hotel Puerta Negra' LIMIT 1),
    board_type = 'FULL_BOARD',
    included_tickets = 4,
    total_price = 399.99,
    image_url = 'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778758628/offers/Pack_Familiar_Puerta_Negra_ivhhzf.png'
WHERE title = 'Pack Familiar Puerta Negra'
  AND EXISTS (
      SELECT 1 FROM hotels WHERE name = 'Hotel Puerta Negra'
  );

INSERT INTO offers (title, description, hotel_id, board_type, included_tickets, total_price, image_url)
SELECT 'Pack Familiar Puerta Negra',
       'Hotel + entradas familiares para cruzar la puerta con una estancia completa junto al parque.',
       hotels.id,
       'FULL_BOARD',
       4,
       399.99,
       'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778758628/offers/Pack_Familiar_Puerta_Negra_ivhhzf.png'
FROM hotels
WHERE hotels.name = 'Hotel Puerta Negra'
  AND NOT EXISTS (
      SELECT 1 FROM offers WHERE title = 'Pack Familiar Puerta Negra'
  )
LIMIT 1;

UPDATE offers
SET title = 'Pack Noche Carmesí',
    description = 'Escapada temática con hotel, entradas y ambiente nocturno para vivir el parque al caer la noche.',
    hotel_id = (SELECT id FROM hotels WHERE name = 'Hotel Mansión Carmesí' LIMIT 1),
    board_type = 'HALF_BOARD',
    included_tickets = 2,
    total_price = 249.99,
    image_url = 'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778756951/offers/Pack_Noche_Carmes%C3%AD_cpmqkz.png'
WHERE title = 'Oferta Aventura'
  AND NOT EXISTS (
      SELECT 1 FROM (
          SELECT id FROM offers WHERE title = 'Pack Noche Carmesí'
      ) existing_offer
  )
  AND EXISTS (
      SELECT 1 FROM hotels WHERE name = 'Hotel Mansión Carmesí'
  );

UPDATE offers
SET description = 'Escapada temática con hotel, entradas y ambiente nocturno para vivir el parque al caer la noche.',
    hotel_id = (SELECT id FROM hotels WHERE name = 'Hotel Mansión Carmesí' LIMIT 1),
    board_type = 'HALF_BOARD',
    included_tickets = 2,
    total_price = 249.99,
    image_url = 'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778756951/offers/Pack_Noche_Carmes%C3%AD_cpmqkz.png'
WHERE title = 'Pack Noche Carmesí'
  AND EXISTS (
      SELECT 1 FROM hotels WHERE name = 'Hotel Mansión Carmesí'
  );

INSERT INTO offers (title, description, hotel_id, board_type, included_tickets, total_price, image_url)
SELECT 'Pack Noche Carmesí',
       'Escapada temática con hotel, entradas y ambiente nocturno para vivir el parque al caer la noche.',
       hotels.id,
       'HALF_BOARD',
       2,
       249.99,
       'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778756951/offers/Pack_Noche_Carmes%C3%AD_cpmqkz.png'
FROM hotels
WHERE hotels.name = 'Hotel Mansión Carmesí'
  AND NOT EXISTS (
      SELECT 1 FROM offers WHERE title = 'Pack Noche Carmesí'
  )
LIMIT 1;

UPDATE offers
SET description = 'Experiencia premium con hotel exclusivo, entradas y estancia de lujo oscuro dentro del universo de La Última Puerta.',
    hotel_id = (SELECT id FROM hotels WHERE name = 'Hotel Cripta Real' LIMIT 1),
    board_type = 'FULL_BOARD',
    included_tickets = 2,
    total_price = 599.99,
    image_url = 'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778757012/offers/Pack_Cripta_Premium_hpizxo.png'
WHERE title = 'Pack Cripta Premium'
  AND EXISTS (
      SELECT 1 FROM hotels WHERE name = 'Hotel Cripta Real'
  );

INSERT INTO offers (title, description, hotel_id, board_type, included_tickets, total_price, image_url)
SELECT 'Pack Cripta Premium',
       'Experiencia premium con hotel exclusivo, entradas y estancia de lujo oscuro dentro del universo de La Última Puerta.',
       hotels.id,
       'FULL_BOARD',
       2,
       599.99,
       'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778757012/offers/Pack_Cripta_Premium_hpizxo.png'
FROM hotels
WHERE hotels.name = 'Hotel Cripta Real'
  AND NOT EXISTS (
      SELECT 1 FROM offers WHERE title = 'Pack Cripta Premium'
  )
LIMIT 1;

UPDATE offers
SET description = 'Hotel + entradas con descuento de accesibilidad para visitantes con discapacidad y acompañante.',
    hotel_id = (SELECT id FROM hotels WHERE name = 'Hotel Fantasía Nocturna' LIMIT 1),
    board_type = 'FULL_BOARD',
    included_tickets = 2,
    total_price = 179.99,
    image_url = 'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778759129/offers/Pack_Acceso_Inclusivo_ot4iio.png'
WHERE title = 'Pack Acceso Inclusivo'
  AND EXISTS (
      SELECT 1 FROM hotels WHERE name = 'Hotel Fantasía Nocturna'
  );

INSERT INTO offers (title, description, hotel_id, board_type, included_tickets, total_price, image_url)
SELECT 'Pack Acceso Inclusivo',
       'Hotel + entradas con descuento de accesibilidad para visitantes con discapacidad y acompañante.',
       hotels.id,
       'FULL_BOARD',
       2,
       179.99,
       'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778759129/offers/Pack_Acceso_Inclusivo_ot4iio.png'
FROM hotels
WHERE hotels.name = 'Hotel Fantasía Nocturna'
  AND NOT EXISTS (
      SELECT 1 FROM offers WHERE title = 'Pack Acceso Inclusivo'
  )
LIMIT 1;
