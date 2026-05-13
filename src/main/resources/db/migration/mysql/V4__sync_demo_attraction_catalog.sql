UPDATE attractions
SET name = 'Dragon Coaster',
    description = 'Montana rusa principal del parque.',
    image_url = 'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778222227/attractions/attractionTerrorTower_hbkqm6.png'
WHERE name IN ('Dragon Coaster', 'Terror Tower')
   OR image_url LIKE '%attractionTerrorTower%';

UPDATE attractions
SET name = 'Splash River',
    description = 'Recorrido acuatico familiar.',
    image_url = 'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778221870/attractions/attractionBloodRiver_kx4mxb.png'
WHERE name IN ('Splash River', 'Blood River')
   OR image_url LIKE '%attractionBloodRiver%';

UPDATE attractions
SET name = 'Fantasy Carousel',
    description = 'Atraccion infantil del area fantasy.',
    image_url = 'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778221799/attractions/attractionDarkLabyrinth_yqjgnt.png'
WHERE name IN ('Fantasy Carousel', 'Dark Labyrinth')
   OR image_url LIKE '%attractionDarkLabyrinth%';
