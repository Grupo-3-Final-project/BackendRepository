UPDATE hotels
SET image_url = CASE name
    WHEN 'Hotel Magic Park' THEN 'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778153079/hotels/publicHomeHeroGate_sytdho.png'
    WHEN 'Hotel Adventure' THEN 'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778494828/hotels/publicHomeParkMap_d23ikl.png'
    WHEN 'Hotel Fantasy' THEN 'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778234157/hotels/chef_bbsbqp.jpg'
    ELSE image_url
END
WHERE name IN ('Hotel Magic Park', 'Hotel Adventure', 'Hotel Fantasy')
  AND image_url LIKE 'https://res.cloudinary.com/demo/%';

UPDATE attractions
SET image_url = CASE name
    WHEN 'Dragon Coaster' THEN 'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778222227/attractions/attractionTerrorTower_hbkqm6.png'
    WHEN 'Splash River' THEN 'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778221870/attractions/attractionBloodRiver_kx4mxb.png'
    WHEN 'Fantasy Carousel' THEN 'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778221799/attractions/attractionDarkLabyrinth_yqjgnt.png'
    ELSE image_url
END
WHERE name IN ('Dragon Coaster', 'Splash River', 'Fantasy Carousel')
  AND image_url LIKE 'https://res.cloudinary.com/demo/%';

UPDATE offers
SET image_url = CASE title
    WHEN 'Escapada Familiar Magic Park' THEN 'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778494828/offers/offerHotelTicket_d8hvg3.png'
    WHEN 'Oferta Aventura' THEN 'https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778494828/offers/offerFamilyPack_tzegmw.png'
    ELSE image_url
END
WHERE title IN ('Escapada Familiar Magic Park', 'Oferta Aventura')
  AND image_url LIKE 'https://res.cloudinary.com/demo/%';
