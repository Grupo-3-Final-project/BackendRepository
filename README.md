# Backend Parque de Atracciones

Backend Spring Boot del proyecto final de gestión de parque de atracciones. Expone CRUDs, reservas, dashboard, turnos, mantenimiento, subida de imágenes y Swagger/OpenAPI.

## Stack técnico

- Java 25
- Spring Boot 4
- Maven
- Spring Data JPA
- MySQL
- H2 para tests
- Spring Validation
- Spring Security
- JWT
- Springdoc OpenAPI

## Estructura

```text
src/main/java/com/parque
|-- attraction
|-- auth
|-- booking
|-- cloudinary
|-- config
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

## Perfiles

- `dev`: MySQL local, datos demo automáticos
- `test`: H2 en memoria
- `prod`: MySQL real sin datos demo

Si no se indica perfil, el backend arranca en `dev`.

## Variables de entorno

El proyecto puede cargar variables desde un archivo `.env` en la raíz. Hay una plantilla en `.env.example`.

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

## Arranque rápido en local

### 1. Levantar MySQL

```bash
docker compose up -d mysql
```

### 2. Crear `.env`

Usa la plantilla `.env.example`. Los valores por defecto del perfil `dev` ya están alineados con el `docker-compose.yml`.

### 3. Arrancar la aplicación

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Windows:

```bash
./mvnw.cmd spring-boot:run
```

Para forzar un perfil concreto:

Linux/macOS:

```bash
SPRING_PROFILES_ACTIVE=prod ./mvnw spring-boot:run
```

Windows PowerShell:

```powershell
$env:SPRING_PROFILES_ACTIVE="prod"
./mvnw.cmd spring-boot:run
```

## Datos demo

En el perfil `dev`, si `APP_DEMO_DATA_ENABLED=true`, el backend crea automáticamente:

- usuarios demo
- hoteles demo
- atracciones demo
- empleados demo
- ofertas demo
- reservas demo
- turnos demo
- agenda de mantenimiento demo

Esto deja el dashboard y los endpoints principales con datos desde el primer arranque.

Tambien crea una credencial interna para las rutas protegidas:

- `username`: valor de `APP_DEMO_ADMIN_USERNAME`
- `password`: valor de `APP_DEMO_ADMIN_PASSWORD`

## Swagger

Con la aplicación levantada:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Testing

Tests unitarios e integración:

Linux/macOS:

```bash
./mvnw verify
```

Windows:

```bash
./mvnw.cmd verify
```

Los tests se ejecutan con el perfil `test` y base de datos H2 en memoria.

## Contrato API

La referencia funcional de la API está en:

- `docs/API_CONTRACT.md`
- `docs/CONTRACT_TESTING.md`

## Notas de operación

- Cloudinary requiere `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY` y `CLOUDINARY_API_SECRET`.
- El login interno usa la credencial definida por `APP_DEMO_ADMIN_USERNAME` y `APP_DEMO_ADMIN_PASSWORD`.
- El perfil `prod` no carga datos demo y valida el esquema de base de datos en arranque.
