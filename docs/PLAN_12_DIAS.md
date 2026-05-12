[200~# Plan 12 Días: Backend + Frontend Integration Ready

## TL;DR
El backend está ~95% listo (todos módulos implementados, tests pasando). La estrategia es: **Pulir backend para integración (días 1-5)** → **Avanzar frontend React e integrarlo (días 6-11)** → **Testing E2E y ajustes (día 12)**.

---

## Timeline Detallado

### **FASE 1: Backend Integration-Ready (Días 1-5)**

#### **Día 1: CORS & Security Setup** (6 horas)
- **Tarea 1.1**: Configurar CORS en `WebConfig` para producción (orígenes permitidos)
  - Tiempo: 1h | Criteria: GET/POST/OPTIONS funcionando desde localhost:3000 y producción
- **Tarea 1.2**: Validar JWT/autenticación si está pendiente (revisar UserController)
  - Tiempo: 2h | Criteria: Tokens generados, validados sin CORS issues
- **Tarea 1.3**: Documentar endpoints en Swagger (`/swagger-ui.html`)
  - Tiempo: 2h | Criteria: Todos 30+ endpoints visibles y documentados
- **Tarea 1.4**: Crear script de inicialización BD (seed data para demo)
  - Tiempo: 1h | Criteria: Script genera 5 usuarios, 3 hoteles, 10 atracciones

**Dependencias**: Ninguna | **Blocker**: Revisar si Spring Security/JWT ya existe

---

#### **Día 2: Error Handling & Validation** (7 horas)
- **Tarea 2.1**: Estandarizar respuestas de error (ErrorResponse DTO con código HTTP)
  - Tiempo: 2h | Criteria: Todos endpoints retornan formato `{error, message, timestamp, code}`
- **Tarea 2.2**: Agregar validaciones Input (edad mínima, fechas válidas, etc.)
  - Tiempo: 3h | Criteria: Booking rechaza menores sin guardián, hotel rechaza fechas inválidas
- **Tarea 2.3**: Tests unitarios para validaciones
  - Tiempo: 2h | Criteria: 10+ casos de error cubiertos (edad, duración, capacidad)

**Dependencias**: Bloqueado por Día 1 Tarea 1.2 (si hay JWT)

---

#### **Día 3: Integration Testing** (8 horas)
- **Tarea 3.1**: Tests de integración E2E para flujo booking (user → hotel → attraction → booking)
  - Tiempo: 4h | Criteria: Test simula: crear user → buscar hotel → buscar atracción → crear booking → confirmar
- **Tarea 3.2**: Tests de integración para dashboard (cálculos de revenue, métricas)
  - Tiempo: 2h | Criteria: Dashboard retorna datos correctos post-booking
- **Tarea 3.3**: Contract testing (asegurar respuestas match API_CONTRACT.md)
  - Tiempo: 2h | Criteria: 20+ tests verifican estructura de respuesta matches spec

**Dependencias**: Depende Día 2 (validaciones deben estar en place)

---

#### **Día 4: Docker & Environment Setup** (6 horas)
- **Tarea 4.1**: Crear Dockerfile y docker-compose.yml (Java + MySQL)
  - Tiempo: 3h | Criteria: `docker-compose up` inicia backend + MySQL en puerto 8080
- **Tarea 4.2**: Configurar application.properties para dev/test/prod
  - Tiempo: 1.5h | Criteria: Profiles funcionan, logs diferenciados
- **Tarea 4.3**: Documentar setup local en README
  - Tiempo: 1.5h | Criteria: Nuevo dev sigue guía y tiene app corriendo en 15min

**Dependencias**: Ninguna (paralel con Día 3)

---

#### **Día 5: Documentation & API Export** (5 horas)
- **Tarea 5.1**: Generar Postman collection automático desde Swagger
  - Tiempo: 1h | Criteria: Archivo .json con todos 30+ endpoints listos para import
- **Tarea 5.2**: Crear API_INTEGRATION_GUIDE.md (variables, ejemplos, casos de uso)
  - Tiempo: 2h | Criteria: Frontend dev entiende cómo usar cada endpoint
- **Tarea 5.3**: Alinear rutas API con el contrato actual
  - Tiempo: 1h | Criteria: `/api/users`, `/api/hotels`, `/api/attractions`, `/api/bookings` etc
- **Tarea 5.4**: QA final backend: todos tests pasan, Swagger accesible
  - Tiempo: 1h | Criteria: `mvn test` = 100% pases, Swagger visible

**Dependencias**: Días 1-4 completados

---

### **FASE 2: Frontend Implementation & Integration (Días 6-10)**

#### **Día 6: Setup & Auth Pages** (7 horas)
- **Tarea 6.1**: Estructura proyecto React (si no existe): componentes, pages, services
  - Tiempo: 2h | Criteria: Carpetas `src/components`, `src/pages`, `src/services` organizadas
- **Tarea 6.2**: Servicio API (axios instance + interceptor para CORS headers)
  - Tiempo: 1.5h | Criteria: `apiService.ts` configurado, maneja base URL + auth headers
- **Tarea 6.3**: Login page conectado a backend
  - Tiempo: 2h | Criteria: Username/password → backend → token guardado en localStorage
- **Tarea 6.4**: Signup page conectada
  - Tiempo: 1.5h | Criteria: Form validación local → POST /api/users → Redirect login

**Dependencias**: FASE 1 completada (backend listo)

---

#### **Día 7: Core Pages - Hotel & Attractions** (8 horas)
- **Tarea 7.1**: Hotel search page (GET /api/hotels + disponibilidad)
  - Tiempo: 3h | Criteria: Listar hoteles, filtrar por fecha/capacidad, ver detalles
- **Tarea 7.2**: Attractions page (GET /api/attractions)
  - Tiempo: 2h | Criteria: Listar, filtrar por estado (OPEN), ver descripciones
- **Tarea 7.3**: Hotel detail + booking form (POST /api/bookings)
  - Tiempo: 2h | Criteria: Seleccionar hotel + fecha → enviar booking → confirmación
- **Tarea 7.4**: Attraction detail page (no booking, solo info)
  - Tiempo: 1h | Criteria: Mostrar detalles, características, horarios

**Dependencias**: Día 6 completado

---

#### **Día 8: Dashboard & Advanced Features** (7 horas)
- **Tarea 8.1**: Dashboard page (si usuario es admin: GET /api/dashboard/summary?year=YYYY)
  - Tiempo: 3h | Criteria: Gráficas revenue, tickets por edad, hoteles top
- **Tarea 8.2**: Mis Reservas page (GET /api/bookings)
  - Tiempo: 2h | Criteria: Listar bookings disponibles, estado y detalles segun el alcance actual
- **Tarea 8.3**: Editar perfil (PUT /api/users/{id})
  - Tiempo: 1.5h | Criteria: Form para cambiar nombre, email, etc
- **Tarea 8.4**: Logout & auth guard
  - Tiempo: 0.5h | Criteria: Logout limpia token, rutas protegidas redirigen login

**Dependencias**: Día 7 completado

---

#### **Día 9: UI Polish & Responsiveness** (6 horas)
- **Tarea 9.1**: Diseño responsive (mobile-first si no está hecho)
  - Tiempo: 2h | Criteria: Páginas funcionan en mobile (375px), tablet (768px), desktop
- **Tarea 9.2**: Componentes reutilizables (Button, Card, Modal, Input)
  - Tiempo: 2h | Criteria: 5+ componentes estandarizados, usados en todas las páginas
- **Tarea 9.3**: Estados loading/error en todas las llamadas API
  - Tiempo: 1.5h | Criteria: Spinners en fetches, mensajes de error amigables
- **Tarea 9.4**: Validación de formularios en frontend (email, edad, etc.)
  - Tiempo: 0.5h | Criteria: Feedback inmediato en inputs (rojo si inválido)

**Dependencias**: Día 8 completado (paralel con Día 8 es posible)

---

#### **Día 10: Integration Testing Frontend** (6 horas)
- **Tarea 10.1**: Tests unitarios componentes críticos (Auth, Booking form)
  - Tiempo: 2.5h | Criteria: 15+ unit tests, mocking API calls
- **Tarea 10.2**: Tests de integración frontend-backend (mocking requests a /api/*)
  - Tiempo: 2h | Criteria: Flujo completo: login → buscar hotel → booking → dashboard
- **Tarea 10.3**: Error handling tests (qué pasa si backend returna 500)
  - Tiempo: 1.5h | Criteria: App no crusha, user ve mensaje amigable

**Dependencias**: Día 9 completado

---

### **FASE 3: E2E & Final Polish (Días 11-12)**

#### **Día 11: End-to-End Testing** (7 horas)
- **Tarea 11.1**: Setup Cypress/Playwright para E2E
  - Tiempo: 2h | Criteria: Suite E2E configurada, corriendo contra backend local
- **Tarea 11.2**: Flujo completo E2E (signup → login → hotel search → booking → dashboard)
  - Tiempo: 3h | Criteria: Test automático cubre todo el user journey, pasa 100%
- **Tarea 11.3**: Performance testing (response times, bundle size)
  - Tiempo: 1h | Criteria: API responses < 500ms, Frontend bundle < 1MB
- **Tarea 11.4**: Documentar proceso de testing
  - Tiempo: 1h | Criteria: Guía de cómo correr E2E tests

**Dependencias**: FASE 2 completada

---

#### **Día 12: QA Final & Deployment Prep** (6 horas)
- **Tarea 12.1**: Bug fixes de QA (issues encontrados en Día 11)
  - Tiempo: 2h | Criteria: Bugs críticos cerrados, app es stable
- **Tarea 12.2**: Preparar deployment (env vars, secrets, CI/CD pipeline)
  - Tiempo: 2h | Criteria: GitHub Actions setup, Docker images buildeable
- **Tarea 12.3**: Security check (no hardcoded credentials, headers correctos)
  - Tiempo: 1h | Criteria: grep para no encontrar passwords, JWT headers presentes
- **Tarea 12.4**: Final sanity check & sign-off
  - Tiempo: 1h | Criteria: Todo funcionando, demo list preparado

**Dependencias**: Día 11 completado

---

## Resumen de Horas

| Fase | Días | Horas | Tareas |
|------|------|-------|--------|
| Backend Integration | 1-5 | 32h | 13 tareas |
| Frontend Implementation | 6-10 | 34h | 20 tareas |
| E2E & Polish | 11-12 | 13h | 9 tareas |
| **TOTAL** | **12** | **79h** | **42 tareas** |

**Velocidad esperada**: ~6.5h/día (asumir 1h almuerzo/breaks)

---

## Criterios de Aceptación Global (Definition of Done)

### Backend ✅
- [ ] Todos 30+ endpoints documentados en Swagger
- [ ] CORS configurado para Frontend domain
- [ ] 100% tests pasando (`mvn test`)
- [ ] Contract testing: respuestas match API_CONTRACT.md
- [ ] Docker compose levanta app + MySQL sin errores
- [ ] Postman collection exportada
- [ ] Zero hardcoded credentials

### Frontend ✅
- [ ] Flujo completo funciona: signup → login → search → booking → dashboard
- [ ] Responsivo en mobile (375px+)
- [ ] Loading states en todos los API calls
- [ ] Error handling: user ve mensajes amigables si API falla
- [ ] 15+ unit tests + 1 E2E flow
- [ ] Performance: API responses < 500ms, bundle < 1MB

### Integration ✅
- [ ] Backend y Frontend en environments separados (localhost:8080, localhost:3000)
- [ ] CORS headers correctos
- [ ] Auth flow funciona (signup → login → token en header)
- [ ] End-to-end booking completo
- [ ] Documentación API_INTEGRATION_GUIDE.md lista

---

## Riesgos & Mitigaciones

| Riesgo | Probabilidad | Impacto | Mitigación |
|--------|--------------|--------|-----------|
| **JWT/Auth no implementado en backend** | Media | Alto | Día 1 Tarea 1.2: revisar inmediatamente, Si no existe: 1-2 días adicionales |
| **Frontend vacío** | Baja | Alto | Día 6: validar estructura, si falta: añadir 2 días (total 14) |
| **CORS issues debugging** | Media | Medio | Día 1: comprehensive testing, Postman collection para verificar headers |
| **Performance bottleneck en queries** | Baja | Medio | Día 3: tests de integración revelan, optimize ORM queries si es necesario |
| **Incompatibilidad Node/React versions** | Baja | Medio | Día 6: usar `package-lock.json` fijo, documentar versions |
| **Member desaparece/sick** | Baja | Alto | Plan asume 1 dev full-time, si no: timeline se multiplica x1.5 |

---

## Archivos Críticos a Modificar

**Backend**:
- `src/main/java/com/parque/config/WebConfig.java` — CORS setup (Día 1)
- `src/main/java/com/parque/exception/GlobalExceptionHandler.java` — Error handling (Día 2)
- `src/test/java/...` — Integration tests (Día 3)
- `pom.xml` — Dependencias si falta JWT (Día 1)
- `Dockerfile` + `docker-compose.yml` — Containers (Día 4)

**Frontend** (paths dependen de estructura actual):
- `src/services/apiService.ts` — API client (Día 6)
- `src/pages/LoginPage.tsx` — Auth (Día 6)
- `src/pages/HotelSearchPage.tsx` — Core feature (Día 7)
- `src/components/` — Reutilizables (Día 9)

**Documentation**:
- `docs/API_INTEGRATION_GUIDE.md` — Nuevo (Día 5)
- `docs/DEPLOYMENT.md` — Nuevo (Día 12)

---

## Decisiones & Scope

- ✅ **INCLUIDO**: Backend pulido, frontend core features (auth, search, booking, dashboard), E2E basic
- ❌ **EXCLUIDO**: Carrito abandonment emails, Analytics avanzado, Multi-idioma, Admin panel completo
- ⚠️ **ASUMIDO**: 1 dev full-time, backend dev ya familiar con codebase, Figma/UI specs existen

---

## Próximos Pasos Ahora

1. ✅ **Valida el plan**: ¿Algún cambio? ¿Prioridades diferentes?
2. 🔍 **Revisar**: ¿JWT ya implementado en backend? (impacta Día 1)
3. 📁 **Snapshot**: ¿Frontend repo existe? ¿Qué está hecho?
4. 🚀 **Ejecutar**: Iniciamos Día 1 Tarea 1.1 si dan OK~
