DELETE FROM offers
WHERE title IN ('Escapada Familiar Magic Park', 'Oferta Aventura')
  AND 4 = (
      SELECT final_offer_count
      FROM (
          SELECT COUNT(DISTINCT title) AS final_offer_count
          FROM offers
          WHERE title IN (
              'Pack Familiar Puerta Negra',
              'Pack Noche Carmesí',
              'Pack Cripta Premium',
              'Pack Acceso Inclusivo'
          )
      ) final_offers
  );
