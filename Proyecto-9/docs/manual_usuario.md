Manual de Usuario - Sistema de Gestión Académica (SGA)
=====================================================

Versión: final-v1.0
Fecha: 2026-06-22

Resumen
-------
Este documento explica cómo usar la aplicación SGA desde la perspectiva de un usuario (Estudiante y Administrador). Contiene instrucciones paso a paso, capturas de pantalla sugeridas (marcadores) y consejos de uso para las funciones desarrolladas en el Sprint 4: visualización del mapa del campus, recorrido BFS/DFS y ruta más corta (Dijkstra), además de la exportación de reportes.

Contenido
- Introducción
- Requisitos previos
- Inicio de sesión
- Vista principal
- Mapa del Campus (Estudiante)
- Mapa del Campus (Administrador)
- Recorridos BFS/DFS
- Buscar ruta más corta (Dijkstra)
- Exportar reportes
- Preguntas frecuentes

1. Introducción
----------------
La aplicación SGA permite gestionar aspectos académicos y ofrece visualizaciones interactivas del campus para facilitar la orientación y planificación de rutas. El Sprint 4 añade funciones de grafos: mapa con edificios y conexiones, algoritmos de búsqueda y exportación de métricas.

2. Requisitos previos
---------------------
- Java 11 o superior instalado.
- Maven (opcional para ejecutar desde la línea de comandos) o un IDE como IntelliJ IDEA.
- Repositorio del proyecto descargado y compilado.

3. Inicio de sesión
-------------------
1. Abra la aplicación (ver sección de instalación del Manual Técnico si es necesario).
2. En la pantalla de login, ingrese sus credenciales.
   - Cuenta de ejemplo (Administrador): usuario `admin` / contraseña `admin123`
   - Cuenta de ejemplo (Estudiante): usuario `student` / contraseña `student123`

4. Vista principal
-------------------
Al iniciar sesión verá el menú principal con accesos a: Búsqueda de cursos, Árbol de prerrequisitos, Trámites y Mapa del Campus.

5. Mapa del Campus (Estudiante)
-------------------------------
Funcionalidad disponible:
- Visualización del mapa con edificios (nodos) y caminos (aristas).
- Seleccionar nodos con el ratón para ver información básica.

Cómo usar:
1. En el menú principal seleccione "Mapa del Campus".
2. El mapa mostrará los edificios sobre un fondo del campus.
3. Haga zoom/pan si la interfaz lo permite para acercar áreas concretas.

6. Mapa del Campus (Administrador)
----------------------------------
Funciones adicionales para el rol Administrador:
- Ejecutar Dijkstra para obtener la ruta más corta entre edificios.
- Iniciar recorridos BFS y DFS con animación.
- Exportar reportes CSV con métricas del grafo.

Cómo usar Dijkstra:
1. Seleccione un edificio origen en el desplegable "Origen".
2. Seleccione un edificio destino en el desplegable "Destino".
3. Pulse el botón "Calcular ruta más corta".
4. El mapa resaltará el camino y mostrará la distancia total.

7. Recorridos BFS / DFS
-----------------------
El administrador puede iniciar recorridos que muestran el orden de visita de nodos.

Controles:
- Iniciar BFS: recorre el grafo por niveles desde el nodo origen.
- Iniciar DFS: recorre el grafo en profundidad.
- Paso / Pausa / Reproducir: controla la animación paso a paso.

8. Exportar reportes
--------------------
Desde la vista de administrador puede exportar un informe en formato CSV con las siguientes métricas:
- Número de nodos y aristas
- Ruta actual y distancia
- Tiempos de ejecución de Dijkstra (ms)

Ubicación del archivo exportado: Carpeta Documentos del usuario o ruta indicada en el diálogo de guardado.

9. Preguntas frecuentes
------------------------
Q: ¿Qué hago si el mapa no carga?
A: Revise que `campus.json` existe en `src/main/resources/data/` y que la aplicación tiene permisos para leer recursos. Reinicie la aplicación.

Q: ¿Puedo cambiar la imagen de fondo del campus?
A: Sí, el administrador puede subir una nueva imagen (si se habilitó esa opción). Consulte el Manual Técnico para detalles.

Capturas (sugeridas)
- Pantalla de inicio de sesión
- Vista principal con botón "Mapa del Campus"
- Mapa con ruta Dijkstra resaltada
- Panel de exportación de reportes

Fin del Manual de Usuario.

