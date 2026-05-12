<div align="center">

# 🚪 La Última Puerta

**¿Te atreves a cruzarla?**

[![Java](https://img.shields.io/badge/Java-25-red?style=flat-square&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.6-red?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19-red?style=flat-square&logo=react)](https://react.dev/)
[![Vite](https://img.shields.io/badge/Vite-8-red?style=flat-square&logo=vite)](https://vitejs.dev/)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-4-red?style=flat-square&logo=tailwindcss)](https://tailwindcss.com/)

</div>

---

<div align="center">

[🇪🇸 Leer en Español](#es) &nbsp;·&nbsp; [🇬🇧 Read in English](#en)

</div>

---

<a id="es"></a>

<div align="right"><a href="#en">🇬🇧 English version ↓</a></div>

## 🇪🇸 La Última Puerta

Aplicación web de gestión y venta para un parque de atracciones de terror.
Construida como proyecto final del bootcamp de **Factoría F5 · 2026**.

El sistema ofrece tres experiencias separadas e independientes:

- **Home pública** — escaparate comercial orientado a venta y presentación del parque.
- **Dashboard interno** — panel operativo para taquilla y administración.
- **Mobile visitante** — guía interactiva accesible mediante QR desde el parque.

---

### Índice

- [Stack tecnológico](#stack-es)
- [Estructura del proyecto](#estructura-es)
- [Instalación](#instalacion-es)
- [Variables de entorno](#variables-es)
- [Comandos principales](#comandos-es)
- [Testing](#testing-es)
- [API y Swagger](#api-es)
- [Equipo](#equipo-es)

---

<a id="stack-es"></a>
### Stack tecnológico

#### Backend

| Elemento | Tecnología |
|----------|-----------|
| Lenguaje | Java 25 |
| Framework | Spring Boot 4.0.6 |
| Build | Maven |
| Base de datos | MySQL (producción) / H2 (desarrollo) |
| Imágenes | Cloudinary |
| Documentación API | Swagger / OpenAPI (springdoc 3.0.2) |

#### Frontend

| Elemento | Tecnología |
|----------|-----------|
| Framework | React 19 + Vite 8 |
| Lenguaje | JavaScript / JSX |
| Estilos | Tailwind CSS 4 |
| HTTP client | Axios |
| Routing | React Router DOM 7 |
| Iconos | Lucide React / React Icons |
| Gestor de paquetes | npm |
| Testing unitario | Vitest + React Testing Library |
| Testing E2E | Playwright |

---

<a id="estructura-es"></a>
### Estructura del proyecto

El sistema se divide en dos repositorios independientes.

#### Backend

```
backend-repository/
├── docs/
│   ├── API_CONTRACT.md
│   └── CONTRACT_TESTING.md
└── src/
    └── main/java/com/parque/
        ├── attraction/
        ├── booking/
        ├── dashboard/
        ├── employee/
        ├── hotel/
        ├── maintenance/
        ├── shift/
        └── user/
```

#### Frontend

```
frontend-project/
├── docs/
│   ├── API_CONTRACT.md
│   ├── CONTRACT_TESTING.md
│   └── FRONTEND_CONTEXT.md
└── src/
    ├── api/            ← llamadas HTTP centralizadas
    ├── assets/
    ├── components/
    │   ├── dashboard/
    │   ├── mobileExperience/
    │   └── ui/
    ├── features/       ← lógica por dominio
    │   ├── users/
    │   ├── hotels/
    │   ├── attractions/
    │   ├── employees/
    │   ├── bookings/
    │   └── dashboard/
    ├── hooks/
    ├── layouts/
    ├── pages/          ← HomePage · DashboardPage · MobilePage
    └── router/
```

---

<a id="instalacion-es"></a>
### Instalación

#### Backend

**Requisitos previos:** Java 25, Maven.

```bash
# 1. Clonar el repositorio
git clone <url-repositorio-backend>
cd backend-repository

# 2. Configurar las variables de entorno (ver sección Variables de entorno)
cp .env.example .env

# 3. Arrancar la aplicación
./mvnw spring-boot:run
```

En Windows:

```bash
./mvnw.cmd spring-boot:run
```

#### Frontend

**Requisitos previos:** Node.js, npm.

```bash
# 1. Clonar el repositorio
git clone <url-repositorio-frontend>
cd frontend-project

# 2. Instalar dependencias
npm install

# 3. Configurar las variables de entorno (ver sección Variables de entorno)
cp .env.example .env

# 4. Arrancar en modo desarrollo
npm run dev
```

---

<a id="variables-es"></a>
### Variables de entorno

#### Backend — `.env`

```env
DB_URL=jdbc:mysql://localhost:3306/nombre_base_datos
DB_USERNAME=tu_usuario
DB_PASSWORD=tu_contraseña
CLOUDINARY_CLOUD_NAME=tu_cloud_name
CLOUDINARY_API_KEY=tu_api_key
CLOUDINARY_API_SECRET=tu_api_secret
MAIL_USERNAME=tu_email
MAIL_PASSWORD=tu_contraseña_email
```

#### Frontend — `.env`

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

> ⚠️ El archivo `.env` no se sube al repositorio. Usar siempre `.env.example` como plantilla.

---

<a id="comandos-es"></a>
### Comandos principales

#### Backend

```bash
./mvnw spring-boot:run     # arrancar la aplicación
./mvnw clean test          # ejecutar los tests
./mvnw clean install       # compilar y empaquetar
```

#### Frontend

```bash
npm run dev        # servidor de desarrollo en http://localhost:5173
npm run build      # compilar para producción
npm run preview    # previsualizar la build de producción
npm run lint       # análisis estático del código
npm run test       # ejecutar tests unitarios
npx playwright test  # ejecutar tests E2E
```

---

<a id="testing-es"></a>
### Testing

#### Backend

- **Tests unitarios:** servicios, reglas de negocio y validaciones.
- **Tests de integración:** controladores, repositorios y flujos de reserva.
- **Tests de contrato:** validación del contrato API entre frontend y backend.

Se aplica TDD en reglas de negocio críticas.

```bash
./mvnw clean test
```

#### Frontend

- **Tests unitarios:** componentes, servicios API y manejo de errores.
- **Tests E2E (Playwright):**
  - Cargar la home.
  - Ver hoteles disponibles.
  - Crear una reserva.
  - Ver el dashboard.
  - Validar errores de negocio desde backend.

```bash
npm run test
npx playwright test
```

Una tarea no se considera terminada hasta que los tests relevantes pasan.
Consultar `docs/CONTRACT_TESTING.md` para los casos de contrato mínimos.

---

<a id="api-es"></a>
### API y Swagger

El backend expone documentación interactiva Swagger/OpenAPI en:

```
http://localhost:8080/swagger-ui/index.html
```

Contrato API completo en JSON:

```
http://localhost:8080/v3/api-docs
```

La fuente de verdad del contrato es siempre `docs/API_CONTRACT.md`.
No se modifican endpoints, campos JSON ni estructuras de request/response sin actualizar ese documento y comunicarlo al equipo.

---

<a id="equipo-es"></a>
### Equipo

Proyecto desarrollado por el equipo de **Factoría F5 · 2026**.

| Rol | Persona |
|-----|---------|
| Product Owner | Alberto |
| Scrum Master | Xavier |
| Desarrollador / Líbero de soporte | David |
| Desarrolladora | Alba |
| Desarrollador | JuanLu |

---

<div align="right"><a href="#en">🇬🇧 English version ↓</a></div>
<div align="center"><a href="#es">⬆ Volver al inicio</a></div>

---
---

<a id="en"></a>

<div align="right"><a href="#es">🇪🇸 Versión en Español ↑</a></div>

## 🇬🇧 La Última Puerta

Web application for management and ticket sales for a horror theme park.
Built as the final project of the **Factoría F5 · 2026** bootcamp.

The system provides three separate and independent experiences:

- **Public home** — commercial showcase oriented to sales and park presentation.
- **Internal dashboard** — operational panel for ticket office and administration.
- **Visitor mobile** — interactive guide accessible via QR from inside the park.

---

### Table of Contents

- [Tech Stack](#stack-en)
- [Project Structure](#structure-en)
- [Installation](#installation-en)
- [Environment Variables](#variables-en)
- [Main Commands](#commands-en)
- [Testing](#testing-en)
- [API and Swagger](#api-en)
- [Team](#team-en)

---

<a id="stack-en"></a>
### Tech Stack

#### Backend

| Element | Technology |
|---------|-----------|
| Language | Java 25 |
| Framework | Spring Boot 4.0.6 |
| Build | Maven |
| Database | MySQL (production) / H2 (development) |
| Images | Cloudinary |
| API Documentation | Swagger / OpenAPI (springdoc 3.0.2) |

#### Frontend

| Element | Technology |
|---------|-----------|
| Framework | React 19 + Vite 8 |
| Language | JavaScript / JSX |
| Styles | Tailwind CSS 4 |
| HTTP client | Axios |
| Routing | React Router DOM 7 |
| Icons | Lucide React / React Icons |
| Package manager | npm |
| Unit testing | Vitest + React Testing Library |
| E2E testing | Playwright |

---

<a id="structure-en"></a>
### Project Structure

The system is split into two independent repositories.

#### Backend

```
backend-repository/
├── docs/
│   ├── API_CONTRACT.md
│   └── CONTRACT_TESTING.md
└── src/
    └── main/java/com/parque/
        ├── attraction/
        ├── booking/
        ├── dashboard/
        ├── employee/
        ├── hotel/
        ├── maintenance/
        ├── shift/
        └── user/
```

#### Frontend

```
frontend-project/
├── docs/
│   ├── API_CONTRACT.md
│   ├── CONTRACT_TESTING.md
│   └── FRONTEND_CONTEXT.md
└── src/
    ├── api/            ← centralised HTTP calls
    ├── assets/
    ├── components/
    │   ├── dashboard/
    │   ├── mobileExperience/
    │   └── ui/
    ├── features/       ← domain logic
    │   ├── users/
    │   ├── hotels/
    │   ├── attractions/
    │   ├── employees/
    │   ├── bookings/
    │   └── dashboard/
    ├── hooks/
    ├── layouts/
    ├── pages/          ← HomePage · DashboardPage · MobilePage
    └── router/
```

---

<a id="installation-en"></a>
### Installation

#### Backend

**Prerequisites:** Java 25, Maven.

```bash
# 1. Clone the repository
git clone <backend-repository-url>
cd backend-repository

# 2. Set up environment variables (see Environment Variables section)
cp .env.example .env

# 3. Start the application
./mvnw spring-boot:run
```

On Windows:

```bash
./mvnw.cmd spring-boot:run
```

#### Frontend

**Prerequisites:** Node.js, npm.

```bash
# 1. Clone the repository
git clone <frontend-repository-url>
cd frontend-project

# 2. Install dependencies
npm install

# 3. Set up environment variables (see Environment Variables section)
cp .env.example .env

# 4. Start in development mode
npm run dev
```

---

<a id="variables-en"></a>
### Environment Variables

#### Backend — `.env`

```env
DB_URL=jdbc:mysql://localhost:3306/your_database_name
DB_USERNAME=your_username
DB_PASSWORD=your_password
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
MAIL_USERNAME=your_email
MAIL_PASSWORD=your_email_password
```

#### Frontend — `.env`

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

> ⚠️ The `.env` file must never be committed to the repository. Always use `.env.example` as a template.

---

<a id="commands-en"></a>
### Main Commands

#### Backend

```bash
./mvnw spring-boot:run     # start the application
./mvnw clean test          # run the test suite
./mvnw clean install       # compile and package
```

#### Frontend

```bash
npm run dev        # development server at http://localhost:5173
npm run build      # production build
npm run preview    # preview the production build
npm run lint       # static code analysis
npm run test       # run unit tests
npx playwright test  # run E2E tests
```

---

<a id="testing-en"></a>
### Testing

#### Backend

- **Unit tests:** services, business rules and validations.
- **Integration tests:** controllers, repositories and booking flows.
- **Contract tests:** API contract validation between frontend and backend.

TDD is applied for critical business rules.

```bash
./mvnw clean test
```

#### Backend

- **Unit tests:** components, API services and error handling.
- **E2E tests (Playwright):**
  - Load the home page.
  - View available hotels.
  - Create a booking.
  - View the dashboard.
  - Validate business rule errors returned from the backend.

```bash
npm run test
npx playwright test
```

A task is not considered done until all relevant tests pass.
See `docs/CONTRACT_TESTING.md` for the minimum contract test cases.

---

<a id="api-en"></a>
### API and Swagger

The backend exposes interactive Swagger/OpenAPI documentation at:

```
http://localhost:8080/swagger-ui/index.html
```

Full API contract in JSON:

```
http://localhost:8080/v3/api-docs
```

The source of truth for the contract is always `docs/API_CONTRACT.md`.
Endpoints, JSON field names and request/response structures must not be changed without updating that document and communicating the change to the team.

---

<a id="team-en"></a>
### Team

Project developed by the **Factoría F5 · 2026** team.

| Role | Person |
|------|--------|
| Product Owner | Alberto |
| Scrum Master | Xavier |
| Developer / Support Libero | David |
| Developer | Alba |
| Developer | JuanLu |

---

<div align="right"><a href="#es">🇪🇸 Versión en Español ↑</a></div>
<div align="center"><a href="#en">⬆ Back to top</a></div>

---

<div align="center">

**La Última Puerta · Factoría F5 · 2026**

</div>
