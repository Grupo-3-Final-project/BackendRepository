# API_CONTRACT.md - Parque de Atracciones

## 1. Objetivo

Este documento define el contrato API acordado entre backend y frontend para el proyecto final del Parque de Atracciones.

Sirve como referencia para:

- Desarrollo coordinado entre backend y frontend.
- Documentacion Swagger/OpenAPI.
- Contract testing.
- Pruebas con Postman.
- Validacion de criterios de aceptacion.

Cualquier cambio en endpoints, metodos HTTP, DTOs, requests, responses, nombres de campos JSON o codigos de error debe comunicarse al equipo y actualizar este documento.

## 2. Convenciones generales

Base URL:

```text
/api/v1/v1
```

Todas las respuestas deben ser JSON.

Las fechas deben enviarse en formato ISO:

```json
{
  "visitDate": "2026-05-22"
}
```

Los importes deben enviarse como numeros decimales:

```json
{
  "totalPrice": 149.99
}
```

Codigos HTTP acordados:

| Codigo | Uso |
| --- | --- |
| `200 OK` | Consulta correcta |
| `201 Created` | Recurso creado correctamente |
| `204 No Content` | Eliminacion correcta sin body |
| `400 Bad Request` | Error de validacion o datos incorrectos |
| `401 Unauthorized` | Credenciales invalidas o autenticacion requerida |
| `404 Not Found` | Recurso no encontrado |
| `409 Conflict` | Conflicto con una regla de negocio |
| `500 Internal Server Error` | Error inesperado del servidor |

