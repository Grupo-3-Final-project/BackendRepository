# AGENTS.md - Proyecto Final Parque de Atracciones

## Instruccion obligatoria para agentes

Antes de hacer cualquier cambio en este proyecto, cualquier agente de IA o asistente de programacion debe leer este documento completo.

Ademas, antes de modificar cualquier elemento relacionado con la comunicacion entre frontend y backend, tambien debe leer estos documentos:

- `docs/API_CONTRACT.md`
- `docs/CONTRACT_TESTING.md`

Esto aplica especialmente a endpoints, metodos HTTP, DTOs, requests, responses, nombres de campos JSON, codigos de error, servicios API del frontend, tests de contrato y Swagger/OpenAPI.

---

## 1. Contexto general del proyecto

Este proyecto corresponde al proyecto final de JM Factoria.

La aplicacion sera utilizada en ordenadores de taquilla y administracion de un parque de atracciones.

El sistema tendra dos repositorios separados:

- Backend: Spring Boot.
- Frontend: React con Vite.

El objetivo principal es construir una aplicacion completa con CRUD, gestion de reservas, hoteles, atracciones, empleados, dashboard de direccion, Cloudinary, validaciones, excepciones, DTOs y testing.

Este archivo define las normas generales que debe seguir cualquier agente de IA o asistente de programacion que trabaje en el proyecto.

---

## 2. Idioma y naming

El codigo debe escribirse usando naming en ingles.

Deben ir en ingles:

- Clases.
- Interfaces.
- Variables.
- Metodos.
- Funciones.
- Componentes.
- Hooks.
- Servicios.
- Carpetas tecnicas.
- Endpoints.
- Commits.

Deben ir en castellano:

- README.
- Documentacion del proyecto.
- Textos visibles en la interfaz.
- Mensajes visibles para el usuario final.
- Explicaciones funcionales del proyecto.

Reglas de naming:

- Variables, metodos y funciones: `camelCase`.
- Clases Java: `PascalCase`.
- Componentes React: `PascalCase`.
- Hooks React: deben empezar por `use`.
- Endpoints: en ingles, en plural y en minusculas.
- Ramas Git: en ingles y usando `/`.

Ejemplos correctos:

```text
User
Hotel
Attraction
Employee
Booking
DashboardPage
HotelCard
useHotels
/api/users
/api/hotels
/api/attractions
```

Ejemplos incorrectos:

```text
Usuario
hotel_card
getUsuarios
/api/getUsers
/api/listaHoteles
```

## 3. Comentarios en el codigo

No se deben anadir comentarios en el codigo.

La claridad debe conseguirse mediante:

- Buen naming.
- Metodos pequenos.
- Responsabilidad unica.
- Codigo simple.
- Estructura clara.
- Separacion correcta de responsabilidades.

No se deben anadir comentarios explicativos, comentarios obvios, comentarios de bloque ni comentarios temporales.

## 4. Calidad de codigo

El codigo debe seguir principios de Clean Code.

Se deben aplicar principios SOLID siempre que sea razonable para el alcance del proyecto.

Reglas generales:

- Evitar duplicacion de codigo.
- Evitar funciones o metodos demasiado largos.
- Evitar clases o componentes con demasiadas responsabilidades.
- Evitar codigo muerto.
- Evitar logica repetida.
- Evitar nombres ambiguos.
- Evitar soluciones dificiles de mantener.
- No introducir arquitectura nueva sin necesidad clara.
- No hacer refactorizaciones grandes sin que la tarea lo pida expresamente.

Cada clase, funcion, metodo o componente debe tener una responsabilidad clara.

## 5. Git, ramas y flujo de trabajo

No se debe trabajar directamente sobre `main`.

La rama `main` queda reservada para versiones estables y entregables.

La rama `dev` sera la rama de integracion del equipo.

Cada tarea debera desarrollarse en una rama especifica.

Estructura de ramas:

```text
main
dev
feature/*
fix/*
test/*
docs/*
```

Uso de ramas:

- `feature/*`: nuevas funcionalidades.
- `fix/*`: correcciones.
- `test/*`: trabajo relacionado con testing.
- `docs/*`: documentacion.
- `dev`: integracion del trabajo del equipo.
- `main`: version estable.

