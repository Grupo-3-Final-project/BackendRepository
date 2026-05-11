# 🔀 WORKFLOW - Sprint 1 Phase 2
## Desarrollo en Paralelo: Dos Programadores, Dos Ramas

**Fecha:** 4 de mayo de 2026  
**Estado:** ✅ Ramas creadas y listas

---

## 📍 ESTRUCTURA DE RAMAS

```
backend/phase2 (base)
├── feature/p1-cors-security      ← Programador 1 (CORS & Security)
└── feature/p3-integration-testing ← Programador 3 (Testing)
```

---

## 👨‍💻 PROGRAMADOR 1: CORS & SECURITY

### Rama Asignada
```bash
feature/p1-cors-security
```

### ✅ Trabajo Completado
- [x] WebConfig con CORS para localhost:3000 + 5173
- [x] SecurityConfig con Spring Security
- [x] JwtTokenProvider para generación/validación
- [x] JwtAuthenticationFilter para procesar requests
- [x] CustomUserDetailsService para autenticación

### 📋 Próximos Pasos (Tareas 1.3 - 1.4)

#### Tarea 1.3: Test CORS (1h)
```bash
# Validar CORS headers
curl -H "Origin: http://localhost:3000" \
     -H "Access-Control-Request-Method: POST" \
     -H "Access-Control-Request-Headers: Content-Type" \
     -X OPTIONS http://localhost:8080/api/users -v
```

**Esperado:**
```
Access-Control-Allow-Origin: http://localhost:3000
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, PATCH, OPTIONS
Access-Control-Allow-Headers: *
```

#### Tarea 1.4: SecurityConfig Fine-tuning (2h)
- Revisar roles y permisos
- Asegurar endpoints admin protegidos
- Archivo: `src/main/java/com/parque/config/SecurityConfig.java`

### 🔗 Depende De
- Backend compilando sin errores

### 🚀 Cómo Trabajar

1. **Asegurar estar en la rama correcta:**
   ```bash
   git checkout feature/p1-cors-security
   git pull origin feature/p1-cors-security
   ```

2. **Hacer cambios:**
   ```bash
   # Editar archivos
   vim src/main/java/com/parque/security/...
   ```

3. **Commit local:**
   ```bash
   git add src/main/java/com/parque/
   git commit -m "feat(p1): [descripción específica]"
   ```

4. **Mantener sync con backend/phase2:**
   ```bash
   git fetch origin backend/phase2
   git rebase origin/backend/phase2  # O merge si hay conflictos
   ```

5. **Push:**
   ```bash
   git push origin feature/p1-cors-security
   ```

---

## 🧪 PROGRAMADOR 3: INTEGRATION TESTING

### Rama Asignada
```bash
feature/p3-integration-testing
```

### ✅ Trabajo Completado
- [x] IntegrationTestBase para setup común
- [x] application-test.properties con H2
- [x] BookingE2ETest con flujo completo
- [x] ContractTest con 10+ casos
- [x] DashboardIntegrationTest

### 📋 Próximos Pasos (Tareas 3.5)

#### Tarea 3.5: Reporte de Tests (1h)
```bash
# Ejecutar todos los tests
mvn clean verify

# Generar reporte
mvn test

# Ver resultados
cat target/surefire-reports/TEST-com.parque.*.xml
```

**Criterio de Aceptación:**
- [ ] `mvn test` = 100% pases
- [ ] `mvn verify` = 100% pases
- [ ] Coverage >= 80%
- [ ] Reporte generado en `target/surefire-reports/`

### 🔗 Depende De
- Entidades y DTOs implementados
- Controladores con endpoints básicos

### 🚀 Cómo Trabajar

1. **Asegurar estar en la rama correcta:**
   ```bash
   git checkout feature/p3-integration-testing
   git pull origin feature/p3-integration-testing
   ```

2. **Agregar nuevos tests:**
   ```bash
   vim src/test/java/com/parque/booking/BookingE2ETest.java
   ```

3. **Ejecutar tests locales:**
   ```bash
   mvn test -Dtest=BookingE2ETest
   ```

4. **Commit:**
   ```bash
   git add src/test/
   git commit -m "test(p3): [descripción de tests]"
   ```

5. **Mantener sync:**
   ```bash
   git fetch origin backend/phase2
   git rebase origin/backend/phase2
   ```

