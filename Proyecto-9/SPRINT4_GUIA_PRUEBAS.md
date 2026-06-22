# Guía de Pruebas - Sprint 4: Grafos y Mapa del Campus

## Resumen Ejecutivo

Sprint 4 implementa completamente los requisitos de grafos y mapa del campus:
- ✅ US-15: Mapa del campus con edificios (8 nodos) y caminos (12 aristas)
- ✅ US-16: Algoritmo Dijkstra para ruta más corta
- ✅ US-17: Recorridos BFS y DFS con animación
- ✅ US-18: Exportación de reportes en CSV (métricas y rutas)
- ✅ US-19: Integración en aplicación principal

## Requisitos para Ejecutar

- **Java 17+** (configurado en pom.xml)
- **JavaFX 21.0.2** (incluido en dependencias)
- **Maven 3.6+** (compilación)
- **Gson 2.10.1** (carga de JSON)

## Pasos para Compilar y Ejecutar

### 1. Compilar el proyecto
```powershell
cd D:\Programacion2\Proyecto-9\Proyecto-9
mvn clean compile
```
Esto debería compilar sin errores todos los archivos Java nuevos.

### 2. Ejecutar todos los tests
```powershell
mvn test
```

Esto ejecutará:
- Todos los tests anteriores (AVL, BST, Dijkstra, Graph, Lists, etc.)
- ✨ **NUEVO**: `CampusFunctionalTest` (6 tests específicos del campus)

### 3. Ejecutar solo tests del campus
```powershell
mvn test -Dtest=CampusFunctionalTest
```

### 4. Ejecutar la aplicación
```powershell
mvn javafx:run
```

O desde el IDE (IntelliJ IDEA):
- File → Open → Proyecto-9/pom.xml
- Esperar a que Maven descargue dependencias
- Run → Run 'Main' (si existe Run Configuration)

## Credenciales para Pruebas

Dos tipos de usuarios:

### Estudiante
- **Usuario**: `student`
- **Contraseña**: `student123`
- **Acceso**: Visualización del mapa (solo lectura)

### Administrador
- **Usuario**: `admin`
- **Contraseña**: `admin123`
- **Acceso**: Mapa interactivo + Dijkstra + BFS/DFS + Exportación de reportes

## Casos de Prueba

### Test 1: Cargar la aplicación y acceder al mapa (Estudiante)

1. Ejecutar: `mvn javafx:run`
2. Login como estudiante: `student` / `student123`
3. En el menú principal, click en **"Mapa del Campus"**
4. **Esperado**: 
   - Se muestra el mapa con 8 edificios (A-H)
   - Los nombres de los edificios son visibles
   - Las distancias entre edificios aparecen en las líneas

### Test 2: Usar Dijkstra (Admin)

1. Ejecutar: `mvn javafx:run`
2. Login como admin: `admin` / `admin123`
3. Click en **"Mapa del Campus"**
4. En los controles de admin:
   - Seleccionar "Edificio A - Ingeniería" en "Origen"
   - Seleccionar "Edificio G - Enfermería" en "Destino"
   - Click en **"Ejecutar Dijkstra"**
5. **Esperado**:
   - Se resalta el camino en rojo
   - Se muestra la distancia total (ej: "Distancia: 230.0 metros")
   - Se muestra la ruta completa (ej: "Camino: A → C → G")
   - Status muestra el tiempo de ejecución (ej: "Algoritmo ejecutado en 0.123 ms")

### Test 3: BFS (Búsqueda en Anchura)

1. En modo admin, en los controles de admin:
   - Seleccionar "Edificio A - Ingeniería" en "Origen"
   - Click en **"Iniciar BFS"**
2. **Esperado**:
   - Los edificios cambian de color según el orden de visita
   - Status muestra: "✔ BFS iniciado. Nodos encontrados: 8. Tiempo: X.XXX ms"
   - Se muestra el orden de visita: "Orden de visita: A → B → C → D → E → ..."

### Test 4: Paso a Paso (BFS/DFS)

1. Después de iniciar BFS:
   - Click en **"Paso"** (múltiples veces)
2. **Esperado**:
   - Cada click avanza un edificio en el recorrido
   - El edificio actual cambia a color amarillo
   - Status muestra: "Paso 1: Visitando nodo A", "Paso 2: Visitando nodo B", etc.

### Test 5: Animación Automática

1. Después de iniciar BFS o DFS:
   - Click en **"Reproducir Animación"**
2. **Esperado**:
   - Los edificios se colorean automáticamente cada 500ms
   - Status muestra el avance
   - Al terminar: "✔ Recorrido completado."

### Test 6: Reiniciar

1. Durante un recorrido BFS/DFS o después de Dijkstra:
   - Click en **"Reiniciar"**
2. **Esperado**:
   - Todos los edificios vuelven a azul claro
   - Se limpia la distancia y el camino
   - Status: "✔ Estado reiniciado."