Las ramas de trabajo deben integrarse primero en `dev`.

Antes de integrar cambios en `dev`, se debe comprobar que:

- El proyecto compila correctamente.
- Los tests relevantes pasan.
- No se han modificado archivos no relacionados con la tarea.
- Si se ha cambiado un endpoint, request, response o campo JSON, se ha comunicado al equipo.
- Si se ha aprobado un cambio de contrato, se ha actualizado la documentacion correspondiente.
- Si cambia el comportamiento de una funcionalidad, se actualizan los tests necesarios.

## 6. Commits

Se deben usar conventional commits.

Los commits deben ser pequenos, claros y relacionados con una unica intencion.

No se deben mezclar cambios de backend, frontend, documentacion y tests en un mismo commit si pertenecen a tareas distintas.

Tipos permitidos:

```text
feat
fix
test
docs
refactor
style
chore
build
ci
```

Ejemplos:

```text
feat: add hotel CRUD
fix: prevent booking when hotel is full
test: add booking business rule tests
docs: update API contract
refactor: simplify booking service
style: improve dashboard layout
chore: configure project setup
```

## 7. Forma de trabajo

El trabajo debe realizarse paso a paso.

Cada cambio debe validarse antes de continuar con el siguiente.

Se deben evitar cambios grandes sin probar.

Antes de dar una tarea por terminada, se debe revisar:

- Que archivos han cambiado.
- Si los cambios pertenecen realmente a la tarea.
- Si el proyecto compila.
- Si los tests relevantes pasan.
- Si se ha respetado la estructura acordada.
- Si se ha respetado el contrato API cuando la tarea afecte a backend/frontend.

No se deben modificar archivos no relacionados con la tarea.

No se debe borrar codigo existente salvo que sea necesario para completar la tarea y este justificado por el propio cambio.

No se deben cambiar nombres de carpetas, entidades, DTOs, componentes, endpoints o servicios si la tarea no lo requiere.

## 8. Contrato API y contract testing

El proyecto tendra documentacion especifica de contrato API y contract testing dentro de la carpeta `docs/`.

Documentos esperados:

- `docs/API_CONTRACT.md`
- `docs/CONTRACT_TESTING.md`

Antes de modificar cualquier elemento relacionado con la comunicacion entre frontend y backend, se deben revisar esos documentos.

Esto aplica a:

- Endpoints.
- Metodos HTTP.
- DTOs.
- Requests.
- Responses.
- Nombres de campos JSON.
- Codigos de error.
- Servicios API del frontend.
- Tests de contrato.
- Swagger/OpenAPI.

No se deben renombrar rutas, campos JSON, estructuras de request o estructuras de response sin comunicarlo previamente al equipo.

Si el equipo aprueba un cambio de contrato, se deberan actualizar:

- `docs/API_CONTRACT.md`.
- `docs/CONTRACT_TESTING.md`, si aplica.
- Swagger/OpenAPI.
- Tests afectados.
- Servicios API del frontend.
- Codigo backend relacionado.

Los nombres de campos usados en frontend deben coincidir exactamente con los nombres definidos en el contrato API.

## 9. Backend - Stack tecnico

El backend se desarrollara con:

- Java.
- Spring Boot.
- Maven.
- MySQL.
- DTOs.
- Validaciones.
- Manejo global de excepciones.
- Cloudinary.
- Tests de backend.
- Swagger/OpenAPI.

Comandos principales:

```bash
./mvnw clean test
./mvnw spring-boot:run
```

En Windows tambien puede usarse:

```bash
./mvnw.cmd clean test
./mvnw.cmd spring-boot:run
```

## 10. Backend - Estructura por dominio

El backend debe organizarse por dominio.

La estructura recomendada es:

