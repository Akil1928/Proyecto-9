package cr.ac.ucr.sga.model.trees;

public class BTreeNode<T> {
    public T data;
    public BTreeNode<T> left, right;
    public String path;

    public BTreeNode(T data) {
        this.data = data;
        this.left = this.right = null;
    }

    public BTreeNode(T data, String path) {
        this.data = data;
        this.path = path;
        this.left = this.right = null;
    }
}