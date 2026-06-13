package cr.ac.ucr.sga.model.services;

import cr.ac.ucr.sga.model.entities.CourseEntry;
import cr.ac.ucr.sga.model.trees.AVL;
import cr.ac.ucr.sga.model.trees.BST;
import cr.ac.ucr.sga.model.trees.BTree;
import cr.ac.ucr.sga.model.trees.BTreeNode;
import cr.ac.ucr.sga.model.trees.Tree;
import cr.ac.ucr.sga.model.trees.TreeException;

import java.util.ArrayList;
import java.util.List;

/**
 * CourseSearchService — US-11 (Sprint 3).
 *
 * Construye un Árbol Binario de Búsqueda (BST, clase vista en laboratorio PG-05)
 * con todos los cursos de la malla curricular (CurriculumService), ordenados
 * por código de curso.
 *
 * Permite al administrador buscar un curso de forma eficiente (O(log n) en
 * promedio) y devuelve además el "camino recorrido" durante la búsqueda,
 * útil para resaltarlo en la visualización del Canvas (US-14).
 */
public class CourseSearchService {

    private static CourseSearchService instance;

    private final BST<CourseEntry> bst = new BST<>();

    private CourseSearchService() {
        buildFromCurriculum();
    }

    public static CourseSearchService getInstance() {
        if (instance == null) {
            instance = new CourseSearchService();
        }
        return instance;
    }

    public void buildFromCurriculum() {
        bst.clear();
        CurriculumService curriculum = CurriculumService.getInstance();
        for (String code : curriculum.getAllCourseCodes()) {
            String name = curriculum.getCourseName(code);
            bst.add(new CourseEntry(code, name != null ? name : code));
        }
    }

    public BST<CourseEntry> getTree() {
        return bst;
    }

    public static class SearchResult {
        private final boolean found;
        private final CourseEntry course;
        private final List<CourseEntry> path;
        private final int comparisons;

        public SearchResult(boolean found, CourseEntry course, List<CourseEntry> path, int comparisons) {
            this.found = found;
            this.course = course;
            this.path = path;
            this.comparisons = comparisons;
        }

        public boolean isFound() { return found; }
        public CourseEntry getCourse() { return course; }
        public List<CourseEntry> getPath() { return path; }
        public int getComparisons() { return comparisons; }
    }

    public SearchResult search(String code) {
        List<CourseEntry> path = new ArrayList<>();
        if (bst.isEmpty()) {
            return new SearchResult(false, null, path, 0);
        }

        CourseEntry target = new CourseEntry(code.toUpperCase(), "");
        BTreeNode<CourseEntry> current = bst.root;
        int comparisons = 0;

        while (current != null) {
            path.add(current.data);
            comparisons++;
            int cmp = current.data.compareTo(target);
            if (cmp == 0) {
                return new SearchResult(true, current.data, path, comparisons);
            } else if (cmp > 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }

        return new SearchResult(false, null, path, comparisons);
    }

    public boolean contains(String code) throws TreeException {
        return bst.contains(new CourseEntry(code.toUpperCase(), ""));
    }

    public int size() {
        try {
            return bst.isEmpty() ? 0 : bst.size();
        } catch (TreeException e) {
            return 0;
        }
    }

    public int height() {
        try {
            return bst.isEmpty() ? 0 : bst.height();
        } catch (TreeException e) {
            return 0;
        }
    }
}