```text
src/main/java/com/parque
|-- user
|   |-- controller
|   |-- dto
|   |-- model
|   |-- repository
|   `-- service
|-- hotel
|   |-- controller
|   |-- dto
|   |-- model
|   |-- repository
|   `-- service
|-- attraction
|   |-- controller
|   |-- dto
|   |-- model
|   |-- repository
|   `-- service
|-- employee
|   |-- controller
|   |-- dto
|   |-- model
|   |-- repository
|   `-- service
|-- booking
|   |-- controller
|   |-- dto
|   |-- model
|   |-- repository
|   `-- service
|-- offer
|   |-- controller
|   |-- dto
|   |-- model
|   |-- repository
|   `-- service
|-- dashboard
|   |-- controller
|   |-- dto
|   `-- service
|-- maintenance
|   |-- controller
|   |-- dto
|   |-- model
|   |-- repository
|   `-- service
|-- shift
|   |-- controller
|   |-- dto
|   |-- model
|   |-- repository
|   `-- service
|-- config
|-- exception
`-- cloudinary
```

Si el package base definitivo cambia, se debe mantener la misma estructura interna por dominio.

## 11. Backend - DTOs, validaciones y excepciones

El backend debe usar DTOs para requests y responses.

No se deben exponer entidades directamente si eso puede generar:

- Acoplamiento innecesario.
- Bucles de serializacion.
- Respuestas demasiado grandes.
- Dependencia directa entre frontend y modelo interno de base de datos.

Las validaciones deben aplicarse en DTOs de entrada cuando corresponda.

Las reglas de negocio deben validarse en servicios o clases especificas de dominio.

El backend debe usar manejo global de excepciones.

No se deben devolver trazas internas del servidor al frontend.

Los errores deben devolverse de forma controlada y consistente.

Casos esperados:

- `400 Bad Request`: errores de validacion.
- `404 Not Found`: recurso no encontrado.
- `409 Conflict`: conflicto con reglas de negocio.
- `500 Internal Server Error`: error inesperado.

## 12. Backend - Reglas de negocio principales

Las reglas de negocio criticas deben estar cubiertas por tests.

Reglas principales:

- Un cliente puede comprar varias entradas para varias personas.
- Cada acompanante debe tener nombre, apellidos y fecha de nacimiento.
- La tarifa debe calcularse segun el rango de edad.
- Un menor no puede viajar si no esta acompanado por un adulto.
- No se puede vender un hotel si esta completo.
- Al crear una reserva con hotel, debe actualizarse la disponibilidad.
- Deben existir empleados limpiadores, animadores y tecnicos.
- Siempre debe haber al menos 3 empleados de cada oficio repartidos en 2 turnos.
- Los turnos deben rotar cada 15 dias.
- La agenda de mantenimiento debe generarse automaticamente.
- Las atracciones grandes deben revisarse con mas frecuencia.
- El mantenimiento debe asociar tecnicos automaticamente.
- El dashboard debe calcular metricas reales a partir de reservas, entradas y hoteles.

## 13. Backend - Dashboard y metricas funcionales

Las metricas del dashboard deben calcularse en backend.

El frontend solo debe mostrar los datos devueltos por la API.

Las metricas minimas del dashboard son:

- Entradas vendidas por rango de edad y ano.
- Recaudacion total del ano en curso.
- Top 3 hoteles que mas recaudan en el ano en curso.

No se deben duplicar calculos de negocio en React.

## 14. Backend - Swagger/OpenAPI

El backend debe exponer documentacion Swagger/OpenAPI.

Swagger debe reflejar el contrato API acordado por el equipo.

Si se modifica un endpoint, request, response o DTO relacionado con la API, debe comprobarse que Swagger sigue representando correctamente el comportamiento esperado.

## 15. Backend - Cloudinary

Las imagenes deben gestionarse mediante Cloudinary.

No se deben guardar imagenes binarias en la base de datos.

La base de datos debe guardar la URL de la imagen y, si es necesario, el `publicId`.

Cloudinary se usara para imagenes de:

- Hoteles.
- Atracciones.
- Empleados.
- Ofertas, si aplica.

No se deben dejar URLs hardcodeadas como solucion final.

## 16. Frontend - Stack tecnico

El frontend se desarrollara con:

- React.
- Vite.
- JavaScript.
- JSX.
- npm.
- Axios.
- Vitest.
- React Testing Library.
- Playwright para tests E2E.

Comandos principales:

```bash
npm install
npm run dev
npm run build
npm run test
```

Si se configura Playwright:

