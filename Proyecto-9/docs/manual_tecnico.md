Manual Técnico - SGA (Sprint 4)
================================

Versión: final-v1.0
Fecha: 2026-06-22

Contenido
- Arquitectura general
- Paquetes principales
- Descripción de clases clave
- Integración del módulo de grafos
- Datos de entrada: campus.json
- Construcción y ejecución
- Tests automatizados
- Despliegue y packaging

1. Arquitectura general
------------------------
La aplicación está organizada como una aplicación Java modular (module-info.java) con JavaFX para la interfaz gráfica. Los paquetes relevantes añadidos en Sprint 4 son:

- `cr.ac.ucr.sga.model.graph` : implementación de grafos, Dijkstra, BFS y DFS.
- `cr.ac.ucr.sga.model.services` : servicios de carga (CampusGraphService) y reportes (ReportService).
- `cr.ac.ucr.sga.controller` : controladores JavaFX, incluyendo `CampusMapController`.

2. Paquetes principales y responsabilidades
-----------------------------------------
- cr.ac.ucr.sga.controller: controladores JavaFX y manejo de eventos de UI.
- cr.ac.ucr.sga.model.graph: modelos `Graph`, `Vertex`, `Edge`, `Dijkstra`, `Traversals`.
- cr.ac.ucr.sga.model.services: servicios para cargar datos desde JSON (`CampusGraphService`) y exportar reportes (`ReportService`).

3. Clases clave
----------------
- Graph
  - Representa un grafo ponderado usando listas de adyacencia.
  - Métodos relevantes: `addVertex`, `addEdge`, `getEdgesFrom`, `getVertexIds`, `vertexCount`, `edgeCount`.

- Dijkstra
  - Implementación estática: `Dijkstra.compute(Graph g, String source)` devuelve `Dijkstra.Result` con `distanceTo(node)` y `pathTo(node)`.

- Traversals
  - Contiene `bfs(Graph g, String start)` y `dfs(Graph g, String start)` que devuelven listas de ids en orden de visita.

- CampusGraphService
  - Singleton que carga `resources/data/campus.json` y construye el `Graph` utilizado por la UI.
  - Provee utilidades: `getGraph()`, `getAllNodeIds()`, `getAllNodeNames()`, `getNodeIdByName()`.

- CampusMapController
  - Controlador JavaFX que renderiza el grafo sobre un `Canvas`, controla selección Origen/Destino y lanza algoritmos.

4. Integración del módulo de grafos
----------------------------------
`CampusGraphService` deserializa `campus.json` (usando Gson) a DTOs internos y llama a `graph.addVertex(...)` y `graph.addEdge(...)`.

5. Formato de `campus.json`
---------------------------
Ejemplo mínimo (ubicado en `src/main/resources/data/campus.json`):

{
  "buildings": [ { "id":"B1", "name":"Biblioteca", "x":100, "y":200 } ],
  "edges": [ { "from":"B1", "to":"B2", "weight":150 } ]
}

Las coordenadas `x`/`y` se usan para dibujar los nodos en el `Canvas`.

6. Construcción y ejecución
----------------------------
Requisitos: JDK 11+, Maven.

Compilar:
```powershell
cd D:\Programacion2\Proyecto-9\Proyecto-9
mvn clean compile
```

Ejecutar tests:
```powershell
mvn test
```

Ejecutar aplicación JavaFX (si está configurado javafx-maven-plugin):
```powershell
mvn javafx:run
```

7. Tests automatizados
-----------------------
La suite incluye pruebas unitarias para estructuras de datos y pruebas funcionales (`CampusFunctionalTest`) que verifican la carga del grafo, Dijkstra y recorridos.

Si los tests fallan, ejecutar con salida extendida:
```powershell
mvn -X test
```

8. Despliegue y empaquetado
---------------------------
Se recomienda crear una release con tag `final-v1.0` y empaquetar el JAR con dependencias (shade plugin) o usando el `javafx-maven-plugin` para generar un paquete nativo si se requiere.

9. Notas de diseño y decisiones
-------------------------------
- Se eligió representar vértices por `String id` para facilidad de serialización y pruebas.
- Dijkstra devuelve un objeto `Result` para evitar exponer estructuras internas.
- Gson se utiliza para deserializar `campus.json` por su simplicidad.

Fin del Manual Técnico.

