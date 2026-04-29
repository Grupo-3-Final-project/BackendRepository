# CONTRACT_TESTING.md - Parque de Atracciones

## 1. Objetivo

Este documento define la estrategia de contract testing entre backend y frontend para el proyecto final del Parque de Atracciones.

El objetivo es garantizar que:

- Backend devuelve exactamente los campos esperados por frontend.
- Frontend envia requests con la estructura acordada.
- Los nombres de campos JSON coinciden con `docs/API_CONTRACT.md`.
- Los codigos HTTP acordados se respetan.
- Los errores controlados mantienen una estructura consistente.
- Los cambios de contrato se detectan antes de romper la integracion.

Antes de crear o modificar tests de contrato se debe revisar tambien `docs/API_CONTRACT.md`.

## 2. Regla principal

`docs/API_CONTRACT.md` es la fuente de verdad del contrato API.

Ningun test de contrato debe inventar campos, rutas, codigos o estructuras que no esten definidos en ese documento.

Si durante el desarrollo se necesita cambiar el contrato, el orden correcto es:

1. Comunicar el cambio al equipo.
2. Actualizar `docs/API_CONTRACT.md`.
3. Actualizar `docs/CONTRACT_TESTING.md` si afecta a la estrategia o a los casos minimos.
4. Actualizar Swagger/OpenAPI.
5. Actualizar tests de contrato.
6. Actualizar backend y servicios API del frontend.

## 3. Alcance de los contract tests

Los contract tests deben validar como minimo:

- Endpoint correcto.
- Metodo HTTP correcto.
- Status code correcto.
- Content-Type esperado.
- Campos obligatorios de response.
- Campos obligatorios de request.
- Tipos de datos principales.
- Formato de fechas.
- Formato de importes.
- Estructura comun de error.
- Casos de negocio criticos expuestos por API.

No deben sustituir a los tests unitarios, tests de integracion ni tests E2E. Su responsabilidad es comprobar que el contrato entre sistemas no se rompe.

## 4. Convenciones a validar

Base URL:

```text
/api
```

Formato de respuesta:

```text
application/json
```

Formato de fecha:

```text
YYYY-MM-DD
```

Ejemplo:

```json
{
  "visitDate": "2026-05-22"
}
```

Formato de fecha y hora:

```text
YYYY-MM-DDTHH:mm:ss
```

Ejemplo:

```json
{
  "createdAt": "2026-05-22T10:30:00"
}
```

Formato de importes:

```json
{
  "totalPrice": 149.99
}
```

## 5. Codigos HTTP a validar

| Codigo | Uso esperado |
| --- | --- |
| `200 OK` | Consulta correcta |
| `201 Created` | Recurso creado correctamente |
| `204 No Content` | Eliminacion correcta sin body |
| `400 Bad Request` | Error de validacion o datos incorrectos |
| `404 Not Found` | Recurso no encontrado |
| `409 Conflict` | Conflicto con una regla de negocio |
| `500 Internal Server Error` | Error inesperado del servidor |

## 6. Formato comun de error

