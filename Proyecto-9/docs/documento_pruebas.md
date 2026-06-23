Documento de Pruebas - Sprint 4
================================

Objetivo
--------
Verificar la correcta implementación del módulo de grafos: carga del mapa del campus, algoritmo de Dijkstra, recorridos BFS/DFS y la exportación de reportes.

Instrucciones para ejecutar las pruebas
--------------------------------------
1. Asegúrese de tener Maven instalado o use IntelliJ IDEA para ejecutar las pruebas.
2. Desde PowerShell en la carpeta del proyecto:

```powershell
cd D:\Programacion2\Proyecto-9\Proyecto-9
mvn test
```

Pruebas incluidas
-----------------
- `CampusFunctionalTest` : pruebas funcionales que cargan `campus.json`, ejecutan Dijkstra y verifican BFS/DFS.
- Suite de pruebas unitarias existentes (AVLTest, BSTTest, etc.) no fueron modificadas en este sprint.

Plantilla de reporte (ejecutar y pegar salida)
---------------------------------------------
Ejecute:
```powershell
mvn -Dtest=CampusFunctionalTest -DtrimStackTrace=false -DskipTests=false test
```
Copie las líneas relevantes de salida (fallos o éxitos) y péguelas debajo.

Resultados esperados (resumen)
-----------------------------
- Todas las pruebas de `CampusFunctionalTest` deben pasar.
- Dijkstra debe ejecutarse en tiempo razonable (<100ms) en el grafo de ejemplo.

Registro de pruebas (rellenar después de ejecutar):

- Fecha:
- Usuario que ejecutó las pruebas:
- Máquina / JDK:
- Resultado: PASÓ / FALLÓ
- Observaciones:

Fin del documento de pruebas.