```bash
npx playwright test
```

## 17. Frontend - Estructura

La estructura base del frontend sera:

```text
src/
|-- api/
|-- assets/
|-- components/
|-- features/
|   |-- users/
|   |-- hotels/
|   |-- attractions/
|   |-- employees/
|   |-- bookings/
|   `-- dashboard/
|-- hooks/
|-- layouts/
|-- pages/
|-- router/
`-- styles/
```

Se debe respetar esta estructura.

No se deben crear carpetas alternativas para resolver problemas que ya tienen una ubicacion definida.

## 18. Frontend - Comunicacion con API

Todas las llamadas al backend deben centralizarse en `/src/api/`.

El frontend usara Axios como cliente HTTP para comunicarse con el backend.

La configuracion base de Axios debe centralizarse en:

```text
src/api/apiClient.js
```

Ese archivo debe configurar:

- `baseURL` usando `VITE_API_BASE_URL`.
- Headers comunes si son necesarios.
- Manejo basico de errores.
- Exportacion de una instancia reutilizable de Axios.

No se deben crear instancias de Axios duplicadas en distintos archivos.

Los servicios API de cada dominio deben reutilizar `apiClient`.

Estructura recomendada:

```text
src/api/apiClient.js
src/api/userApi.js
src/api/hotelApi.js
src/api/attractionApi.js
src/api/employeeApi.js
src/api/bookingApi.js
src/api/dashboardApi.js
```

No se deben hacer llamadas directas a la API dentro de componentes JSX.

Los componentes no deben contener logica HTTP directamente.

Los servicios de API deben encargarse de:

- Construir la URL correcta.
- Enviar requests al backend.
- Recibir responses.
- Propagar o normalizar errores.

No se debe llamar a la API directamente dentro del JSX.

No se debe duplicar logica de comunicacion con backend.

Los nombres de campos recibidos o enviados deben coincidir exactamente con el contrato API.

## 19. Frontend - Componentes

Los componentes deben ser pequenos, reutilizables y con una unica responsabilidad.

Los componentes deben ubicarse por dominio cuando sea posible.

Ejemplos:

```text
components/users/
components/hotels/
components/attractions/
components/employees/
components/bookings/
components/dashboard/
```

Reglas:

- Evitar componentes demasiado grandes.
- Evitar logica duplicada.
- Evitar llamadas API dentro de componentes visuales.
- Evitar estilos inline.
- Evitar romper la estructura de carpetas acordada.
- Extraer formularios, cards, tablas, botones y badges reutilizables cuando tenga sentido.

## 20. Frontend - Estado

Se usara estado local de React cuando sea suficiente.

Se pueden usar hooks propios para reutilizar logica.

Se evitara estado global salvo que sea realmente necesario.

No se debe introducir una solucion compleja de estado global para resolver problemas simples.

## 21. Frontend - Manejo de errores

El frontend debe manejar correctamente los errores devueltos por backend.

Casos minimos:

- `400 Bad Request`: mostrar errores de validacion.
- `404 Not Found`: mostrar mensaje de recurso no encontrado.
- `409 Conflict`: mostrar conflicto de negocio.
- `500 Internal Server Error`: mostrar error generico controlado.

Los mensajes visibles para el usuario deben estar en castellano.

No se deben mostrar trazas tecnicas, nombres de excepciones internas ni errores crudos del servidor al usuario final.

## 22. Frontend - Estilo visual

La interfaz debe ser consistente en todas las vistas.

Se deben reutilizar componentes y estilos para:

- Botones.
- Cards.
- Formularios.
- Badges.
- Tablas.
- Mensajes de error.
- Estados de carga.
- Modales, si se usan.

Se debe mantener coherencia en:

- Colores.
- Espaciados.
- Tipografia.
- Bordes.
- Sombras.
- Tamanos de botones.
- Tamanos de formularios.

No se deben usar estilos inline.

Los estilos deben estar organizados en archivos CSS o en la solucion de estilos acordada por el equipo.

## 23. Frontend - Imagenes

Las imagenes deben integrarse respetando el flujo acordado con Cloudinary.

No se deben dejar URLs hardcodeadas como solucion final.

