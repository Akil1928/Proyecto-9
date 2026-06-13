package cr.ac.ucr.sga.model.trees;

import cr.ac.ucr.sga.model.trees.BTreeNode;
import cr.ac.ucr.sga.model.trees.Tree;

import java.util.Random;

public class BTree<T extends Comparable<T>> implements Tree<T> {
    public BTreeNode<T> root;

    public BTree() {
        this.root = null;
    }

    @Override
    public int size() throws TreeException {
        if (isEmpty()) throw new TreeException("Binary Tree is empty");
        return size(root);
    }

    private int size(BTreeNode<T> node) {
        if (node == null) return 0;
        return size(node.left) + size(node.right) + 1;
    }

    @Override
    public void clear() {
        this.root = null;
    }

    @Override
    public boolean isEmpty() {
        return this.root == null;
    }

    @Override
    public boolean contains(T element) throws TreeException {
        if (isEmpty()) throw new TreeException("Binary Tree is empty");
        return binarySearch(this.root, element);
    }

    private boolean binarySearch(BTreeNode<T> node, T element) {
        if (node == null) return false;
        if (equals(node.data, element)) return true;
        return binarySearch(node.left, element) || binarySearch(node.right, element);
    }

    @Override
    public void add(T element) {
        this.root = add(root, element, "root");
    }

    private BTreeNode<T> add(BTreeNode<T> node, T element, String path) {
        if (node == null) {
            node = new BTreeNode<>(element, path);
        } else {
            int value = new Random().nextInt(10);
            if (value % 2 == 0) {
                node.left = add(node.left, element, path + "/left");
            } else {
                node.right = add(node.right, element, path + "/right");
            }
        }
        return node;
    }

    @Override
    public void remove(T element) throws TreeException {
        if (isEmpty()) throw new TreeException("Binary Tree is empty");
        boolean[] removed = new boolean[1];
        root = remove(root, element, removed);
        if (!removed[0]) throw new TreeException("Element not found in Binary Tree");
    }

    private BTreeNode<T> remove(BTreeNode<T> node, T element, boolean[] removed) {
        if (node == null) return null;

        if (equals(node.data, element)) {
            removed[0] = true;
            if (node.left == null && node.right == null) return null;
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;
            T successor = min(node.right);
            node.data = successor;
            boolean[] dummy = new boolean[1];
            node.right = remove(node.right, successor, dummy);
            return node;
        } else {
            node.left = remove(node.left, element, removed);
            if (removed[0]) return node;
            node.right = remove(node.right, element, removed);
            return node;
        }
    }

    @Override
    public int height(T element) throws TreeException {
        if (isEmpty()) throw new TreeException("Binary Tree is empty");
        BTreeNode<T> target = findNode(root, element);
        if (target == null) throw new TreeException("Element not found in Binary Tree");
        return height(target);
    }

    @Override
    public int height() throws TreeException {
        if (isEmpty()) throw new TreeException("Binary Tree is empty");
        return height(root);
    }

    public int height(BTreeNode<T> node) {
        if (node == null) return 0;
        return 1 + Math.max(height(node.left), height(node.right));
    }

    private BTreeNode<T> findNode(BTreeNode<T> node, T element) {
        if (node == null) return null;
        if (equals(node.data, element)) return node;
        BTreeNode<T> foundLeft = findNode(node.left, element);
        if (foundLeft != null) return foundLeft;
        return findNode(node.right, element);
    }

    @Override
    public T min() throws TreeException {
        if (isEmpty()) throw new TreeException("Binary Tree is empty");
        return min(root);
    }

    private T min(BTreeNode<T> node) {
        if (node == null) return null;
        if (node.left != null && node.right != null) {
            return minElement(node.data, minElement(min(node.left), min(node.right)));
        } else if (node.left != null) {
            return minElement(node.data, min(node.left));
        } else if (node.right != null) {
            return minElement(node.data, min(node.right));
        } else {
            return node.data;
        }
    }

    private T minElement(T a, T b) {
        if (a == null) return b;
        if (b == null) return a;
        return compareElements(a, b) <= 0 ? a : b;
    }

    @Override
    public T max() throws TreeException {
        if (isEmpty()) throw new TreeException("Binary Tree is empty");
        return max(root);
    }

    private T max(BTreeNode<T> node) {
        if (node == null) return null;
        if (node.left != null && node.right != null) {
            return maxElement(node.data, maxElement(max(node.left), max(node.right)));
        } else if (node.left != null) {
            return maxElement(node.data, max(node.left));
        } else if (node.right != null) {
            return maxElement(node.data, max(node.right));
        } else {
            return node.data;
        }
    }

    private T maxElement(T a, T b) {
        if (a == null) return b;
        if (b == null) return a;
        return compareElements(a, b) >= 0 ? a : b;
    }

    @Override
    public String preOrder() throws TreeException {
        if (isEmpty()) throw new TreeException("Binary Tree is empty");
        return preOrder(root);
    }

    protected String preOrder(BTreeNode<T> node) {
        String result = "";
        if (node != null) {
            result = node.data + ", ";
            result += preOrder(node.left);
            result += preOrder(node.right);
        }
        return result;
    }

    @Override
    public String inOrder() throws TreeException {
        if (isEmpty()) throw new TreeException("Binary Tree is empty");
        return inOrder(root);
    }

    protected String inOrder(BTreeNode<T> node) {
        String result = "";
        if (node != null) {
            result += inOrder(node.left);
            result += node.data + ", ";
            result += inOrder(node.right);
        }
        return result;
    }

    @Override
    public String postOrder() throws TreeException {
        if (isEmpty()) throw new TreeException("Binary Tree is empty");
        return postOrder(root);
    }

    protected String postOrder(BTreeNode<T> node) {
        String result = "";
        if (node != null) {
            result += postOrder(node.left);
            result += postOrder(node.right);
            result += node.data + ", ";
        }
        return result;
    }

    @Override
    public String nodeHeight() throws TreeException {
        if (isEmpty()) throw new TreeException("Binary Tree is empty");
        StringBuilder sb = new StringBuilder();
        buildNodeHeight(root, sb);
        return sb.toString();
    }

    private void buildNodeHeight(BTreeNode<T> node, StringBuilder sb) {
        if (node == null) return;
        sb.append(node.data).append(": ").append(height(node)).append("\n");
        buildNodeHeight(node.left, sb);
        buildNodeHeight(node.right, sb);
    }

    @Override
    public String toString() {
        if (isEmpty()) return "Binary Tree is empty";
        String result = "Binary Tree Tour\n";
        result += "PreOrder (N-L-R): " + preOrder(root) + "\n";
        result += "InOrder (L-N-R): " + inOrder(root) + "\n";
        result += "PostOrder (L-R-N): " + postOrder(root) + "\n";
        return result;
    }

    public boolean equals(T a, T b) {
        return a == null ? b == null : a.equals(b);
    }

    public int compareElements(T a, T b) {
        return a.compareTo(b);
    }
}