Todos los errores controlados deben respetar esta estructura:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "The hotel is full and cannot be booked",
  "path": "/api/bookings",
  "timestamp": "2026-05-22T10:30:00"
}
```

Campos obligatorios:

- `status`
- `error`
- `message`
- `path`
- `timestamp`

El frontend no debe depender de trazas internas, nombres de excepciones Java ni mensajes tecnicos no controlados.

## 7. Tests minimos por modulo

| Modulo | Endpoint | Que debe validar |
| --- | --- | --- |
| Usuarios | `GET /api/users` | Campos correctos del usuario, incluyendo `dni` |
| Hoteles | `GET /api/hotels` | Campos correctos del hotel, incluyendo `totalPlaces` y `availablePlaces` |
| Atracciones | `GET /api/attractions` | Campos correctos de atraccion, incluyendo `totalSeats` y `availableSeats` |
| Empleados | `GET /api/employees` | `dni`, `employeeType` y `shift` |
| Reservas | `POST /api/bookings` | Compra con acompanantes y total calculado |
| Reservas | `POST /api/bookings` | Error si el hotel esta completo |
| Reservas | `POST /api/bookings` | Error si un menor viaja sin adulto |
| Dashboard | `GET /api/dashboard/tickets-by-age-range` | Metrica por rango de edad |
| Dashboard | `GET /api/dashboard/current-year-revenue` | Recaudacion anual |
| Dashboard | `GET /api/dashboard/top-hotels` | Top 3 hoteles por recaudacion |

## 8. Reglas de negocio cubiertas por contrato

### Reservas y entradas

Los tests deben cubrir que:

- Un cliente puede comprar varias entradas para varias personas.
- Cada acompanante tiene `firstName`, `lastName` y `birthDate`.
- El `dni` del usuario es obligatorio y no se repite.
- El backend calcula la tarifa segun edad.
- Un menor no puede viajar si no esta acompanado de un adulto.
- El total de la compra se calcula correctamente.
- La respuesta de reserva incluye `tickets`, `totalPrice`, `emailSent` y `createdAt`.

### Hoteles

Los tests deben cubrir que:

- No se puede vender un hotel sin plazas disponibles.
- Al crear una reserva con hotel se reduce la disponibilidad de plazas.
- No se puede reservar un hotel inexistente.
- El contrato expone `totalPlaces` y `availablePlaces`.

### Empleados y turnos

Los tests deben cubrir que:

- Existen empleados de tipo `CLEANER`, `ANIMATOR` y `TECHNICIAN`.
- El `dni` del empleado es obligatorio y no se repite.
- Siempre debe haber al menos 3 empleados de cada oficio repartidos en 2 turnos.
- La rotacion de turnos se hace cada 15 dias.
- No se pueden generar turnos si no hay suficientes empleados activos.

### Mantenimiento

Los tests deben cubrir que:

- Las atracciones grandes se revisan con mas frecuencia.
- El contrato expone `totalSeats` y `availableSeats`.
- La agenda de mantenimiento se genera automaticamente.
- Cada mantenimiento tiene tecnicos asignados automaticamente.
- No se puede generar mantenimiento si no hay tecnicos disponibles.

### Dashboard

Los tests deben cubrir que:

- Se calculan entradas vendidas por rango de edad y ano.
- Se calcula el total ganado en el ano en curso.
- Se obtienen los 3 hoteles que mas recaudan en el ano indicado.
- Las metricas se calculan en backend y el frontend solo las muestra.

## 9. Campos criticos por response

### Usuario

```json
{
  "id": 1,
  "firstName": "David",
  "lastName": "Navarro",
  "dni": "12345678A",
  "email": "david@example.com",
  "phone": "600123123",
  "birthDate": "1990-04-15"
}
```

### Hotel

```json
{
  "id": 1,
  "name": "Hotel Magic Park",
  "description": "Hotel familiar situado junto al parque.",
  "totalRooms": 120,
  "availableRooms": 32,
  "totalPlaces": 240,
  "availablePlaces": 64,
  "halfBoardPrice": 80.0,
  "fullBoardPrice": 120.0,
  "imageUrl": "https://example.com/hotel.jpg"
}
```

### Atraccion

```json
{
  "id": 1,
  "name": "Dragon Coaster",
  "description": "Montana rusa principal del parque.",
  "size": "LARGE",
  "status": "OPEN",
  "totalSeats": 32,
  "availableSeats": 28,
  "maintenanceFrequencyDays": 7,
  "imageUrl": "https://example.com/attraction.jpg"
}
```

### Empleado

```json
{
  "id": 1,
  "firstName": "Laura",
  "lastName": "Gomez",
  "dni": "87654321B",
  "email": "laura@example.com",
  "employeeType": "TECHNICIAN",
  "shift": "MORNING",
  "active": true
}
```

### Reserva

```json
{
  "id": 1,
  "userId": 1,
  "userFullName": "David Navarro",
  "hotelId": 1,
  "hotelName": "Hotel Magic Park",
  "boardType": "FULL_BOARD",
  "visitDate": "2026-05-22",
  "tickets": [
    {
      "holderFullName": "Ana Garcia",
      "ageRange": "ADULT",
      "price": 45.0
    }
  ],
  "totalPrice": 190.0,
  "emailSent": true,
  "createdAt": "2026-05-22T10:30:00"
}
```

## 10. Casos de error minimos

### Usuario duplicado

Endpoint:

```text
POST /api/users
```

Errores esperados:

- `409 Conflict`: `Email already exists`
- `409 Conflict`: `DNI already exists`

### Hotel completo

Endpoint:

```text
POST /api/bookings
```

Error esperado:

- `409 Conflict`: `Hotel is full`

### Menor sin adulto

Endpoint:

```text
POST /api/bookings
```

Error esperado:

- `409 Conflict`: `A minor cannot travel without an adult`

### Recurso inexistente

Endpoints de detalle o reservas:

```text
GET /api/users/{id}
GET /api/hotels/{id}
GET /api/bookings/{id}
POST /api/bookings
```

Errores esperados:

- `404 Not Found`: `User not found`
- `404 Not Found`: `Hotel not found`

### Turnos sin empleados suficientes

Endpoint:

```text
POST /api/shifts/generate
```

Error esperado:

- `409 Conflict`: `Not enough employees to cover required shifts`

### Mantenimiento sin tecnicos suficientes

Endpoint:

```text
POST /api/maintenance/generate
```

Error esperado:

- `409 Conflict`: `Not enough technicians available`

## 11. Responsabilidades del backend

Backend debe garantizar que:

- Implementa los endpoints definidos en `docs/API_CONTRACT.md`.
- Devuelve los campos JSON con los nombres exactos acordados.
- Calcula reglas de negocio y metricas.
- Devuelve errores controlados con formato comun.
- Mantiene Swagger/OpenAPI alineado con el contrato.
- Incluye tests de contrato cuando un endpoint forma parte de la integracion frontend-backend.

## 12. Responsabilidades del frontend

Frontend debe garantizar que:

- Usa servicios centralizados en `/src/api/`.
- No hace llamadas HTTP directas dentro de componentes JSX.
- Envia requests con los campos definidos en el contrato.
- Consume responses usando los nombres exactos del contrato.
- Maneja `400`, `404`, `409` y `500` con mensajes visibles en castellano.
- No muestra errores crudos del servidor al usuario final.

## 13. Relacion con Swagger/OpenAPI

Swagger/OpenAPI debe ser una representacion tecnica del contrato.

Una vez aprobado el contrato, el backend debe exponer:

```text
http://localhost:8080/swagger-ui/index.html
```

Y el contrato tecnico en JSON:

```text
http://localhost:8080/v3/api-docs
```

Los contract tests pueden apoyarse en Swagger/OpenAPI, pero no deben contradecir `docs/API_CONTRACT.md`.

## 14. Checklist antes de cerrar una tarea que afecta al contrato

Antes de cerrar una tarea que modifique comunicacion frontend-backend, comprobar:

- Se ha revisado `docs/API_CONTRACT.md`.
- Se ha revisado `docs/CONTRACT_TESTING.md`.
- El cambio ha sido comunicado al equipo.
- El endpoint mantiene metodo HTTP, ruta y campos esperados.
- Los tests de contrato relevantes pasan.
- Swagger/OpenAPI esta actualizado.
- Los servicios API del frontend estan actualizados.
- Los tests afectados se han actualizado.
- No se han cambiado campos JSON sin aprobacion.
