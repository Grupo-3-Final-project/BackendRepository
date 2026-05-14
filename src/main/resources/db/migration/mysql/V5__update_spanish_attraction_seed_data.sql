UPDATE attractions
SET name = 'Torre del Terror',
    description = 'Atracción intensa de altura con ambiente oscuro, vistas al parque y una caída diseñada para los visitantes más valientes.'
WHERE name IN ('Dragon Coaster', 'Terror Tower', 'Torre del Terror')
   OR image_url LIKE '%attractionTerrorTower%';

UPDATE attractions
SET name = 'Río de Sangre',
    description = 'Recorrido acuático oscuro con barcas temáticas, niebla baja y luces rojas para una experiencia intensa pero controlada.'
WHERE name IN ('Splash River', 'Blood River', 'Río de Sangre')
   OR image_url LIKE '%attractionBloodRiver%';

UPDATE attractions
SET name = 'Laberinto de las Sombras',
    description = 'Recorrido inmersivo a pie entre pasillos oscuros, niebla baja y luces rojas diseñado para perder la orientación sin perder la seguridad.'
WHERE name IN ('Fantasy Carousel', 'Dark Labyrinth', 'Laberinto de las Sombras')
   OR image_url LIKE '%attractionDarkLabyrinth%';
