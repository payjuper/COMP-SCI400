/**
 * File name: BinarySearchTree.java
 * Author: Youngkyo Kim
 * Major: Computer Science
 * Description: This is a university assignment for CS400, 
 * implementing a P101.BinarySearchTree data structure.
 */
import java.util.LinkedList;
import java.util.Queue;

/**
 * This class implements a Binary Search Tree data structure.
 * It stores comparable data in a sorted order and implements the SortedCollection interface.
 * The tree structure is built using BinaryNode objects, 
 * where each node has references to a parent, left child, and right child.
 *
 * @param <T> The type of data stored in the tree, which must be comparable.
 */
public class BinarySearchTree<T extends Comparable<T>> implements SortedCollection<T> {
    
    /**
     * The root node of the binary search tree.
     * This field is null if the tree is empty.
     */
    protected BinaryNode<T> root;

    /**
     * Constructs an empty Binary Search Tree.
     * The root of the new tree is initialized to null.
     */
    public BinarySearchTree() {
        this.root = null;
    }

    /**
     * Inserts a new data value into the sorted collection.
     *
     * @param data The new value being inserted.
     * @throws NullPointerException if the data argument is null.
     */
    @Override
    public void insert(T data) throws NullPointerException {
        if (data == null) {
            throw new NullPointerException("Data cannot be null.");
        }
        
        BinaryNode<T> newNode = new BinaryNode<>(data);
        
        if (this.root == null) {
            this.root = newNode;
            return;
        }
        
        insertHelper(newNode, this.root);
    }

    /**
     * NEW: One-parameter insertHelper method required by P104.
     * This allows subclasses like RedBlackTree to directly insert a new node.
     *
     * @param newNode the node to be inserted
     */
    protected void insertHelper(BinaryNode<T> newNode) {
        if (this.root == null) {
            this.root = newNode;
            return;
        }
        insertHelper(newNode, this.root); // delegate to recursive helper
    }

    /**
     * Recursive helper for inserting a new node into the correct position.
     *
     * @param newNode the new node to insert
     * @param subtree the root of the subtree we are inserting into
     */
    protected void insertHelper(BinaryNode<T> newNode, BinaryNode<T> subtree) {
        int compareResult = newNode.getData().compareTo(subtree.getData());

        if (compareResult <= 0) {
            if (subtree.getLeft() == null) {
                subtree.setLeft(newNode);
                newNode.setParent(subtree);
            } else {
                insertHelper(newNode, subtree.getLeft());
            }
        } else {
            if (subtree.getRight() == null) {
                subtree.setRight(newNode);
                newNode.setParent(subtree);
            } else {
                insertHelper(newNode, subtree.getRight());
            }
        }
    }

    @Override
    public boolean contains(Comparable<T> data) throws NullPointerException {
        if (data == null) {
            throw new NullPointerException("Data cannot be null.");
        }
        
        BinaryNode<T> current = root;
        while (current != null) {
            int compareResult = data.compareTo(current.getData());
            
            if (compareResult == 0) {
                return true;
            } else if (compareResult < 0) {
                current = current.getLeft();
            } else {
                current = current.getRight();
            }
        }
        return false;
    }

    @Override
    public int size() {
        if (root == null) return 0;
        
        int count = 0;
        Queue<BinaryNode<T>> queue = new LinkedList<>();
        queue.add(root);
        
        while (!queue.isEmpty()) {
            BinaryNode<T> current = queue.poll();
            count++;
            
            if (current.getLeft() != null) queue.add(current.getLeft());
            if (current.getRight() != null) queue.add(current.getRight());
        }
        return count;
    }

    @Override
    public boolean isEmpty() {
        return this.root == null;
    }

    @Override
    public void clear() {
        this.root = null;
    }

    // --- Testing methods (Required by assignment) ---
    public boolean test1() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.insert(10);
        tree.insert(5);
        tree.insert(15);
        return tree.size() == 3 && tree.contains(10) && tree.contains(5) && tree.contains(15);
    }

    public boolean test2() {
        BinarySearchTree<String> tree = new BinarySearchTree<>();
        tree.insert("apple");
        tree.insert("banana");
        tree.insert("orange");
        tree.clear();
        return tree.size() == 0 && tree.isEmpty();
    }

    public boolean test3() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.insert(20);
        tree.insert(10);
        tree.insert(30);
        tree.insert(10); 
        tree.insert(5);
        return tree.size() == 5 && tree.contains(10) && tree.contains(5) && tree.contains(30);
    }

    public static void main(String[] args) {
        BinarySearchTree<Integer> testTree = new BinarySearchTree<>();
        System.out.println("Test1: " + testTree.test1());
        System.out.println("Test2: " + testTree.test2());
        System.out.println("Test3: " + testTree.test3());
    }
}
