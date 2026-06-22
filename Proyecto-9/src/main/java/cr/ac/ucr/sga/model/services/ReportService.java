package cr.ac.ucr.sga.model.services;

import cr.ac.ucr.sga.model.graph.Dijkstra;
import cr.ac.ucr.sga.model.graph.Graph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servicio para generar y exportar reportes del sistema.
 */
public class ReportService {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private ReportService() {}

    /**
     * Genera un reporte CSV con métricas del grafo del campus.
     *
     * @param outputPath Ruta del archivo de salida (ej: "C:\\Users\\User\\Desktop\\campus_report.csv")
     * @return verdadero si el reporte se generó exitosamente
     */
    public static boolean generateCampusMetricsReport(String outputPath) {
        try {
            CampusGraphService campusService = CampusGraphService.getInstance();
            Graph graph = campusService.getGraph();

            File file = new File(outputPath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            // Encabezado del reporte
            writer.write("=== REPORTE DE MÉTRICAS DEL CAMPUS ===\n");
            writer.write("Fecha de generación: " + LocalDateTime.now().format(DTF) + "\n\n");

            // Sección 1: Métricas generales del grafo
            writer.write("--- MÉTRICAS GENERALES DEL GRAFO ---\n");
            writer.write("Número de edificios (nodos): " + graph.vertexCount() + "\n");
            writer.write("Número de caminos (aristas): " + graph.edgeCount() / 2 + "\n"); // Dividir por 2 porque es no dirigido
            writer.write("\n");

            // Sección 2: Listado de edificios
            writer.write("--- LISTADO DE EDIFICIOS ---\n");
            writer.write("ID,Nombre,Coordenada X,Coordenada Y\n");
            for (String nodeId : campusService.getAllNodeIds()) {
                CampusGraphService.CampusNode node = campusService.getNode(nodeId);
                if (node != null) {
                    writer.write(nodeId + "," + node.name + "," + node.x + "," + node.y + "\n");
                }
            }
            writer.write("\n");

            // Sección 3: Matriz de distancias (para primeros 5 nodos para no sobrecargar)
            writer.write("--- MATRIZ DE DISTANCIAS (DIJKSTRA) ---\n");
            writer.write("Calculando distancias entre edificios...\n");
            List<String> nodeIds = campusService.getAllNodeIds().stream().toList();
            int sampleSize = Math.min(5, nodeIds.size());

            // Encabezado de la matriz
            writer.write("Desde/Hacia");
            for (int i = 0; i < sampleSize; i++) {
                writer.write("," + nodeIds.get(i));
            }
            writer.write("\n");

            // Matriz de distancias
            for (int i = 0; i < sampleSize; i++) {
                String from = nodeIds.get(i);
                writer.write(from);

                long startTime = System.nanoTime();
                Dijkstra.Result dijkstraResult = Dijkstra.compute(graph, from);
                long endTime = System.nanoTime();

                for (int j = 0; j < sampleSize; j++) {
                    String to = nodeIds.get(j);
                    double distance = dijkstraResult.distanceTo(to);

                    if (i == j) {
                        writer.write(",0.0");
                    } else if (Double.isInfinite(distance)) {
                        writer.write(",INF");
                    } else {
                        writer.write("," + String.format("%.1f", distance));
                    }
                }
                writer.write("\n");
            }
            writer.write("\n");

            // Sección 4: Rendimiento
            writer.write("--- ANÁLISIS DE RENDIMIENTO ---\n");
            long dijkstraStartTime = System.nanoTime();
            Dijkstra.Result dijkstraTestResult = Dijkstra.compute(graph, nodeIds.get(0));
            long dijkstraEndTime = System.nanoTime();
            long dijkstraDurationMs = (dijkstraEndTime - dijkstraStartTime) / 1_000_000;

            writer.write("Tiempo de ejecución de Dijkstra: " + dijkstraDurationMs + " ms\n");
            writer.write("Complejidad esperada: O((V+E) log V) donde V=" + graph.vertexCount() + ", E=" + (graph.edgeCount() / 2) + "\n");
            writer.write("\n");

            // Sección 5: Resumen
            writer.write("--- RESUMEN ---\n");
            writer.write("Total de edificios analizados: " + graph.vertexCount() + "\n");
            writer.write("Total de conexiones (bidireccionales): " + (graph.edgeCount() / 2) + "\n");
            writer.write("Reporte generado exitosamente.\n");

            writer.close();
            return true;

        } catch (IOException e) {
            System.err.println("Error generando reporte: " + e.getMessage());
            return false;
        }
    }

    /**
     * Genera un reporte CSV con detalle de una ruta Dijkstra específica.
     *
     * @param outputPath Ruta del archivo de salida
     * @param startNode ID del nodo de inicio
     * @param endNode ID del nodo de destino
     * @return verdadero si el reporte se generó exitosamente
     */
    public static boolean generateDijkstraPathReport(String outputPath, String startNode, String endNode) {
        try {
            CampusGraphService campusService = CampusGraphService.getInstance();
            Graph graph = campusService.getGraph();

            BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));

            writer.write("=== REPORTE DE RUTA - ALGORITMO DIJKSTRA ===\n");
            writer.write("Fecha de generación: " + LocalDateTime.now().format(DTF) + "\n\n");

            // Información de origen y destino
            CampusGraphService.CampusNode startNodeInfo = campusService.getNode(startNode);
            CampusGraphService.CampusNode endNodeInfo = campusService.getNode(endNode);

            writer.write("Punto de inicio: " + (startNodeInfo != null ? startNodeInfo.name : startNode) + "\n");
            writer.write("Punto de destino: " + (endNodeInfo != null ? endNodeInfo.name : endNode) + "\n");
            writer.write("\n");

            // Cálculo de la ruta
            long startTime = System.nanoTime();
            Dijkstra.Result dijkstraResult = Dijkstra.compute(graph, startNode);
            long endTime = System.nanoTime();

            double distance = dijkstraResult.distanceTo(endNode);
            List<String> path = dijkstraResult.pathTo(endNode);

            writer.write("--- RESULTADOS ---\n");
            if (Double.isInfinite(distance)) {
                writer.write("Estado: No existe ruta entre los edificios\n");
            } else {
                writer.write("Distancia total: " + String.format("%.1f", distance) + " metros\n");
                writer.write("Número de paradas intermedias: " + (path.size() - 2) + "\n");
                writer.write("Tiempo de cálculo: " + ((endTime - startTime) / 1_000_000) + " ms\n");
                writer.write("\n");

                writer.write("--- RUTA DETALLADA ---\n");
                for (int i = 0; i < path.size(); i++) {
                    String nodeId = path.get(i);
                    CampusGraphService.CampusNode node = campusService.getNode(nodeId);
                    String nodeName = node != null ? node.name : nodeId;

                    if (i == 0) {
                        writer.write("Inicio: " + nodeName + "\n");
                    } else if (i == path.size() - 1) {
                        writer.write("Fin: " + nodeName + "\n");
                    } else {
                        writer.write("Parada " + i + ": " + nodeName + "\n");
                    }
                }
            }

            writer.close();
            return true;

        } catch (IOException e) {
            System.err.println("Error generando reporte de ruta: " + e.getMessage());
            return false;
        }
    }
}