Formato comun de error:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "The hotel is full and cannot be booked",
  "path": "/api/v1/bookings",
  "timestamp": "2026-05-22T10:30:00"
}
```

## 3. Usuarios

Entidad usada para clientes que compran entradas o reservas. Incluye `dni` como identificador documental del usuario.

### GET /api/v1/users

Devuelve todos los usuarios.

Response `200 OK`:

```json
[
  {
    "id": 1,
    "firstName": "David",
    "lastName": "Navarro",
    "dni": "12345678A",
    "email": "david@example.com",
    "phone": "600123123",
    "birthDate": "1990-04-15"
  }
]
```

### GET /api/v1/users/{id}

Devuelve un usuario por ID.

Response `200 OK`:

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

Errores posibles:

- `404 Not Found`: `User not found`

### POST /api/v1/users

Crea un usuario.

Request:

```json
{
  "firstName": "David",
  "lastName": "Navarro",
  "dni": "12345678A",
  "email": "david@example.com",
  "phone": "600123123",
  "birthDate": "1990-04-15"
}
```

Response `201 Created`:

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

Errores posibles:

- `400 Bad Request`: `Invalid user data`
- `409 Conflict`: `Email already exists`
- `409 Conflict`: `DNI already exists`

### PUT /api/v1/users/{id}

Actualiza un usuario existente.

Request:

```json
{
  "firstName": "David",
  "lastName": "Navarro Oliver",
  "dni": "12345678A",
  "email": "david@example.com",
  "phone": "600123123",
  "birthDate": "1990-04-15"
}
```

Response `200 OK`:

```json
{
  "id": 1,
  "firstName": "David",
  "lastName": "Navarro Oliver",
  "dni": "12345678A",
  "email": "david@example.com",
  "phone": "600123123",
  "birthDate": "1990-04-15"
}
```

### DELETE /api/v1/users/{id}

Elimina un usuario.

Response:

```text
204 No Content
```

`GET /api/v1/users`, `GET /api/v1/users/{id}`, `PUT /api/v1/users/{id}` y `DELETE /api/v1/users/{id}` requieren autenticacion interna con JWT.

## 3.1. Auth interna

Este modulo es solo para rutas internas de administracion y taquilla. No aplica a visitantes ni a la compra publica.

### POST /api/v1/auth/login

Valida una credencial interna y devuelve un JWT Bearer.

Request:

```json
{
  "username": "admin",
  "password": "admin12345"
}
```

Response `200 OK`:

```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "credentialId": 1,
  "username": "admin",
  "email": "admin@parque.local",
  "role": "ADMIN",
  "expiresAt": "2026-05-22T10:30:00"
}
```

Errores posibles:

- `400 Bad Request`: `Invalid login data`
- `401 Unauthorized`: `Invalid credentials`

## 4. Hoteles

Entidad usada para mostrar hoteles, crear reservas y calcular ofertas. Debe reflejar habitaciones, plazas totales y plazas disponibles.

### GET /api/v1/hotels

Devuelve todos los hoteles.

Response `200 OK`:

```json
[
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
]
```

### GET /api/v1/hotels/{id}

Devuelve un hotel por ID.

Response `200 OK`:

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

### POST /api/v1/hotels

Crea un hotel.

Request:

```json
{
  "name": "Hotel Magic Park",
  "description": "Hotel familiar situado junto al parque.",
  "totalRooms": 120,
  "availableRooms": 120,
  "totalPlaces": 240,
  "availablePlaces": 240,
  "halfBoardPrice": 80.0,
  "fullBoardPrice": 120.0,
  "imageUrl": "https://example.com/hotel.jpg"
}
```

Response `201 Created`:

```json
{
  "id": 1,
  "name": "Hotel Magic Park",
  "description": "Hotel familiar situado junto al parque.",
  "totalRooms": 120,
  "availableRooms": 120,
  "totalPlaces": 240,
  "availablePlaces": 240,
  "halfBoardPrice": 80.0,
  "fullBoardPrice": 120.0,
  "imageUrl": "https://example.com/hotel.jpg"
}
```

### PUT /api/v1/hotels/{id}

Actualiza un hotel existente.

Request:

```json
{
  "name": "Hotel Magic Park Resort",
  "description": "Hotel familiar situado junto al parque.",
  "totalRooms": 120,
  "availableRooms": 80,
  "totalPlaces": 240,
  "availablePlaces": 160,
  "halfBoardPrice": 90.0,
  "fullBoardPrice": 130.0,
  "imageUrl": "https://example.com/hotel.jpg"
}
```

Response `200 OK`:

```json
{
  "id": 1,
  "name": "Hotel Magic Park Resort",
  "description": "Hotel familiar situado junto al parque.",
  "totalRooms": 120,
  "availableRooms": 80,
  "totalPlaces": 240,
  "availablePlaces": 160,
  "halfBoardPrice": 90.0,
  "fullBoardPrice": 130.0,
  "imageUrl": "https://example.com/hotel.jpg"
}
```

### DELETE /api/v1/hotels/{id}

Elimina un hotel.

Response:

```text
204 No Content
```

`POST /api/v1/hotels`, `PUT /api/v1/hotels/{id}` y `DELETE /api/v1/hotels/{id}` requieren autenticacion interna con JWT.

## 5. Atracciones

Valores propuestos para `size`: `SMALL`, `MEDIUM`, `LARGE`.

Valores propuestos para `status`: `OPEN`, `CLOSED`, `MAINTENANCE`.

`totalSeats` representa la capacidad total de plazas de la atraccion y `availableSeats` las plazas disponibles en ese momento.

### GET /api/v1/attractions

Devuelve todas las atracciones.

Response `200 OK`:

```json
[
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
]
```

### GET /api/v1/attractions/{id}

Devuelve una atraccion por ID.

Response `200 OK`:

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

### POST /api/v1/attractions

Crea una atraccion.

Request:

```json
{
  "name": "Dragon Coaster",
  "description": "Montana rusa principal del parque.",
  "size": "LARGE",
  "status": "OPEN",
  "totalSeats": 32,
  "availableSeats": 32,
  "imageUrl": "https://example.com/attraction.jpg"
}
```

Response `201 Created`:

```json
{
  "id": 1,
  "name": "Dragon Coaster",
  "description": "Montana rusa principal del parque.",
  "size": "LARGE",
  "status": "OPEN",
  "totalSeats": 32,
  "availableSeats": 32,
  "maintenanceFrequencyDays": 7,
  "imageUrl": "https://example.com/attraction.jpg"
}
```

`maintenanceFrequencyDays` puede calcularse automaticamente segun el tamano de la atraccion.

### PUT /api/v1/attractions/{id}

Actualiza una atraccion existente.

Request:

```json
{
  "name": "Dragon Coaster",
  "description": "Montana rusa principal del parque.",
  "size": "LARGE",
  "status": "MAINTENANCE",
  "totalSeats": 32,
  "availableSeats": 0,
  "imageUrl": "https://example.com/attraction.jpg"
}
```

Response `200 OK`:

```json
{
  "id": 1,
  "name": "Dragon Coaster",
  "description": "Montana rusa principal del parque.",
  "size": "LARGE",
  "status": "MAINTENANCE",
  "totalSeats": 32,
  "availableSeats": 0,
  "maintenanceFrequencyDays": 7,
  "imageUrl": "https://example.com/attraction.jpg"
}
```

### DELETE /api/v1/attractions/{id}

Elimina una atraccion.

Response:

```text
204 No Content
```

`POST /api/v1/attractions`, `PUT /api/v1/attractions/{id}` y `DELETE /api/v1/attractions/{id}` requieren autenticacion interna con JWT.

## 6. Empleados

Entidad usada para limpiadores, animadores y tecnicos. Incluye `dni` como identificador documental del empleado.

Valores propuestos para `employeeType`: `CLEANER`, `ANIMATOR`, `TECHNICIAN`.

Valores propuestos para `shift`: `MORNING`, `AFTERNOON`.

### GET /api/v1/employees

Devuelve todos los empleados.

Response `200 OK`:

```json
[
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
]
```

### GET /api/v1/employees/{id}

Devuelve un empleado por ID.

Response `200 OK`:

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

### POST /api/v1/employees

Crea un empleado.

Request:

```json
{
  "firstName": "Laura",
  "lastName": "Gomez",
  "dni": "87654321B",
  "email": "laura@example.com",
  "employeeType": "TECHNICIAN",
  "shift": "MORNING",
  "active": true
}
```

Response `201 Created`:

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

### PUT /api/v1/employees/{id}

Actualiza un empleado.

Request:

```json
{
  "firstName": "Laura",
  "lastName": "Gomez Perez",
  "dni": "87654321B",
  "email": "laura@example.com",
  "employeeType": "TECHNICIAN",
  "shift": "AFTERNOON",
  "active": true
}
```

Response `200 OK`:

```json
{
  "id": 1,
  "firstName": "Laura",
  "lastName": "Gomez Perez",
  "dni": "87654321B",
  "email": "laura@example.com",
  "employeeType": "TECHNICIAN",
  "shift": "AFTERNOON",
  "active": true
}
```

### DELETE /api/v1/employees/{id}

Elimina un empleado.

Response:

```text
204 No Content
```

Todos los endpoints de `/api/v1/employees` requieren autenticacion interna con JWT.

## 7. Ofertas

Las ofertas combinan hotel, tipo de pension y entradas.

Valor propuesto para `boardType`: `HALF_BOARD`, `FULL_BOARD`.

### GET /api/v1/offers

Devuelve las ofertas disponibles.

Response `200 OK`:

```json
[
  {
    "id": 1,
    "title": "Oferta Familiar Magic Park",
    "description": "Hotel + entradas para 2 adultos y 2 ninos.",
    "hotelId": 1,
    "hotelName": "Hotel Magic Park",
    "boardType": "FULL_BOARD",
    "includedTickets": 4,
    "totalPrice": 399.99,
    "imageUrl": "https://example.com/offer.jpg"
  }
]
```

### GET /api/v1/offers/{id}

Devuelve una oferta por ID.

Response `200 OK`:

```json
{
  "id": 1,
  "title": "Oferta Familiar Magic Park",
  "description": "Hotel + entradas para 2 adultos y 2 ninos.",
  "hotelId": 1,
  "hotelName": "Hotel Magic Park",
  "boardType": "FULL_BOARD",
  "includedTickets": 4,
  "totalPrice": 399.99,
  "imageUrl": "https://example.com/offer.jpg"
}
```

### POST /api/v1/offers

Crea una oferta.

Request:

```json
{
  "title": "Oferta Familiar Magic Park",
  "description": "Hotel + entradas para 2 adultos y 2 ninos.",
  "hotelId": 1,
  "boardType": "FULL_BOARD",
  "includedTickets": 4,
  "totalPrice": 399.99,
  "imageUrl": "https://example.com/offer.jpg"
}
```

`POST /api/v1/offers` requiere autenticacion interna con JWT.

Response `201 Created`:

```json
{
  "id": 1,
  "title": "Oferta Familiar Magic Park",
  "description": "Hotel + entradas para 2 adultos y 2 ninos.",
  "hotelId": 1,
  "hotelName": "Hotel Magic Park",
  "boardType": "FULL_BOARD",
  "includedTickets": 4,
  "totalPrice": 399.99,
  "imageUrl": "https://example.com/offer.jpg"
}
```

## 8. Compra y reserva

Este contrato es critico porque conecta varios criterios de aceptacion.

`offerId` puede ser opcional si el cliente crea sus propias entradas.

`hotelId` puede ser opcional si solo compra entradas sin hotel.

`companions` debe incluir nombre, apellidos y fecha de nacimiento.

El backend calcula las tarifas segun edad.

Valores propuestos para `ageRange`: `CHILD`, `ADULT`, `SENIOR`.

### POST /api/v1/bookings

Crea una compra o reserva.

Request:

```json
{
  "userId": 1,
  "offerId": 1,
  "hotelId": 1,
  "boardType": "FULL_BOARD",
  "visitDate": "2026-05-22",
  "companions": [
    {
      "firstName": "Ana",
      "lastName": "Garcia",
      "birthDate": "1988-03-10"
    },
    {
      "firstName": "Lucas",
      "lastName": "Garcia",
      "birthDate": "2015-07-20"
    }
  ]
}
```

Response `201 Created`:

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
    },
    {
      "holderFullName": "Lucas Garcia",
      "ageRange": "CHILD",
      "price": 25.0
    }
  ],
  "totalPrice": 190.0,
  "emailSent": true,
  "createdAt": "2026-05-22T10:30:00"
}
```

