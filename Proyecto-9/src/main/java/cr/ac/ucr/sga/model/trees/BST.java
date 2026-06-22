package cr.ac.ucr.sga.model.trees;

import cr.ac.ucr.sga.model.trees.BTree;
import cr.ac.ucr.sga.model.trees.BTreeNode;


public class BST<T extends Comparable<T>> extends BTree<T> {

    @Override
    public void remove(T element) throws TreeException {
        if (isEmpty()) throw new TreeException("Binary Search Tree is empty");
        this.root = remove(root, element);
    }

    private BTreeNode<T> remove(BTreeNode<T> node, T element) {
        if (node != null) {
            if (compareElements(element, node.data) < 0)
                node.left = remove(node.left, element);
            else if (compareElements(element, node.data) > 0)
                node.right = remove(node.right, element);
            else if (equals(element, node.data)) {
                if (node.left == null && node.right == null) return null;
                else if (node.right != null && node.left == null) return node.right;
                else if (node.left != null && node.right == null) return node.left;
                else {
                    T minValue = min(node.right);
                    node.data = minValue;
                    node.right = remove(node.right, minValue);
                }
            }
        }
        return node;
    }

    @Override
    public boolean contains(T element) throws TreeException {
        if (isEmpty()) throw new TreeException("Binary Search Tree is empty");
        return binarySearch(this.root, element);
    }

    private boolean binarySearch(BTreeNode<T> node, T element) {
        if (node == null) return false;
        if (equals(node.data, element)) return true;
        else if (compareElements(element, node.data) < 0)
            return binarySearch(node.left, element);
        else return binarySearch(node.right, element);
    }

    @Override
    public void add(T element) {
        this.root = add(root, element);
    }

    private BTreeNode<T> add(BTreeNode<T> node, T element) {
        if (node == null) {
            node = new BTreeNode<>(element);
        } else if (compareElements(element, node.data) < 0)
            node.left = add(node.left, element);
        else if (compareElements(element, node.data) > 0)
            node.right = add(node.right, element);
        return node;
    }

    @Override
    public T min() throws TreeException {
        if (isEmpty()) throw new TreeException("Binary Search Tree is empty");
        return min(root);
    }

    public T min(BTreeNode<T> node) {
        if (node.left != null) return min(node.left);
        return node.data;
    }

    @Override
    public T max() throws TreeException {
        if (isEmpty()) throw new TreeException("Binary Search Tree is empty");
        return max(root);
    }

    private T max(BTreeNode<T> node) {
        if (node.right != null) return max(node.right);
        return node.data;
    }

    @Override
    public String preOrder() throws TreeException {
        if (isEmpty()) throw new TreeException("Binary Search Tree is empty");
        return preOrder(root);
    }

    @Override
    public String toString() {
        if (isEmpty()) return "Binary Tree is empty";
        String result = "Binary Tree Tour\n";
        try {
            result += "PreOrder (N-L-R): " + preOrder() + "\n";
            result += "InOrder (L-N-R): " + inOrder() + "\n";
            result += "PostOrder (L-R-N): " + postOrder() + "\n";
        } catch (TreeException e) {
            result += "Error: " + e.getMessage();
        }
        return result;
    }
}