Las imagenes de hoteles, atracciones, empleados u ofertas deberan usar los datos devueltos por backend.

## 24. Testing general

Cualquier cambio en reglas de negocio debe incluir o actualizar los tests correspondientes.

Cualquier correccion de bug debe incluir, cuando sea posible, un test que evite que el error vuelva a repetirse.

No se debe considerar una tarea terminada hasta que los tests relevantes se hayan ejecutado correctamente.

El testing debe cubrir, como minimo:

- Reglas de negocio.
- Servicios.
- Validaciones.
- Excepciones.
- Endpoints principales.
- Comunicacion frontend-backend.
- Componentes principales.
- Manejo de errores.
- Flujos criticos de usuario.
- Dashboard.

## 25. Backend - Testing

El backend debe incluir tests para:

- Servicios.
- Reglas de negocio.
- Validadores.
- Controladores.
- Repositorios cuando sea necesario.
- Flujos de reserva.
- Disponibilidad de hoteles.
- Calculo de tarifas.
- Turnos de empleados.
- Agenda de mantenimiento.
- Metricas del dashboard.

Tipos de test esperados:

- Tests unitarios.
- Tests de integracion.
- Tests de contrato cuando afecte a API.

Se debe aplicar TDD cuando se implementen reglas de negocio criticas.

Ciclo esperado de TDD:

1. Crear test que falla.
2. Implementar el minimo codigo necesario.
3. Ejecutar test.
4. Refactorizar si procede.
5. Confirmar que los tests siguen pasando.

## 26. Frontend - Testing

El frontend debe incluir tests basicos para:

- Renderizado de componentes principales.
- Formularios CRUD.
- Servicios API.
- Manejo de errores.
- Componentes del dashboard.
- Flujo principal de reserva.

Herramientas:

- Vitest.
- React Testing Library.
- Playwright para E2E.

Tests E2E minimos recomendados:

- Cargar la home.
- Ver hoteles disponibles.
- Crear una reserva.
- Ver dashboard.
- Validar error de negocio cuando el backend devuelve un conflicto.

## 27. Seguridad y variables de entorno

No se deben hardcodear secretos, contrasenas, tokens o claves privadas.

No se debe subir el archivo `.env` al repositorio.

Debe existir un archivo `.env.example` cuando el proyecto necesite variables de entorno.

Backend - variables esperadas:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
CLOUDINARY_CLOUD_NAME
CLOUDINARY_API_KEY
CLOUDINARY_API_SECRET
MAIL_USERNAME
MAIL_PASSWORD
```

Frontend - variable esperada:

```text
VITE_API_BASE_URL
```

## 28. Documentacion

La documentacion del proyecto debe estar en castellano.

El README debe explicar como minimo:

- Descripcion del proyecto.
- Stack tecnico.
- Estructura del proyecto.
- Instalacion.
- Variables de entorno.
- Comandos principales.
- Testing.
- Endpoints o referencia a Swagger/API contract.
- Autores o equipo, si aplica.

La documentacion tecnica relacionada con API y contract testing debe mantenerse dentro de `/docs`.

## 29. Jira

El trabajo debe estar relacionado con tareas de Jira siempre que sea posible.

Cada tarea debe tener un alcance claro.

No se deben mezclar varias tareas grandes en una sola rama.

Cuando una tarea cambie durante el desarrollo, debe actualizarse o comunicarse en Jira o al equipo.

## 30. Reglas especificas para agentes de IA

El agente debe trabajar de forma conservadora.

No debe hacer refactorizaciones masivas sin peticion expresa.

No debe modificar archivos no relacionados.

No debe cambiar estructura de carpetas sin motivo.

No debe cambiar endpoints, DTOs, requests o responses sin revisar antes los documentos de contrato y sin dejar constancia de que el cambio afecta al contrato.

No debe introducir comentarios en el codigo.

No debe duplicar logica.

No debe hacer llamadas API directamente dentro del JSX.

No debe usar estilos inline.

No debe dejar URLs hardcodeadas como solucion final.

No debe dar una tarea por terminada sin indicar:

- Que se ha cambiado.
- Que archivos se han tocado.
- Que tests se han ejecutado.
- Si queda algo pendiente.
