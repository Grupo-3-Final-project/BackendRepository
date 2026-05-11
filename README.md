# Backend Parque de Atracciones

Backend Spring Boot del proyecto final de gestion del parque de atracciones. Expone CRUDs, reservas, dashboard, turnos, mantenimiento, subida de imagenes con Cloudinary, autenticacion interna con JWT y documentacion OpenAPI.

## Objetivo del repositorio

Este repositorio resuelve la parte backend del sistema usado por:

- taquilla
- administracion interna
- dashboard de direccion
- integracion con el frontend React

La API sigue el contrato definido por el equipo en:

- `docs/API_CONTRACT.md`
- `docs/CONTRACT_TESTING.md`

## Stack tecnico

- Java 25
- Spring Boot 4
- Maven
- Spring Data JPA
- MySQL
- H2 para tests y perfil E2E
- Spring Validation
- Spring Security
- JWT
- Flyway
- Springdoc OpenAPI
- Cloudinary

## Estructura

```text
src/main/java/com/parque
|-- attraction
|-- auth
|-- booking
|-- cloudinary
|-- config
|-- db/migration
|-- dashboard
|-- employee
|-- exception
|-- hotel
|-- maintenance
|-- offer
|-- security
|-- shift
|-- user
`-- validation
```

## Perfiles disponibles

- `dev`: MySQL local con datos demo
- `test`: H2 en memoria para tests unitarios e integracion
- `prod`: MySQL real sin datos demo
- `e2e`: H2 en memoria con datos demo para Playwright

Si no se indica perfil, la aplicacion arranca en `dev`.

## Variables de entorno

El proyecto puede cargar variables desde un archivo `.env` en la raiz. La plantilla base esta en `.env.example`.

Variables principales:

```text
SPRING_PROFILES_ACTIVE
SERVER_PORT
DB_URL
DB_USERNAME
DB_PASSWORD
CLOUDINARY_CLOUD_NAME
CLOUDINARY_API_KEY
CLOUDINARY_API_SECRET
MAIL_USERNAME
MAIL_PASSWORD
JWT_SECRET
JWT_EXPIRATION
APP_DEMO_DATA_ENABLED
APP_DEMO_ADMIN_USERNAME
APP_DEMO_ADMIN_EMAIL
APP_DEMO_ADMIN_PASSWORD
```

## Instalacion y arranque

### 1. Preparar variables

Crear un archivo `.env` en la raiz usando `.env.example` como referencia.

### 2. Levantar MySQL para `dev`

```bash
docker compose up -d mysql
```

### 3. Arrancar la aplicacion

Linux o macOS:

```bash
./mvnw spring-boot:run
```

Windows:

```bash
./mvnw.cmd spring-boot:run
```

### 4. Forzar un perfil concreto

Linux o macOS:

```bash
SPRING_PROFILES_ACTIVE=prod ./mvnw spring-boot:run
```

Windows PowerShell:

```powershell
$env:SPRING_PROFILES_ACTIVE="prod"
./mvnw.cmd spring-boot:run
```

## Datos demo

En `dev` y `e2e`, si `APP_DEMO_DATA_ENABLED=true`, el backend crea automaticamente:

- usuarios
- hoteles
- atracciones
- empleados
- ofertas
- reservas
- turnos
- agenda de mantenimiento

Tambien crea una credencial interna para rutas protegidas:

- usuario: `APP_DEMO_ADMIN_USERNAME`
- password: `APP_DEMO_ADMIN_PASSWORD`

Con los valores por defecto de `.env.example`:

- usuario: `admin`
- password: `admin12345`

## Swagger y contrato API

Con la aplicacion levantada:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

La fuente funcional del contrato sigue siendo:

- `docs/API_CONTRACT.md`
- `docs/CONTRACT_TESTING.md`

## Migraciones de base de datos

El esquema queda versionado con Flyway.

Ubicaciones:

- `src/main/resources/db/migration/mysql` para la aplicacion
- `src/main/resources/db/migration/h2` para la validacion automatica con H2

Reglas de trabajo:

- los cambios de esquema no se hacen ya con `ddl-auto=update`
- `dev` y `prod` arrancan validando el esquema contra las entidades
- la primera ejecucion sobre una base MySQL ya existente se baselinea automaticamente
- una base nueva aplica `V1__init_schema.sql` antes de levantar la aplicacion

## Testing

Tests unitarios e integracion:

```bash
./mvnw verify
```

Windows:

```bash
./mvnw.cmd verify
```

Tests E2E del sistema completo:

- el frontend ejecuta Playwright desde el repo hermano `../FrontendProject`
- para ese flujo, este backend usa el perfil `e2e`

## Endpoints funcionales principales

- `POST /api/auth/login`
- `POST /api/users`
- `GET /api/hotels`
- `GET /api/attractions`
- `GET /api/offers`
- `POST /api/bookings`
- `GET /api/bookings`
- `GET /api/bookings/{id}`
- `GET /api/dashboard/summary`

## Notas de operacion

- Cloudinary requiere `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY` y `CLOUDINARY_API_SECRET`
- las rutas internas usan JWT y rol `ADMIN`
- el frontend en desarrollo debe apuntar a `http://localhost:8080/api` o `http://127.0.0.1:8080/api`
- el CORS del backend permite puertos de desarrollo `3000`, `3001`, `4173` y `5173`

## Checklist de demo

La checklist final de demo esta en:

- `docs/DEMO_CHECKLIST.md`