### Test 7: Exportación de Reportes (Admin)

1. En los controles de admin, después de ejecutar Dijkstra:
   - Click en **"Generar Reporte Métricas"**
2. **Esperado**:
   - Aparece alerta: "El reporte de métricas se ha guardado en: C:\Users\...\Documents\campus_metrics_report_XXXX.csv"
   - El archivo se crea en la carpeta Documentos del usuario
   - El archivo contiene:
     - Encabezado con fecha
     - Número de edificios (8)
     - Número de caminos (12)
     - Listado de edificios con coordenadas
     - Matriz de distancias (Dijkstra)
     - Análisis de rendimiento

### Test 8: Exportación de Reporte de Ruta

1. Después de ejecutar Dijkstra:
   - Click en **"Generar Reporte Ruta Actual"**
2. **Esperado**:
   - Archivo generado en Documentos
   - Contiene:
     - Punto de inicio y destino
     - Distancia total
     - Número de paradas intermedias
     - Ruta detallada paso a paso

### Test 9: DFS (Búsqueda en Profundidad)

1. Click en **"Iniciar DFS"**
   - Seleccionar un edificio como origen
2. **Esperado**:
   - Status: "✔ DFS iniciado. Nodos encontrados: 8. Tiempo: X.XXX ms"
   - Orden de visita diferente a BFS (profundidad vs anchura)

### Test 10: Cambio de Rol

1. Ejecutar como estudiante y luego cambiar a admin:
   - Logout (botón "Cerrar Sesión" al final del menú)
   - Login como admin
   - Entrar al mapa del campus nuevamente
2. **Esperado**:
   - El panel de controles de admin ahora es **visible**
   - Todas las funciones de Dijkstra, BFS, DFS están disponibles
   - Los reportes se pueden generar

## Tests Automatizados

### Ejecutar suite completa de tests
```powershell
mvn test -q
```

### Tests específicos del campus
```powershell
mvn test -Dtest=CampusFunctionalTest -v
```

Resultados esperados:
- ✅ `testCampusGraphLoads()` - Verifica que el grafo se carga correctamente
- ✅ `testCampusNodesData()` - Verifica que hay 8 nodos con nombres
- ✅ `testDijkstraOnCampus()` - Verifica que Dijkstra funciona
- ✅ `testBFSOnCampus()` - Verifica que BFS recorre todos los nodos
- ✅ `testDFSOnCampus()` - Verifica que DFS recorre todos los nodos
- ✅ `testCampusDijkstraPerformance()` - Verifica que Dijkstra se ejecuta en < 100ms

## Archivos Generados

Al ejecutar la aplicación, los reportes CSV se guardan en:
```
C:\Users\{NombreUsuario}\Documents\campus_metrics_report_XXXX.csv
C:\Users\{NombreUsuario}\Documents\campus_path_report_XXXX.csv
```

Formato de CSV (ejemplo de métricas):
```
=== REPORTE DE MÉTRICAS DEL CAMPUS ===
Fecha de generación: 2026-06-22 14:30:45

--- MÉTRICAS GENERALES DEL GRAFO ---
Número de edificios (nodos): 8
Número de caminos (aristas): 12

--- LISTADO DE EDIFICIOS ---
ID,Nombre,Coordenada X,Coordenada Y
A,Edificio A - Ingeniería,100,100
B,Edificio B - Administración,300,150
...
```

## Validación de Requisitos del Sprint

| Historia | Criterio | Estado |
|----------|----------|--------|
| US-15 | Ver mapa con edificios y conexiones | ✅ Completado |
| US-16 | Dijkstra para ruta más corta | ✅ Completado |
| US-17 | Recorridos BFS/DFS con animación | ✅ Completado |
| US-18 | Informe descargable (CSV) | ✅ Completado |
| US-19 | Integración en aplicación | ✅ Completado |

## Próximos Pasos Opcionales

1. **Exportar a PDF** (requiere dependencia)
2. **Manuales de usuario y técnico** (documentación)
3. **Diagrama UML actualizado** (arquitectura)
4. **Video de demostración** (máx. 10 minutos)
5. **Tag git**: `final-v1.0` para marcar release

## Solución de Problemas

### Problema: "mvn command not found"
**Solución**: Instalar Maven o añadir a PATH. Ver documento SPRINT4_CAMBIOS.md sección "Solución de Problemas"

### Problema: Grafo vacío (sin edificios)
**Solución**: Verificar que `campus.json` existe en `src/main/resources/data/`

### Problema: Error al cargar campus.json
**Solución**: Verificar formato JSON es válido (usar validador online)

### Problema: Los reportes no se generan
**Solución**: Verificar permisos de escritura en carpeta Documentos del usuario

## Contacto y Soporte

Para reportar bugs o problemas:
- Revisar los logs en la consola
- Verificar que todos los archivos nuevos se hayan creado
- Ejecutar `mvn clean` y reintentar compilación

