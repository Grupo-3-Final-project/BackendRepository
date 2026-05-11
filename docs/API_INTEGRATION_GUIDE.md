# API Integration Guide - Parque de Atracciones

Guía completa para desarrolladores de frontend que necesitan integrar la API del backend.

## Tabla de Contenidos

1. [Configuración Inicial](#configuración-inicial)
2. [Autenticación](#autenticación)
3. [Gestión de Errores](#gestión-de-errores)
4. [Endpoints Disponibles](#endpoints-disponibles)
5. [Ejemplos de Uso](#ejemplos-de-uso)
6. [Variables de Entorno](#variables-de-entorno)

---

## Configuración Inicial

### Base URL

```
Desarrollo: http://localhost:8080/api
Producción: https://parque-atracciones.com/api
```

### Setup en Frontend (React + Axios)

```javascript
// src/services/apiClient.ts
import axios from 'axios';

const apiClient = axios.create({
  baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor para agregar JWT al header
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Interceptor para manejar errores
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expirado o inválido
      localStorage.removeItem('accessToken');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default apiClient;
```

---

## Autenticación

### 1. Login (Usuarios Internos)

**Endpoint**: `POST /auth/login`

Usado para empleados, managers y administradores.

```javascript
// Request
{
  "email": "employee@parque.com",
  "password": "password123"
}

// Response (200 OK)
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600,
  "user": {
    "id": 1,
    "email": "employee@parque.com",
    "firstName": "Employee",
    "role": "EMPLOYEE"
  }
}
```

**Errores**:
- `401 Unauthorized`: Credenciales inválidas
- `400 Bad Request`: Campos requeridos faltantes

### 2. Registrar Usuario

**Endpoint**: `POST /users`

Registro de nuevos clientes (sin autenticación requerida).

```javascript
// Request
{
  "firstName": "Juan",
  "lastName": "García",
  "dni": "12345678A",
  "email": "juan@example.com",
  "phone": "600123456",
  "birthDate": "1990-05-15"
}

// Response (201 Created)
{
  "id": 1,
  "firstName": "Juan",
  "lastName": "García",
  "dni": "12345678A",
  "email": "juan@example.com",
  "phone": "600123456",
  "birthDate": "1990-05-15"
}
```

**Errores**:
- `400 Bad Request`: Validación fallida (email invalido, DNI duplicado, etc)
- `409 Conflict`: Email o DNI ya existe

### 3. Refresh Token

**Endpoint**: `POST /auth/refresh`

Renovar el token de acceso cuando está próximo a expirar.

```javascript
// Request
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}

// Response (200 OK)
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600
}
```

---

## Gestión de Errores

### Formato Estándar de Error

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Descripción legible del error",
  "path": "/api/bookings",
  "timestamp": "2026-05-22T10:30:00"
}
```

### Códigos HTTP

| Código | Situación | Acción |
|--------|-----------|--------|
| 200 | Éxito | Procesar respuesta normalmente |
| 201 | Recurso creado | Mostrar confirmación |
| 204 | Eliminación exitosa | No tiene body |
| 400 | Error de validación | Mostrar mensaje de error al usuario |
| 401 | Autenticación requerida | Redirigir a login |
| 404 | Recurso no encontrado | Mostrar página 404 |
| 409 | Conflicto de negocio | Mostrar mensaje descriptivo |
| 500 | Error interno | Mostrar mensaje genérico |

### Manejo en Frontend

```javascript
async function handleApiRequest(apiCall) {
  try {
    const response = await apiCall();
    return response.data;
  } catch (error) {
    const status = error.response?.status;
    const message = error.response?.data?.message || 'Error desconocido';
    
    if (status === 401) {
      // Redirigir a login
      window.location.href = '/login';
    } else if (status === 404) {
      // Mostrar página 404
      throw new Error('Recurso no encontrado');
    } else if (status === 409) {
      // Conflicto de negocio (ej: hotel lleno)
      throw new Error(message);
    } else {
      // Error genérico
      throw new Error(message || 'Error en la solicitud');
    }
  }
}
```

---

## Endpoints Disponibles

### Usuarios (`/users`)

```
GET    /users              - Listar todos (requiere ADMIN)
GET    /users/{id}         - Obtener por ID (requiere auth)
POST   /users              - Crear usuario (público)
PUT    /users/{id}         - Actualizar (requiere auth)
DELETE /users/{id}         - Eliminar (requiere ADMIN)
```

### Hoteles (`/hotels`)

```
GET    /hotels             - Listar todos (público)
GET    /hotels/{id}        - Obtener detalles (público)
POST   /hotels             - Crear (requiere ADMIN)
PUT    /hotels/{id}        - Actualizar (requiere ADMIN)
DELETE /hotels/{id}        - Eliminar (requiere ADMIN)
```

### Atracciones (`/attractions`)

```
GET    /attractions        - Listar todas (público)
GET    /attractions/{id}   - Obtener detalles (público)
POST   /attractions        - Crear (requiere ADMIN)
PUT    /attractions/{id}   - Actualizar (requiere ADMIN)
DELETE /attractions/{id}   - Eliminar (requiere ADMIN)
PATCH  /attractions/{id}/status - Cambiar estado (requiere EMPLOYEE+)
```

### Reservas (`/bookings`)

```
POST   /bookings           - Crear reserva (requiere USER+)
GET    /bookings           - Listar reservas del usuario (requiere USER+)
GET    /bookings/{id}      - Obtener detalles (requiere auth)
POST   /bookings/{id}/confirm - Confirmar (requiere EMPLOYEE+)
POST   /bookings/{id}/cancel  - Cancelar (requiere EMPLOYEE+)
```

### Dashboard (`/dashboard`) - Solo Admin

```
GET    /dashboard/summary?year=2026
GET    /dashboard/tickets-by-age-range?year=2026
GET    /dashboard/current-year-revenue
GET    /dashboard/top-hotels?year=2026
```

### Ofertas (`/offers`)

```
GET    /offers             - Listar todas (público)
GET    /offers/{id}        - Obtener detalles (público)
POST   /offers             - Crear (requiere ADMIN)
PUT    /offers/{id}        - Actualizar (requiere ADMIN)
DELETE /offers/{id}        - Eliminar (requiere ADMIN)
```

### Empleados (`/employees`) - Solo ADMIN

```
GET    /employees          - Listar todos
GET    /employees/{id}     - Obtener detalles
POST   /employees          - Crear
PUT    /employees/{id}     - Actualizar
DELETE /employees/{id}     - Eliminar
```

### Turnos (`/shifts`) - Solo ADMIN

```
GET    /shifts             - Listar todos
POST   /shifts/generate    - Generar turnos automáticos
```

### Mantenimiento (`/maintenance`) - Solo ADMIN

```
GET    /maintenance        - Listar registros
POST   /maintenance/generate - Generar automático
```

### Imágenes (`/images`)

```
POST   /images/upload      - Subir imagen a Cloudinary
```

---

## Ejemplos de Uso

### Ejemplo 1: Autenticación Completa

```javascript
// src/hooks/useAuth.ts
import { useState } from 'react';
import apiClient from '../services/apiClient';

export const useAuth = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const login = async (email: string, password: string) => {
    try {
      setLoading(true);
      setError(null);
      const { data } = await apiClient.post('/auth/login', { email, password });
      
      localStorage.setItem('accessToken', data.accessToken);
      localStorage.setItem('refreshToken', data.refreshToken);
      localStorage.setItem('user', JSON.stringify(data.user));
      
      return data.user;
    } catch (err: any) {
      const message = err.response?.data?.message || 'Error al iniciar sesión';
      setError(message);
      throw new Error(message);
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
  };

  return { login, logout, loading, error };
};
```

### Ejemplo 2: Buscar Hoteles

```javascript
// src/pages/HotelSearchPage.tsx
import { useEffect, useState } from 'react';
import apiClient from '../services/apiClient';

export const HotelSearchPage = () => {
  const [hotels, setHotels] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchHotels = async () => {
      try {
        setLoading(true);
        const { data } = await apiClient.get('/hotels');
        setHotels(data);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Error al cargar hoteles');
      } finally {
        setLoading(false);
      }
    };

    fetchHotels();
  }, []);

  if (loading) return <div>Cargando hoteles...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div>
      {hotels.map((hotel) => (
        <HotelCard key={hotel.id} hotel={hotel} />
      ))}
    </div>
  );
};
```

### Ejemplo 3: Crear una Reserva

```javascript
// src/services/bookingService.ts
import apiClient from './apiClient';

export const createBooking = async (bookingData) => {
  const { data } = await apiClient.post('/bookings', {
    userId: bookingData.userId,
    hotelId: bookingData.hotelId,
    checkInDate: bookingData.checkInDate, // "2026-05-22"
    checkOutDate: bookingData.checkOutDate, // "2026-05-24"
    numGuests: bookingData.numGuests,
    notes: bookingData.notes || '',
  });
  return data;
};

export const getUserBookings = async (userId: number) => {
  const { data } = await apiClient.get(`/bookings?userId=${userId}`);
  return data;
};
```

### Ejemplo 4: Atracciones Disponibles

```javascript
// src/pages/AttractionsPage.tsx
export const AttractionsPage = () => {
  const [attractions, setAttractions] = useState([]);

  useEffect(() => {
    apiClient.get('/attractions')
      .then(res => setAttractions(res.data))
      .catch(err => console.error('Error:', err.message));
  }, []);

  return (
    <div>
      {attractions.map(attr => (
        <AttractionCard key={attr.id} attraction={attr} />
      ))}
    </div>
  );
};
```

---

## Variables de Entorno

### .env.local (Development)

```bash
REACT_APP_API_URL=http://localhost:8080/api
REACT_APP_ENV=development
```

### .env.production

```bash
REACT_APP_API_URL=https://parque-atracciones.com/api
REACT_APP_ENV=production
```

---

## Notas Importantes

### Formatos de Fecha

- Siempre usar ISO 8601: `YYYY-MM-DD`
- Ejemplo: `"2026-05-22"`

### Moneda

- Siempre usar números decimales
- Ejemplo: `149.99` (no `"149.99"`)

### Roles de Usuario

```
ADMIN     - Acceso total
MANAGER   - Gestión general
EMPLOYEE  - Operaciones básicas
USER      - Comprador/Cliente
```

### Límites de Rate

- Sin límite por defecto (configurable en producción)
- Se recomienda implementar caché en frontend

### CORS

El backend permite orígenes:
- `localhost:3000`, `localhost:3001`, `localhost:4173`, `localhost:5173`
- `parque-atracciones.com` (producción)

---

## Soporte

Para preguntas sobre la API, revisar:
- [API_CONTRACT.md](./API_CONTRACT.md) - Especificación técnica
- [CONTRACT_TESTING.md](./CONTRACT_TESTING.md) - Tests del contrato
- Swagger: `/swagger-ui.html`

---

**Última actualización**: 2026-05-08
**Versión API**: v1
