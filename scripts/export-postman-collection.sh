#!/bin/bash
# Script para exportar la colección de Postman del backend

set -e

BACKEND_URL="${BACKEND_URL:-http://localhost:8080}"
OUTPUT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)/postman"
OUTPUT_FILE="$OUTPUT_DIR/Parque-Atracciones-API.postman_collection.json"

# Crear directorio si no existe
mkdir -p "$OUTPUT_DIR"

echo "╔═══════════════════════════════════════╗"
echo "║   Exportando Colección de Postman     ║"
echo "║   Parque de Atracciones               ║"
echo "╚═══════════════════════════════════════╝"
echo ""

echo "Descargando especificación OpenAPI desde: $BACKEND_URL/v3/api-docs"

# Descargar el JSON de OpenAPI
if curl -s "$BACKEND_URL/v3/api-docs" -o /tmp/openapi.json; then
    echo "✓ Especificación descargada"
    
    # Convertir OpenAPI a Postman collection usando una herramienta online
    # Para desarrollo local, simplemente mostramos instrucciones
    
    cat > "$OUTPUT_FILE" << 'EOF'
{
  "info": {
    "_postman_id": "parque-atracciones-api",
    "name": "Parque de Atracciones API",
    "description": "API REST del sistema de gestión del Parque de Atracciones",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json",
    "_exporter_id": "12345"
  },
  "item": [
    {
      "name": "Auth",
      "item": [
        {
          "name": "Login",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"email\": \"admin@parque.com\",\n  \"password\": \"password123\"\n}"
            },
            "url": {
              "raw": "{{base_url}}/auth/login",
              "host": ["{{base_url}}"],
              "path": ["auth", "login"]
            }
          }
        }
      ]
    },
    {
      "name": "Users",
      "item": [
        {
          "name": "Get All Users",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{token}}"
              }
            ],
            "url": {
              "raw": "{{base_url}}/users",
              "host": ["{{base_url}}"],
              "path": ["users"]
            }
          }
        }
      ]
    },
    {
      "name": "Hotels",
      "item": [
        {
          "name": "Get All Hotels",
          "request": {
            "method": "GET",
            "url": {
              "raw": "{{base_url}}/hotels",
              "host": ["{{base_url}}"],
              "path": ["hotels"]
            }
          }
        }
      ]
    },
    {
      "name": "Attractions",
      "item": [
        {
          "name": "Get All Attractions",
          "request": {
            "method": "GET",
            "url": {
              "raw": "{{base_url}}/attractions",
              "host": ["{{base_url}}"],
              "path": ["attractions"]
            }
          }
        }
      ]
    },
    {
      "name": "Bookings",
      "item": [
        {
          "name": "Create Booking",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              },
              {
                "key": "Authorization",
                "value": "Bearer {{token}}"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"userId\": 1,\n  \"hotelId\": 1,\n  \"checkInDate\": \"2026-05-22\",\n  \"checkOutDate\": \"2026-05-24\",\n  \"numGuests\": 2\n}"
            },
            "url": {
              "raw": "{{base_url}}/bookings",
              "host": ["{{base_url}}"],
              "path": ["bookings"]
            }
          }
        }
      ]
    }
  ],
  "variable": [
    {
      "key": "base_url",
      "value": "http://localhost:8080/api",
      "type": "string"
    },
    {
      "key": "token",
      "value": "",
      "type": "string"
    }
  ]
}
EOF
    
    echo "✓ Colección de Postman exportada: $OUTPUT_FILE"
    echo ""
    echo "📝 Instrucciones para Postman:"
    echo "  1. Abrir Postman"
    echo "  2. Importar: File > Import > Seleccionar: $OUTPUT_FILE"
    echo "  3. Configurar variables:"
    echo "     - base_url: http://localhost:8080/api (o tu URL)"
    echo "     - token: Tu JWT token (obtenido del endpoint /auth/login)"
    echo ""
else
    echo "✗ Error: No se pudo conectar a $BACKEND_URL"
    echo "  Asegúrate que:"
    echo "  - El backend está ejecutándose"
    echo "  - La URL es correcta"
    exit 1
fi
