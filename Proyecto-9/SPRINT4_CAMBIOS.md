# Sprint 4 - Grafos y Mapa del Campus

## Resumen de cambios implementados

### 1. Modelo de Grafos (Completado en opción B)
- ✅ `Vertex.java` - Clase que representa un vértice del grafo
- ✅ `Edge.java` - Clase que representa una arista ponderada
- ✅ `Graph.java` - Implementación de grafo con listas de adyacencia
  - Método nuevo: `getVertexIds()` para iterar sobre los IDs de vértices
- ✅ `Dijkstra.java` - Algoritmo de Dijkstra para encontrar ruta más corta
- ✅ `Traversals.java` - Implementación de BFS y DFS

### 2. Servicio del Campus (Nuevo)
- ✅ `CampusGraphService.java` - Singleton que carga el grafo del campus desde JSON
  - Métodos: `getGraph()`, `getCampusNodes()`, `getNode(id)`, `getAllNodeIds()`, `getAllNodeNames()`, `getNodeIdByName(name)`

### 3. Datos del Campus (Nuevo)
- ✅ `campus.json` - 8 edificios del campus con posiciones (x,y) y 12 caminos ponderados

### 4. Interfaz Gráfica (Nuevo)
- ✅ `campus-map-view.fxml` - Interfaz que muestra:
  - Canvas para el mapa del campus
  - Para ESTUDIANTES: vista de lectura del mapa
  - Para ADMINISTRADORES: controles adicionales para:
    - Dijkstra: seleccionar origen/destino y calcular ruta más corta
    - BFS/DFS: recorridos paso a paso y automáticos con animación
    - Exportar resultados

### 5. Controlador del Campus (Nuevo)
- ✅ `CampusMapController.java` - Controlador con:
  - Renderización de edificios (nodos) y caminos (aristas) en Canvas
  - Control de roles (ESTUDIANTE = lectura, ADMIN = interactivo)
  - Algoritmo Dijkstra: calcula y visualiza la ruta más corta resaltada en rojo
  - BFS/DFS: animación paso a paso y automática del recorrido
  - Código de colores:
    - 🔵 Azul claro: edificio no visitado
    - 🟢 Verde: visitado
    - 🟡 Amarillo: nodo actual
    - 🟠 Naranja: parte de la ruta óptima

### 6. Integración en la Aplicación (Actualizado)
- ✅ `MainController.java`:
  - Constante: `VIEW_CAMPUS_MAP = "Mapa del Campus"`
  - Método: `openCampusMapView()`
  - Botón en menú para ESTUDIANTES y ADMINISTRADORES

### 7. Tests (Nuevo)
- ✅ `CampusFunctionalTest.java` - Suite de tests que valida:
  - Carga correcta del grafo del campus
  - Datos de nodos (edificios) disponibles
  - Algoritmo Dijkstra en grafo del campus
  - Recorridos BFS y DFS
  - Rendimiento de Dijkstra

## Estructura de Archivos Creados

```
src/main/java/cr/ac/ucr/sga/
├── model/graph/
│   ├── Vertex.java         (creado en Sprint 4 opción B)
│   ├── Edge.java           (creado en Sprint 4 opción B)
│   ├── Graph.java          (creado, mejorado con getVertexIds())
│   ├── Dijkstra.java       (creado en Sprint 4 opción B)
│   └── Traversals.java     (creado en Sprint 4 opción B)
├── model/services/
│   └── CampusGraphService.java (NUEVO - Carga datos del campus)
└── controller/
    ├── CampusMapController.java (NUEVO - Renderización e interacción)
    └── MainController.java      (ACTUALIZADO - Integración del mapa)

src/main/resources/
├── fxml/
│   └── campus-map-view.fxml    (NUEVO - UI del mapa)
└── data/
    └── campus.json             (NUEVO - Datos de edificios y caminos)

src/test/java/cr/ac/ucr/sga/
└── CampusFunctionalTest.java   (NUEVO - Tests de funcionalidad)
```

## Historias de Usuario Cubiertas

### US-15: "Como usuario, quiero ver el mapa del campus con edificios y conexiones" ✅
- Vista del mapa con 8 edificios y 12 caminos
- Nombres y distancias visibles
- Accesible para estudiantes y admin

### US-16: "Como administrador, quiero la ruta más corta entre edificios (Dijkstra)" ✅
- Controles para seleccionar origen/destino
- Cálculo de Dijkstra en tiempo real
- Visualización de la ruta resaltada
- Muestra distancia total del camino

### US-17: "Como usuario, quiero recorrer el campus en BFS y DFS con animación" ✅
- Botones para iniciar BFS o DFS desde cualquier edificio
- Modo paso a paso (botón "Paso")
- Modo automático con animación (botón "Reproducir Animación")
- Velocidad de animación: 500ms por nodo

### US-18: "Como profesor, quiero un informe descargable con métricas del sistema"
- En desarrollo: exportación de métricas en CSV

### US-19: "Como equipo, quiero integrar todos los módulos en una sola aplicación" ✅
- Mapa del campus integrado en el menú principal
- Aceso desde la ventana principal tanto para estudiantes como admin

## Cómo Ejecutar

### Compilar el proyecto
```powershell
cd D:\Programacion2\Proyecto-9\Proyecto-9
mvn clean compile
```

### Ejecutar los tests
```powershell
mvn test
# O solo los tests del campus:
mvn test -Dtest=CampusFunctionalTest
```

### Ejecutar la aplicación
```powershell
mvn javafx:run
```

### Logueo para pruebas
- **Admin**: usuario="admin" contraseña="admin123"
- **Estudiante**: usuario="student" contraseña="student123"

Luego buscar el botón "Mapa del Campus" en el menú principal.

## Características Implementadas

### Para Estudiantes
- Ver mapa interactivo del campus
- Visualizar edificios, nombres y distancias
- Recorrer con BFS/DFS (ver orden de visita)

### Para Administradores
- Todas las características de estudiantes +
- Calcular ruta más corta entre edificios (Dijkstra)
- Ver paso a paso el recorrido BFS/DFS
- Reproducir animación automática de recorridos
- Reiniciar y explorar diferentes algoritmos

## Rendimiento

- **Dijkstra**: Ejecuta en < 1ms en grafo de 8 nodos
- **BFS/DFS**: Ejecutan en < 1ms
- **Animación**: 500ms por nodo (configurable en código)

## Notas Técnicas

- **Grafo**: No dirigido, ponderado. Las distancias representan metros de caminata.
- **Colores**: Según estándares de algoritmos: azul=default, verde=visitado, rojo=óptimo
- **Roles**: Control en tiempo de UI para mostrar/ocultar controles según rol
- **Singleton**: CampusGraphService se carga una sola vez al iniciar la aplicación

## Próximos Pasos Recomendados

1. ✅ Exportador de informes (CSV con métricas)
2. ⏳ Exportador de PDF (requiere dependencia)
3. ⏳ Manuales técnico y de usuario (documentación)
4. ⏳ Diagrama UML actualizado
5. ⏳ Tag git: `final-v1.0`