6. **Push:**
   ```bash
   git push origin feature/p3-integration-testing
   ```

---

## 🔄 SINCRONIZACIÓN ENTRE RAMAS

### Problema: Ambas ramas necesitan cambios de la otra

**Solución:** Usar `git merge` en lugar de rebase

```bash
# En feature/p1-cors-security
git fetch origin feature/p3-integration-testing
git merge origin/feature/p3-integration-testing

# En feature/p3-integration-testing
git fetch origin feature/p1-cors-security
git merge origin/feature/p1-cors-security
```

---

## 📦 INTEGRACIÓN A backend/phase2

### Cuando ambas ramas estén listas:

1. **Merge P1 primero (dependencia crítica):**
   ```bash
   git checkout backend/phase2
   git pull origin backend/phase2
   git merge feature/p1-cors-security --no-ff -m "Merge P1: CORS & Security"
   ```

2. **Merge P3 después:**
   ```bash
   git merge feature/p3-integration-testing --no-ff -m "Merge P3: Integration Testing"
   ```

3. **Resolver conflictos si existen:**
   ```bash
   git status
   # Editar archivos con conflictos
   git add .
   git commit -m "Resolve merge conflicts"
   ```

4. **Push a origin:**
   ```bash
   git push origin backend/phase2
   ```

---

## ⚠️ CONFLICTOS COMUNES Y SOLUCIONES

### Conflicto: Ambos modifican config/SecurityConfig.java

**Causa:** P1 y P3 necesitan importar clases unas de otras

**Solución:**
```bash
# Comunicar cambios
# Si P3 necesita clases de P1:
git merge feature/p1-cors-security
# Resolver conflictos manualmente
# Editar el archivo y conservar ambas versiones
```

### Conflicto: pom.xml modificado en ambas ramas

**Solución:**
```bash
# Maven requiere manejo cuidadoso
git checkout --theirs pom.xml  # O --ours
mvn dependency:resolve
git add pom.xml
git commit
```

---

## 📊 CHECKPOINTS DIARIOS

### Lunes 5 - Final de día
- [ ] P1: CORS funcionando (curl test)
- [ ] P1: JWT sin conflictos
- [ ] Push a `feature/p1-cors-security`

### Martes 6
- [ ] P1: SecurityConfig fine-tuning completado
- [ ] P3: Integration tests sin errores
- [ ] Ambas ramas sincronizadas

### Miércoles 7
- [ ] P3: ContractTest con 20+ casos
- [ ] P3: Dashboard tests pasando
- [ ] Preparar merge a backend/phase2

### Jueves 8
- [ ] Merge P1 a backend/phase2
- [ ] Merge P3 a backend/phase2
- [ ] Resolver conflictos
- [ ] `mvn clean verify` = ✅

---

## 🛠️ HERRAMIENTAS ÚTILES

### Ver diferencias entre ramas:
```bash
git diff backend/phase2..feature/p1-cors-security
```

### Ver cambios no commiteados:
```bash
git status
git diff
```

### Ver log de rama:
```bash
git log feature/p1-cors-security --oneline
```

### Revertir último commit (no pusheado):
```bash
git reset --soft HEAD~1
```

### Revertir cambios en un archivo:
```bash
git checkout -- src/main/java/com/parque/config/WebConfig.java
```

---

## 📞 SOPORTE & ESCALACIÓN

**Bloqueado?**
- Comunicar en standup
- Revisar este documento
- Hacer PR con descripción clara

**Conflicto en merge?**
- No forzar push (--force)
- Comunicar al equipo
- Resolver en conjunto

**Test fallando?**
- Revisar último commit
- Ejecutar `mvn clean test`
- Revisar logs en `target/surefire-reports/`

---

## ✨ BEST PRACTICES

✅ **DO:**
- Hacer commits pequeños y descriptivos
- Comunicar cambios importantes
- Sincronizar frecuentemente (varias veces al día)
- Ejecutar tests antes de push

❌ **DON'T:**
- Hacer commits de código sin compilar
- Pushear sin probar en local
- Mergear sin resolver conflictos
- Usar `--force` en push

---

**Preparado por:** Sistema de Coordinación  
**Última actualización:** 4 de mayo de 2026  
**Status:** 🟢 LISTO PARA INICIO
