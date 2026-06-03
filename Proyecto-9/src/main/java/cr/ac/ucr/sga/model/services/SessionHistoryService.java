package cr.ac.ucr.sga.model.services;

import cr.ac.ucr.sga.model.structures.lists.CircularDoublyLinkedList;
import cr.ac.ucr.sga.model.structures.lists.CircularLinkedList;

/**
 * Servicio singleton que lleva el historial de vistas visitadas en la sesión.
 *
 * Estructura usada:
 *   CircularDoublyLinkedList<String> → permite navegar hacia adelante y atrás
 *   circularmente (botones Atrás / Adelante en el encabezado).
 *
 * Sprint 2: se agregó reset() para limpiar el historial al cerrar sesión.
 */
public class SessionHistoryService {

    private static SessionHistoryService instance;

    private final CircularDoublyLinkedList<String> visitedViews;
    private final CircularLinkedList<String> recentActions;

    private SessionHistoryService() {
        visitedViews  = new CircularDoublyLinkedList<>();
        recentActions = new CircularLinkedList<>();
    }

    public static SessionHistoryService getInstance() {
        if (instance == null) instance = new SessionHistoryService();
        return instance;
    }

    /** Agrega una vista al historial circular. */
    public void addView(String viewName) {
        visitedViews.add(viewName);
        recentActions.add("Vista visitada: " + viewName);
    }

    /** Avanza en el historial circular y devuelve el nombre de la siguiente vista. */
    public String nextView() {
        String view = visitedViews.next();
        return view == null ? "Sin historial" : view;
    }

    /** Retrocede en el historial circular y devuelve el nombre de la vista anterior. */
    public String previousView() {
        String view = visitedViews.previous();
        return view == null ? "Sin historial" : view;
    }

    /** Devuelve la vista actual sin mover el cursor. */
    public String getCurrentView() {
        String view = visitedViews.current();
        return view == null ? "Sin historial" : view;
    }

    /** Avanza en las acciones recientes. */
    public String nextAction() {
        String action = recentActions.next();
        return action == null ? "Sin acciones" : action;
    }

    /** Número de entradas en el historial. */
    public int size() {
        try {
            return visitedViews.isEmpty() ? 0 : visitedViews.size();
        } catch (cr.ac.ucr.sga.model.structures.lists.ListException e) {
            return 0;
        }
    }

    /**
     * Limpia el historial de vistas y acciones.
     * Se llama al cerrar sesión para que la siguiente sesión empiece en limpio.
     */
    public void reset() {
        visitedViews.clear();
        recentActions.clear();
    }
}