package cr.ac.ucr.sga.model.services;

import cr.ac.ucr.sga.model.structures.lists.CircularDoublyLinkedList;
import cr.ac.ucr.sga.model.structures.lists.CircularLinkedList;
//clase singleton para manejar el historial de vistas y acciones del usuario
public class SessionHistoryService {

    private static SessionHistoryService instance;

    private final CircularDoublyLinkedList<String> visitedViews;
    private final CircularLinkedList<String> recentActions;

    private SessionHistoryService() {
        visitedViews = new CircularDoublyLinkedList<>();
        recentActions = new CircularLinkedList<>();
    }

    public static SessionHistoryService getInstance() {
        if (instance == null) {
            instance = new SessionHistoryService();
        }

        return instance;
    }

    public void addView(String viewName) {
        visitedViews.add(viewName);
        recentActions.add("Vista visitada: " + viewName);
    }

    public String nextView() {
        String view = visitedViews.next();
        return view == null ? "Sin historial" : view;
    }

    public String previousView() {
        String view = visitedViews.previous();
        return view == null ? "Sin historial" : view;
    }

    public String nextAction() {
        String action = recentActions.next();
        return action == null ? "Sin acciones" : action;
    }

    public int size() {
        return visitedViews.size();
    }
}