Errores posibles:

- `400 Bad Request`: `Invalid booking data`
- `404 Not Found`: `User not found`
- `404 Not Found`: `Hotel not found`
- `409 Conflict`: `Hotel is full`
- `409 Conflict`: `A minor cannot travel without an adult`
- `500 Internal Server Error`: `Email could not be sent`

### GET /api/v1/bookings

Devuelve todas las compras o reservas.

Response `200 OK`:

```json
[
  {
    "id": 1,
    "userFullName": "David Navarro",
    "hotelName": "Hotel Magic Park",
    "visitDate": "2026-05-22",
    "totalTickets": 2,
    "totalPrice": 190.0,
    "createdAt": "2026-05-22T10:30:00"
  }
]
```

Requiere autenticacion interna con JWT.

### GET /api/v1/bookings/{id}

Devuelve el detalle completo de una reserva.

Response `200 OK`:

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
    },
    {
      "holderFullName": "Lucas Garcia",
      "ageRange": "CHILD",
      "price": 25.0
    }
  ],
  "totalPrice": 190.0,
  "emailSent": true,
  "createdAt": "2026-05-22T10:30:00"
}
```

Requiere autenticacion interna con JWT.

## 9. Turnos de empleados

### GET /api/v1/shifts

Devuelve los turnos generados.

Response `200 OK`:

```json
[
  {
    "id": 1,
    "employeeId": 1,
    "employeeFullName": "Laura Gomez",
    "employeeType": "TECHNICIAN",
    "shift": "MORNING",
    "startDate": "2026-05-01",
    "endDate": "2026-05-15"
  }
]
```

Requiere autenticacion interna con JWT.

### POST /api/v1/shifts/generate

Genera turnos automaticamente cumpliendo la rotacion de 15 dias y la cobertura minima por oficio.

Request:

```json
{
  "startDate": "2026-05-01",
  "endDate": "2026-05-31"
}
```

Response `201 Created`:

```json
{
  "message": "Shifts generated successfully",
  "startDate": "2026-05-01",
  "endDate": "2026-05-31",
  "totalGeneratedShifts": 36
}
```

Errores posibles:

- `409 Conflict`: `Not enough employees to cover required shifts`

Requiere autenticacion interna con JWT.

## 10. Mantenimiento de atracciones

Valores propuestos para `status`: `SCHEDULED`, `COMPLETED`, `CANCELLED`.

### GET /api/v1/maintenance

Devuelve la agenda de mantenimiento.

Response `200 OK`:

```json
[
  {
    "id": 1,
    "attractionId": 1,
    "attractionName": "Dragon Coaster",
    "scheduledDate": "2026-05-10",
    "status": "SCHEDULED",
    "technicians": [
      {
        "id": 1,
        "fullName": "Laura Gomez"
      }
    ]
  }
]
```

Requiere autenticacion interna con JWT.

### POST /api/v1/maintenance/generate

Genera la agenda de mantenimiento automaticamente segun el tamano de las atracciones y asocia tecnicos disponibles.

Request:

```json
{
  "startDate": "2026-05-01",
  "endDate": "2026-05-31"
}
```

Response `201 Created`:

```json
{
  "message": "Maintenance schedule generated successfully",
  "startDate": "2026-05-01",
  "endDate": "2026-05-31",
  "totalMaintenanceTasks": 12
}
```

Errores posibles:

- `409 Conflict`: `Not enough technicians available`

Requiere autenticacion interna con JWT.

## 11. Dashboard de direccion

Estos endpoints devuelven metricas funcionales calculadas por backend.

### GET /api/v1/dashboard/tickets-by-age-range?year=2026

Devuelve cuantas entradas se han vendido por rango de edad en un ano concreto.

Response `200 OK`:

```json
[
  {
    "ageRange": "CHILD",
    "ticketsSold": 45
  },
  {
    "ageRange": "ADULT",
    "ticketsSold": 130
  },
  {
    "ageRange": "SENIOR",
    "ticketsSold": 22
  }
]
```

Requiere autenticacion interna con JWT.

### GET /api/v1/dashboard/current-year-revenue

Devuelve el total ganado en el ano en curso.

Response `200 OK`:

```json
{
  "year": 2026,
  "totalRevenue": 15420.50
}
```

Requiere autenticacion interna con JWT.

### GET /api/v1/dashboard/top-hotels?year=2026

Devuelve los 3 hoteles que mas recaudan en el ano indicado.

Response `200 OK`:

```json
[
  {
    "hotelId": 1,
    "hotelName": "Hotel Magic Park",
    "revenue": 8200.0
  },
  {
    "hotelId": 2,
    "hotelName": "Hotel Adventure",
    "revenue": 6100.0
  },
  {
    "hotelId": 3,
    "hotelName": "Hotel Fantasy",
    "revenue": 3900.0
  }
]
```

Requiere autenticacion interna con JWT.

### GET /api/v1/dashboard/summary?year=2026

Endpoint opcional para devolver todas las metricas principales del dashboard en una sola llamada.

Response `200 OK`:

```json
{
  "year": 2026,
  "totalRevenue": 15420.50,
  "ticketsByAgeRange": [
    {
      "ageRange": "CHILD",
      "ticketsSold": 45
    },
    {
      "ageRange": "ADULT",
      "ticketsSold": 130
    },
    {
      "ageRange": "SENIOR",
      "ticketsSold": 22
    }
  ],
  "topHotels": [
    {
      "hotelId": 1,
      "hotelName": "Hotel Magic Park",
      "revenue": 8200.0
    },
    {
      "hotelId": 2,
      "hotelName": "Hotel Adventure",
      "revenue": 6100.0
    },
    {
      "hotelId": 3,
      "hotelName": "Hotel Fantasy",
      "revenue": 3900.0
    }
  ]
}
```

Requiere autenticacion interna con JWT.

## 12. Imagenes con Cloudinary

Este endpoint puede usarse para subir imagenes de hoteles, atracciones, ofertas o empleados.

### POST /api/v1/images/upload

Sube una imagen a Cloudinary.

Request:

```text
Content-Type: multipart/form-data
file: imagen seleccionada
folder: hotels | attractions | offers | employees
```

Response `201 Created`:

```json
{
  "imageUrl": "https://res.cloudinary.com/demo/image/upload/example.jpg",
  "publicId": "hotels/example"
}
```

Errores posibles:

- `400 Bad Request`: `Invalid image file`
- `401 Unauthorized`: `Authentication is required`
- `500 Internal Server Error`: `Image upload failed`

Requiere autenticacion interna con JWT.

## 13. Swagger/OpenAPI

El backend debe exponer Swagger/OpenAPI y debe reflejar este contrato.

Dependencia recomendada para Spring Boot:

```xml
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
  <version>2.8.6</version>
</dependency>
```

URL esperada al arrancar el backend:

```text
http://localhost:8080/swagger-ui/index.html
```

Contrato tecnico en JSON:

```text
http://localhost:8080/v3/api-docs
```
