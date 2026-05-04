# Checklist de demo backend

## Antes de la demo

- `.env` creado a partir de `.env.example`
- MySQL levantado si se usa perfil `dev`
- backend arrancado sin errores
- Swagger accesible en `/swagger-ui.html`
- OpenAPI JSON accesible en `/v3/api-docs`

## Credenciales demo

- usuario interno: `admin`
- password interna: `admin12345`

## Flujos a revisar

- crear usuario desde `POST /api/users`
- consultar hoteles, atracciones y ofertas
- crear reserva en `POST /api/bookings`
- validar conflicto por menor sin adulto
- validar conflicto por hotel completo
- consultar detalle de reserva
- iniciar sesion interna
- consultar dashboard
- consultar reservas protegidas
- generar turnos
- generar mantenimiento

## Verificaciones tecnicas

- `./mvnw.cmd verify` en verde
- perfil `e2e` operativo para Playwright
- CORS operativo con el frontend
- subida de imagenes preparada con variables de Cloudinary
