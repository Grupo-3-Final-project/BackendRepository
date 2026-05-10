#!/bin/bash
# Script para cargar datos de prueba en la base de datos del Parque de Atracciones

set -e

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuración por defecto
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-3306}"
DB_NAME="${DB_NAME:-parque_atracciones}"
DB_USER="${DB_USER:-parque_user}"
DB_PASSWORD="${DB_PASSWORD:-parque_password}"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SEED_FILE="${SCRIPT_DIR}/seed-data.sql"

echo -e "${YELLOW}╔════════════════════════════════════════╗${NC}"
echo -e "${YELLOW}║   Cargando datos de prueba (SEED)      ║${NC}"
echo -e "${YELLOW}║   Parque de Atracciones - Backend      ║${NC}"
echo -e "${YELLOW}╚════════════════════════════════════════╝${NC}"
echo ""

# Validar que el archivo de seed existe
if [ ! -f "$SEED_FILE" ]; then
    echo -e "${RED}✗ Error: Archivo $SEED_FILE no encontrado${NC}"
    exit 1
fi

echo -e "${YELLOW}Configuración:${NC}"
echo "  Host: $DB_HOST"
echo "  Puerto: $DB_PORT"
echo "  Base de Datos: $DB_NAME"
echo "  Usuario: $DB_USER"
echo ""

# Ejecutar el script SQL
echo -e "${YELLOW}Ejecutando script SQL...${NC}"
if mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" < "$SEED_FILE" 2>/dev/null; then
    echo -e "${GREEN}✓ Datos de prueba cargados exitosamente${NC}"
    echo ""
    echo -e "${GREEN}Datos cargados:${NC}"
    echo "  • 10 usuarios (admin, manager, employee, 7 clientes)"
    echo "  • 5 hoteles"
    echo "  • 10 atracciones"
    echo "  • 7 reservas"
    echo "  • 5 ofertas especiales"
    echo "  • 5 empleados"
    echo "  • 6 turnos"
    echo "  • 5 registros de mantenimiento"
    echo ""
    echo -e "${YELLOW}Credentials para pruebas:${NC}"
    echo "  Admin:"
    echo "    Email: admin@parque.com"
    echo "    DNI: 00000000A"
    echo "    Contraseña: password123"
    echo ""
    echo "  Employee:"
    echo "    Email: employee@parque.com"
    echo "    DNI: 22222222C"
    echo "    Contraseña: password123"
    echo ""
    echo "  Usuario Regular:"
    echo "    Email: juan.garcia@example.com"
    echo "    DNI: 33333333D"
    echo "    Contraseña: password123"
    echo ""
else
    echo -e "${RED}✗ Error al cargar datos de prueba${NC}"
    echo -e "${RED}Verifica que:${NC}"
    echo "  • MySQL está ejecutándose en $DB_HOST:$DB_PORT"
    echo "  • Las credenciales son correctas"
    echo "  • La base de datos $DB_NAME existe"
    exit 1
fi
