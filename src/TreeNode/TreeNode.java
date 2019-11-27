package TreeNode;

import java.util.List;

public class TreeNode<T> {

    private T data;
    private TreeNode<T> parent;
    private List<TreeNode<T>> children;

    public TreeNode(T data) {
        this.data = data;
    }

    public T getData() {
        return this.data;
    }

    public void addChild(TreeNode<T> child) {
        this.children.add(child);
        child.setParent(this);
    }

    public List<TreeNode<T>> getChildren() {
        return this.children;
    }

    public TreeNode<T> getParent() {
        return this.parent;
    }

    public void setParent(TreeNode<T> parent) {
        this.parent = parent;
    }
}
