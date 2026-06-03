package cr.ac.ucr.sga.model.services;

import cr.ac.ucr.sga.model.entities.Tramit;
import cr.ac.ucr.sga.model.structures.lists.SimpleLinkedList;
import cr.ac.ucr.sga.model.structures.stacks.ArrayStack;
import cr.ac.ucr.sga.model.structures.stacks.LinkedStack;
import cr.ac.ucr.sga.model.structures.stacks.Stack;
import cr.ac.ucr.sga.model.structures.stacks.StackException;

public class TramitService {
    private static TramitService instance;

    private final ArrayStack<Tramit> arrayStack = new ArrayStack<>(50);
    private final LinkedStack<Tramit> linkedStack = new LinkedStack<>();
    private Stack<Tramit> activeStack = arrayStack;
    private final SimpleLinkedList<Tramit> allTramits = new SimpleLinkedList<>();

    private TramitService() {
        // Cargar trámites persistidos al iniciar
        loadTramits();
    }

    public static TramitService getInstance() {
        if (instance == null) {
            instance = new TramitService();
        }
        return instance;
    }

    public void pushTramit(Tramit tramit) {
        try {
            activeStack.push(tramit);
        } catch (StackException e) {
            throw new RuntimeException(e);
        }
        allTramits.addLast(tramit);
        saveTramits();
    }

    public Tramit popTramit() throws StackException {
        Tramit tramit = activeStack.pop();
        saveTramits();
        return tramit;
    }

    public Tramit peekTramit() throws StackException {
        return activeStack.peek();
    }

    public void setStackType(String type) {
        if ("linked".equalsIgnoreCase(type)) {
            activeStack = linkedStack;
        } else {
            activeStack = arrayStack;
        }
    }

    public boolean isEmpty() {
        return activeStack.isEmpty();
    }

    public int size() {
        return activeStack.size();
    }

    public SimpleLinkedList<Tramit> getAllTramits() {
        return allTramits;
    }

    public void saveTramits() {
        JsonService.saveTramits(allTramits.toArray(new Tramit[allTramits.size()]));
    }

    public void loadTramits() {
        Tramit[] tramits = JsonService.loadTramits();
        allTramits.clear();
        activeStack.clear();
        for (Tramit tramit : tramits) {
            allTramits.addLast(tramit);
            try {
                activeStack.push(tramit);
            } catch (StackException e) {
                throw new RuntimeException(e);
            }
        }
    }
}