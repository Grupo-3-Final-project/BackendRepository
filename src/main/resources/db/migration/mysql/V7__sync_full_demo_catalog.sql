INSERT INTO attractions (name, description, size, status, total_seats, available_seats, maintenance_frequency_days, image_url)
SELECT 'Montaña del Último Grito',
       'Montaña rusa principal del parque, con caídas intensas, raíles oscuros y una experiencia diseñada para los visitantes más valientes.',
       'LARGE',
       'OPEN',
       32,
       32,
       7,
       'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778589979/attractions/Monta%C3%B1a_del_%C3%9Altimo_Grito_t7lu0x.png'
WHERE NOT EXISTS (
    SELECT 1 FROM attractions
    WHERE name = 'Montaña del Último Grito'
       OR image_url = 'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778589979/attractions/Monta%C3%B1a_del_%C3%9Altimo_Grito_t7lu0x.png'
);

INSERT INTO attractions (name, description, size, status, total_seats, available_seats, maintenance_frequency_days, image_url)
SELECT 'Río de Sangre',
       'Recorrido acuático oscuro con barcas temáticas, niebla baja y luces rojas para una experiencia intensa pero controlada.',
       'MEDIUM',
       'OPEN',
       24,
       24,
       14,
       'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778221870/attractions/attractionBloodRiver_kx4mxb.png'
WHERE NOT EXISTS (
    SELECT 1 FROM attractions
    WHERE name = 'Río de Sangre'
       OR image_url = 'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778221870/attractions/attractionBloodRiver_kx4mxb.png'
);

INSERT INTO attractions (name, description, size, status, total_seats, available_seats, maintenance_frequency_days, image_url)
SELECT 'Carrusel Maldito',
       'Carrusel familiar de estética oscura, con luces antiguas, música inquietante y ambiente de feria encantada.',
       'SMALL',
       'OPEN',
       18,
       18,
       30,
       'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778590110/attractions/Carrusel_Maldito_hx2lod.png'
WHERE NOT EXISTS (
    SELECT 1 FROM attractions
    WHERE name = 'Carrusel Maldito'
       OR image_url = 'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778590110/attractions/Carrusel_Maldito_hx2lod.png'
);

INSERT INTO attractions (name, description, size, status, total_seats, available_seats, maintenance_frequency_days, image_url)
SELECT 'Laberinto de las Sombras',
       'Recorrido inmersivo a pie entre pasillos oscuros, niebla baja y luces rojas diseñado para perder la orientación sin perder la seguridad.',
       'SMALL',
       'OPEN',
       20,
       20,
       30,
       'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778221799/attractions/attractionDarkLabyrinth_yqjgnt.png'
WHERE NOT EXISTS (
    SELECT 1 FROM attractions
    WHERE name = 'Laberinto de las Sombras'
       OR image_url = 'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778221799/attractions/attractionDarkLabyrinth_yqjgnt.png'
);

INSERT INTO attractions (name, description, size, status, total_seats, available_seats, maintenance_frequency_days, image_url)
SELECT 'Casa del Eco',
       'Experiencia interior de terror suave, con pasillos oscuros, sonidos envolventes y una ambientación misteriosa apta para público general.',
       'SMALL',
       'OPEN',
       16,
       16,
       30,
       'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778590270/attractions/attractionHauntedMansion_ae7gre.png'
WHERE NOT EXISTS (
    SELECT 1 FROM attractions
    WHERE name = 'Casa del Eco'
       OR image_url = 'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778590270/attractions/attractionHauntedMansion_ae7gre.png'
);

INSERT INTO attractions (name, description, size, status, total_seats, available_seats, maintenance_frequency_days, image_url)
SELECT 'Torre del Terror',
       'Atracción intensa de altura con ambiente oscuro, vistas al parque y una caída diseñada para los visitantes más valientes.',
       'LARGE',
       'OPEN',
       32,
       32,
       7,
       'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778222227/attractions/attractionTerrorTower_hbkqm6.png'
WHERE NOT EXISTS (
    SELECT 1 FROM attractions
    WHERE name = 'Torre del Terror'
       OR image_url = 'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778222227/attractions/attractionTerrorTower_hbkqm6.png'
);

INSERT INTO hotels (name, description, total_rooms, available_rooms, total_places, available_places, half_board_price, full_board_price, image_url)
SELECT 'Hotel Puerta Negra',
       'Hotel principal junto a la entrada del parque, con estética oscura, ambiente premium y acceso cómodo a las zonas principales.',
       120,
       120,
       240,
       237,
       80.00,
       120.00,
       'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778588321/hotels/Hotel_Refugio_de_las_Sombras_m7bsct.png'
WHERE NOT EXISTS (
    SELECT 1 FROM hotels
    WHERE name = 'Hotel Puerta Negra'
);

INSERT INTO hotels (name, description, total_rooms, available_rooms, total_places, available_places, half_board_price, full_board_price, image_url)
SELECT 'Hotel Mansión Carmesí',
       'Alojamiento temático para escapadas cortas, con ambiente de mansión encantada, luz roja y acceso cómodo al parque.',
       90,
       90,
       180,
       178,
       70.00,
       110.00,
       'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778589573/hotels/Hotel_Mansi%C3%B3n_Cremes%C3%AD_fdxncs.png'
WHERE NOT EXISTS (
    SELECT 1 FROM hotels
    WHERE name = 'Hotel Mansión Carmesí'
);

INSERT INTO hotels (name, description, total_rooms, available_rooms, total_places, available_places, half_board_price, full_board_price, image_url)
SELECT 'Hotel Fantasía Nocturna',
       'Hotel familiar premium con ambiente misterioso, habitaciones cómodas y estética nocturna para descansar después de visitar el parque.',
       80,
       80,
       160,
       158,
       65.00,
       95.00,
       'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778588939/hotels/Hotel_Fantas%C3%ADa_Nocturna_pfwnhg.png'
WHERE NOT EXISTS (
    SELECT 1 FROM hotels
    WHERE name = 'Hotel Fantasía Nocturna'
);

INSERT INTO hotels (name, description, total_rooms, available_rooms, total_places, available_places, half_board_price, full_board_price, image_url)
SELECT 'Posada del Farol Rojo',
       'Hotel compacto y acogedor junto a los caminos del parque, con faroles rojos, ambiente oscuro y precio accesible para estancias cortas.',
       60,
       60,
       120,
       120,
       55.00,
       85.00,
       'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778589085/hotels/Posada_del_Farol_Rojo_ypgau0.png'
WHERE NOT EXISTS (
    SELECT 1 FROM hotels
    WHERE name = 'Posada del Farol Rojo'
);

INSERT INTO hotels (name, description, total_rooms, available_rooms, total_places, available_places, half_board_price, full_board_price, image_url)
SELECT 'Hotel Cripta Real',
       'Hotel premium de lujo oscuro, inspirado en una cripta elegante, pensado para packs especiales y estancias exclusivas dentro del parque.',
       70,
       70,
       140,
       140,
       105.00,
       155.00,
       'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778589647/hotels/Hotel_Cripta_Real_baaiiw.png'
WHERE NOT EXISTS (
    SELECT 1 FROM hotels
    WHERE name = 'Hotel Cripta Real'
);
