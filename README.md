<div align="center">

# 🚪 La Última Puerta — Backend

[![Java](https://img.shields.io/badge/Java-25-red?style=flat-square&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.6-red?style=flat-square&logo=springboot)](https://spring.io/)
[![MySQL](https://img.shields.io/badge/MySQL-8.4-red?style=flat-square&logo=mysql)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/license-MIT-red?style=flat-square)](LICENSE)

</div>

---

<div align="center">

[🇪🇸 Español](#es) &nbsp;·&nbsp; [🇬🇧 English](#en)

</div>

---

<a id="es"></a>

<div align="right"><a href="#en">🇬🇧 English ↓</a></div>

## 🇪🇸 Español

API REST para la gestión integral de un parque de atracciones de terror.
Proyecto final del bootcamp **Factoría F5 · 2026**.

Proporciona la lógica de negocio, la seguridad y los datos
que alimentan la Home pública, el Dashboard interno,
la experiencia Mobile del visitante y el Control QR de acceso.

---

### ⚙️ Stack

![Java](https://img.shields.io/badge/Java-25-red?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.6-red?style=flat-square&logo=springboot)
![Spring Security](https://img.shields.io/badge/Spring_Security-JWT-red?style=flat-square&logo=springsecurity)
![MySQL](https://img.shields.io/badge/MySQL-8.4-red?style=flat-square&logo=mysql)
![Flyway](https://img.shields.io/badge/Flyway-migraciones-red?style=flat-square&logo=flyway)
![Cloudinary](https://img.shields.io/badge/Cloudinary-imágenes-red?style=flat-square&logo=cloudinary)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI-red?style=flat-square&logo=swagger)
![Maven](https://img.shields.io/badge/Maven-build-red?style=flat-square&logo=apachemaven)

| Elemento | Tecnología |
|----------|-----------|
| Lenguaje | Java 25 |
| Framework | Spring Boot 4.0.6 |
| Seguridad | Spring Security + JWT (jjwt 0.12.3) |
| Persistencia | Spring Data JPA · Hibernate |
| Base de datos | MySQL 8.4 (dev/prod) · H2 (test) |
| Migraciones | Flyway |
| Imágenes | Cloudinary |
| QR | ZXing 3.5.3 |
| Email | Spring Mail |
| Documentación API | Swagger / OpenAPI (springdoc 3.0.2) |
| Build | Maven |
| Utilidades | Lombok |

---

### 🗂️ Estructura del proyecto

![Arquitectura](https://img.shields.io/badge/arquitectura-domain--driven-red?style=flat-square)
![Dominios](https://img.shields.io/badge/dominios-13-red?style=flat-square)
![Perfiles](https://img.shields.io/badge/perfiles-dev_·_test_·_e2e_·_prod-red?style=flat-square)

El código se organiza por dominios de negocio:

```
src/main/java/com/parque/
├── attraction/       ← atracciones del parque
├── auth/             ← autenticación interna (JWT)
├── booking/          ← reservas, notificaciones y QR
│   └── service/
│       ├── booking/
│       ├── notification/   ← email + QR
│       └── ticket/         ← acceso mobile y validación
├── cloudinary/       ← gestión de imágenes
├── config/           ← seguridad, CORS, demo data, Flyway
├── dashboard/        ← métricas y KPIs
├── employee/         ← empleados y tipos
├── hotel/            ← hoteles y disponibilidad
├── maintenance/      ← agenda y técnicos
├── offer/            ← ofertas del parque
├── security/         ← filtros JWT
├── shift/            ← turnos
├── user/             ← usuarios y clientes
└── weather/          ← clima real de Granada
```

```
src/main/resources/
├── application.properties          ← configuración base
├── application-dev.properties      ← desarrollo local
├── application-test.properties     ← tests unitarios
├── application-e2e.properties      ← tests E2E
├── application-prod.properties     ← producción
└── db/migration/mysql/             ← migraciones Flyway (V1–V10)
```

---

### 📋 Requisitos previos

![Java](https://img.shields.io/badge/Java-25_requerido-red?style=flat-square&logo=openjdk)
![Maven](https://img.shields.io/badge/Maven-requerido-red?style=flat-square&logo=apachemaven)
![MySQL](https://img.shields.io/badge/MySQL-8.4_o_Docker-red?style=flat-square&logo=mysql)

- Java 25
- Maven
- MySQL 8.4 **o** Docker y Docker Compose

---

### 🚀 Instalación

![Docker](https://img.shields.io/badge/Docker-recomendado-red?style=flat-square&logo=docker)
![Flyway](https://img.shields.io/badge/Flyway-migraciones_automáticas-red?style=flat-square&logo=flyway)
![Demo data](https://img.shields.io/badge/demo_data-carga_automática-red?style=flat-square)

#### Opción A — Con Docker (recomendada)

Docker levanta la base de datos automáticamente:

```bash
# 1. Clonar el repositorio
git clone <url-repositorio-backend>
cd BackendRepository

# 2. Crear el archivo de entorno
cp .env.example .env
# Editar .env con los valores reales

# 3. Levantar MySQL con Docker
docker-compose up -d

# 4. Arrancar la aplicación
./mvnw spring-boot:run
```

En Windows:
```bash
./mvnw.cmd spring-boot:run
```

#### Opción B — Sin Docker

Crear la base de datos en MySQL local antes de arrancar:

```sql
CREATE DATABASE parque_atracciones;
CREATE USER 'parque_user'@'localhost' IDENTIFIED BY 'parque_password';
GRANT ALL PRIVILEGES ON parque_atracciones.* TO 'parque_user'@'localhost';
```

Después:
```bash
./mvnw spring-boot:run
```

Flyway aplicará las migraciones automáticamente al arrancar.

> Los datos de demostración se cargan solos en el perfil `dev`.
> Credenciales del administrador demo: `admin` / `admin12345`

---

### 🔐 Variables de entorno

![dotenv](https://img.shields.io/badge/.env-requerido-red?style=flat-square&logo=dotenv)
![Cloudinary](https://img.shields.io/badge/Cloudinary-imágenes-red?style=flat-square&logo=cloudinary)
![SMTP](https://img.shields.io/badge/SMTP-email-red?style=flat-square&logo=gmail)
![JWT](https://img.shields.io/badge/JWT-autenticación-red?style=flat-square&logo=jsonwebtokens)

Crear un archivo `.env` en la raíz del proyecto a partir de `.env.example`:

```env
# Base de datos
DB_URL=jdbc:mysql://localhost:3306/parque_atracciones?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Europe/Madrid
DB_USERNAME=parque_user
DB_PASSWORD=parque_password

# Cloudinary
CLOUDINARY_CLOUD_NAME=tu_cloud_name
CLOUDINARY_API_KEY=tu_api_key
CLOUDINARY_API_SECRET=tu_api_secret

# Email
MAIL_HOST=smtp.tuproveedor.com
MAIL_PORT=587
MAIL_USERNAME=tu_email
MAIL_PASSWORD=tu_contraseña_email

# URLs del frontend (para generar los QR)
APP_MOBILE_BASE_URL=http://localhost:5173/mobile
APP_ENTRY_BASE_URL=http://localhost:5173/entry

# Demo data (dev)
APP_DEMO_DATA_ENABLED=true
APP_DEMO_ADMIN_USERNAME=admin
APP_DEMO_ADMIN_EMAIL=admin@parque.local
APP_DEMO_ADMIN_PASSWORD=admin12345
```

> ⚠️ El archivo `.env` no se sube al repositorio. Nunca incluyas secretos en el código.

---

### 💻 Comandos

![Maven](https://img.shields.io/badge/Maven-comandos-red?style=flat-square&logo=apachemaven)
![Docker](https://img.shields.io/badge/Docker_Compose-MySQL-red?style=flat-square&logo=docker)

```bash
./mvnw spring-boot:run          # arrancar en perfil dev
./mvnw clean test               # ejecutar tests unitarios
./mvnw verify                   # ejecutar todos los tests (unitarios + integración)
./mvnw clean install            # compilar y empaquetar
docker-compose up -d            # levantar MySQL con Docker
docker-compose down             # detener MySQL
```

---

### 🔌 Rutas de la API

![REST](https://img.shields.io/badge/REST-API-red?style=flat-square)
![JSON](https://img.shields.io/badge/JSON-application%2Fjson-red?style=flat-square)
![Swagger](https://img.shields.io/badge/Swagger-UI_disponible-red?style=flat-square&logo=swagger)
![Módulos](https://img.shields.io/badge/módulos-13-red?style=flat-square)

La API está disponible en `http://localhost:8080`.
La documentación interactiva Swagger en:

```
http://localhost:8080/swagger-ui.html
```

Contrato en JSON:

```
http://localhost:8080/v3/api-docs
```

**Módulos disponibles:**

| Módulo | Prefijo |
|--------|---------|
| Autenticación | `/api/auth` |
| Usuarios | `/api/users` |
| Atracciones | `/api/attractions` |
| Hoteles | `/api/hotels` |
| Ofertas | `/api/offers` |
| Reservas | `/api/bookings` |
| Empleados | `/api/employees` |
| Turnos | `/api/shifts` |
| Mantenimiento | `/api/maintenance` |
| Dashboard | `/api/dashboard` |
| Imágenes | `/api/images` |
| Tickets / QR mobile | `/api/tickets` |
| Clima | `/api/weather/granada` |

La fuente de verdad del contrato es siempre `docs/API_CONTRACT.md`.

---

### 🧪 Testing

![Tests](https://img.shields.io/badge/tests-31_archivos-red?style=flat-square&logo=junit5)
![Unitarios](https://img.shields.io/badge/tipo-unitarios-red?style=flat-square)
![Integración](https://img.shields.io/badge/tipo-integración-red?style=flat-square)
![Contrato](https://img.shields.io/badge/tipo-contrato-red?style=flat-square)
![E2E](https://img.shields.io/badge/tipo-E2E-red?style=flat-square)
![H2](https://img.shields.io/badge/BD_test-H2_en_memoria-red?style=flat-square)

El proyecto tiene 31 archivos de test que cubren:

| Tipo | Qué cubre |
|------|----------|
| Unitarios | Servicios y reglas de negocio |
| Integración | Controladores y repositorios |
| Contrato | Validación del contrato API con frontend |
| E2E | Flujos completos de reserva |
| Migración | Integridad del esquema de base de datos |

```bash
./mvnw clean test        # tests unitarios (perfil test, H2)
./mvnw verify            # todos los tests incluidos los de integración
```

---

### 📚 Documentación técnica

![Markdown](https://img.shields.io/badge/formato-Markdown-red?style=flat-square&logo=markdown)
![API Contract](https://img.shields.io/badge/contrato_API-fuente_de_verdad-red?style=flat-square)
![Swagger](https://img.shields.io/badge/Swagger-sincronizado-red?style=flat-square&logo=swagger)

| Archivo | Contenido |
|---------|----------|
| `docs/API_CONTRACT.md` | Fuente de verdad del contrato API |
| `docs/CONTRACT_TESTING.md` | Estrategia y casos mínimos de testing |
| `docs/API_INTEGRATION_GUIDE.md` | Guía de integración con el frontend |
| `docs/DEMO_CHECKLIST.md` | Lista de verificación para la demo |

---

### 👥 Equipo

![Factoría F5](https://img.shields.io/badge/Factoría_F5-2026-red?style=flat-square)
![Scrum](https://img.shields.io/badge/metodología-Scrum-red?style=flat-square)

Proyecto desarrollado por el equipo de **Factoría F5 · 2026**.

| Rol | Persona |
|-----|---------|
| Product Owner | Alberto |
| Scrum Master | Xavier |
| Desarrolladora | — |
| Desarrolladora | — |
| Desarrolladora / Líbero | David |

---

<div align="center"><a href="#es">⬆ Volver al inicio</a> · <a href="#en">🇬🇧 English ↓</a></div>

---
---

<a id="en"></a>

<div align="right"><a href="#es">🇪🇸 Español ↑</a></div>

## 🇬🇧 English

REST API for the full management of a horror theme park.
Final project of the **Factoría F5 · 2026** bootcamp.

Provides the business logic, security and data that power
the public Home, the internal Dashboard,
the visitor Mobile experience and the QR entry validation.

---

### ⚙️ Stack

![Java](https://img.shields.io/badge/Java-25-red?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.6-red?style=flat-square&logo=springboot)
![Spring Security](https://img.shields.io/badge/Spring_Security-JWT-red?style=flat-square&logo=springsecurity)
![MySQL](https://img.shields.io/badge/MySQL-8.4-red?style=flat-square&logo=mysql)
![Flyway](https://img.shields.io/badge/Flyway-migrations-red?style=flat-square&logo=flyway)
![Cloudinary](https://img.shields.io/badge/Cloudinary-images-red?style=flat-square&logo=cloudinary)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI-red?style=flat-square&logo=swagger)
![Maven](https://img.shields.io/badge/Maven-build-red?style=flat-square&logo=apachemaven)

| Element | Technology |
|---------|-----------|
| Language | Java 25 |
| Framework | Spring Boot 4.0.6 |
| Security | Spring Security + JWT (jjwt 0.12.3) |
| Persistence | Spring Data JPA · Hibernate |
| Database | MySQL 8.4 (dev/prod) · H2 (test) |
| Migrations | Flyway |
| Images | Cloudinary |
| QR generation | ZXing 3.5.3 |
| Email | Spring Mail |
| API documentation | Swagger / OpenAPI (springdoc 3.0.2) |
| Build | Maven |
| Utilities | Lombok |

---

### 🗂️ Project Structure

![Architecture](https://img.shields.io/badge/architecture-domain--driven-red?style=flat-square)
![Domains](https://img.shields.io/badge/domains-13-red?style=flat-square)
![Profiles](https://img.shields.io/badge/profiles-dev_·_test_·_e2e_·_prod-red?style=flat-square)

Code is organised by business domain:

```
src/main/java/com/parque/
├── attraction/       ← park attractions
├── auth/             ← internal authentication (JWT)
├── booking/          ← bookings, notifications and QR
│   └── service/
│       ├── booking/
│       ├── notification/   ← email + QR
│       └── ticket/         ← mobile access and entry validation
├── cloudinary/       ← image management
├── config/           ← security, CORS, demo data, Flyway
├── dashboard/        ← metrics and KPIs
├── employee/         ← employees and types
├── hotel/            ← hotels and availability
├── maintenance/      ← schedule and technicians
├── offer/            ← park offers
├── security/         ← JWT filters
├── shift/            ← shifts
├── user/             ← users and customers
└── weather/          ← live Granada weather
```

```
src/main/resources/
├── application.properties          ← base configuration
├── application-dev.properties      ← local development
├── application-test.properties     ← unit tests
├── application-e2e.properties      ← E2E tests
├── application-prod.properties     ← production
└── db/migration/mysql/             ← Flyway migrations (V1–V10)
```

---

### 📋 Prerequisites

![Java](https://img.shields.io/badge/Java-25_required-red?style=flat-square&logo=openjdk)
![Maven](https://img.shields.io/badge/Maven-required-red?style=flat-square&logo=apachemaven)
![MySQL](https://img.shields.io/badge/MySQL-8.4_or_Docker-red?style=flat-square&logo=mysql)

- Java 25
- Maven
- MySQL 8.4 **or** Docker and Docker Compose

---

### 🚀 Installation

![Docker](https://img.shields.io/badge/Docker-recommended-red?style=flat-square&logo=docker)
![Flyway](https://img.shields.io/badge/Flyway-auto_migration-red?style=flat-square&logo=flyway)
![Demo data](https://img.shields.io/badge/demo_data-auto_load-red?style=flat-square)

#### Option A — With Docker (recommended)

Docker starts the database automatically:

```bash
# 1. Clone the repository
git clone <backend-repository-url>
cd BackendRepository

# 2. Create the environment file
cp .env.example .env
# Edit .env with real values

# 3. Start MySQL with Docker
docker-compose up -d

# 4. Start the application
./mvnw spring-boot:run
```

On Windows:
```bash
./mvnw.cmd spring-boot:run
```

#### Option B — Without Docker

Create the database in your local MySQL before starting:

```sql
CREATE DATABASE parque_atracciones;
CREATE USER 'parque_user'@'localhost' IDENTIFIED BY 'parque_password';
GRANT ALL PRIVILEGES ON parque_atracciones.* TO 'parque_user'@'localhost';
```

Then:
```bash
./mvnw spring-boot:run
```

Flyway will apply all migrations automatically on startup.

> Demo data loads automatically in the `dev` profile.
> Demo admin credentials: `admin` / `admin12345`

---

### 🔐 Environment Variables

![dotenv](https://img.shields.io/badge/.env-required-red?style=flat-square&logo=dotenv)
![Cloudinary](https://img.shields.io/badge/Cloudinary-images-red?style=flat-square&logo=cloudinary)
![SMTP](https://img.shields.io/badge/SMTP-email-red?style=flat-square&logo=gmail)
![JWT](https://img.shields.io/badge/JWT-authentication-red?style=flat-square&logo=jsonwebtokens)

Create a `.env` file in the project root from `.env.example`:

```env
# Database
DB_URL=jdbc:mysql://localhost:3306/parque_atracciones?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Europe/Madrid
DB_USERNAME=parque_user
DB_PASSWORD=parque_password

# Cloudinary
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret

# Email
MAIL_HOST=smtp.yourprovider.com
MAIL_PORT=587
MAIL_USERNAME=your_email
MAIL_PASSWORD=your_email_password

# Frontend URLs (used to generate QR codes)
APP_MOBILE_BASE_URL=http://localhost:5173/mobile
APP_ENTRY_BASE_URL=http://localhost:5173/entry

# Demo data (dev)
APP_DEMO_DATA_ENABLED=true
APP_DEMO_ADMIN_USERNAME=admin
APP_DEMO_ADMIN_EMAIL=admin@parque.local
APP_DEMO_ADMIN_PASSWORD=admin12345
```

> ⚠️ The `.env` file must never be committed. Never hardcode secrets in the codebase.

---

### 💻 Commands

![Maven](https://img.shields.io/badge/Maven-commands-red?style=flat-square&logo=apachemaven)
![Docker](https://img.shields.io/badge/Docker_Compose-MySQL-red?style=flat-square&logo=docker)

```bash
./mvnw spring-boot:run          # start in dev profile
./mvnw clean test               # run unit tests
./mvnw verify                   # run all tests (unit + integration)
./mvnw clean install            # compile and package
docker-compose up -d            # start MySQL with Docker
docker-compose down             # stop MySQL
```

---

### 🔌 API Endpoints

![REST](https://img.shields.io/badge/REST-API-red?style=flat-square)
![JSON](https://img.shields.io/badge/JSON-application%2Fjson-red?style=flat-square)
![Swagger](https://img.shields.io/badge/Swagger-UI_available-red?style=flat-square&logo=swagger)
![Modules](https://img.shields.io/badge/modules-13-red?style=flat-square)

The API is available at `http://localhost:8080`.
Interactive Swagger documentation at:

```
http://localhost:8080/swagger-ui.html
```

Contract in JSON:

```
http://localhost:8080/v3/api-docs
```

**Available modules:**

| Module | Prefix |
|--------|--------|
| Authentication | `/api/auth` |
| Users | `/api/users` |
| Attractions | `/api/attractions` |
| Hotels | `/api/hotels` |
| Offers | `/api/offers` |
| Bookings | `/api/bookings` |
| Employees | `/api/employees` |
| Shifts | `/api/shifts` |
| Maintenance | `/api/maintenance` |
| Dashboard | `/api/dashboard` |
| Images | `/api/images` |
| Tickets / QR mobile | `/api/tickets` |
| Weather | `/api/weather/granada` |

The source of truth for the contract is always `docs/API_CONTRACT.md`.

---

### 🧪 Testing

![Tests](https://img.shields.io/badge/tests-31_files-red?style=flat-square&logo=junit5)
![Unit](https://img.shields.io/badge/type-unit-red?style=flat-square)
![Integration](https://img.shields.io/badge/type-integration-red?style=flat-square)
![Contract](https://img.shields.io/badge/type-contract-red?style=flat-square)
![E2E](https://img.shields.io/badge/type-E2E-red?style=flat-square)
![H2](https://img.shields.io/badge/test_DB-H2_in_memory-red?style=flat-square)

The project has 31 test files covering:

| Type | Scope |
|------|-------|
| Unit | Services and business rules |
| Integration | Controllers and repositories |
| Contract | API contract validation with frontend |
| E2E | Full booking flows |
| Migration | Database schema integrity |

```bash
./mvnw clean test        # unit tests (test profile, H2)
./mvnw verify            # all tests including integration
```

---

### 📚 Technical Documentation

![Markdown](https://img.shields.io/badge/format-Markdown-red?style=flat-square&logo=markdown)
![API Contract](https://img.shields.io/badge/API_contract-source_of_truth-red?style=flat-square)
![Swagger](https://img.shields.io/badge/Swagger-synced-red?style=flat-square&logo=swagger)

| File | Content |
|------|---------|
| `docs/API_CONTRACT.md` | API contract source of truth |
| `docs/CONTRACT_TESTING.md` | Testing strategy and minimum cases |
| `docs/API_INTEGRATION_GUIDE.md` | Integration guide for the frontend |
| `docs/DEMO_CHECKLIST.md` | Demo verification checklist |

---

### 👥 Team

![Factoría F5](https://img.shields.io/badge/Factoría_F5-2026-red?style=flat-square)
![Scrum](https://img.shields.io/badge/methodology-Scrum-red?style=flat-square)

Project developed by the **Factoría F5 · 2026** team.

| Role | Person |
|------|--------|
| Product Owner | Alberto |
| Scrum Master | Xavier |
| Developer | — |
| Developer | — |
| Developer / Support Libero | David |

---

<div align="center"><a href="#en">⬆ Back to top</a> · <a href="#es">🇪🇸 Español ↑</a></div>

---

<div align="center">

**La Última Puerta · Factoría F5 · 2026**

</div>
