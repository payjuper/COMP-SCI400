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
        // Throw exception if the data is null, as per the interface contract.
        if (data == null) {
            throw new NullPointerException("Data cannot be null.");
        }
        
        // Create a new node to hold the data.
        BinaryNode<T> newNode = new BinaryNode<>(data);
        
        // If the tree is empty, the new node becomes the root.
        if (this.root == null) {
            this.root = newNode;
            return;
        }
        
        // If the tree is not empty, use a helper method to find the correct
        // insertion position recursively.
        insertHelper(newNode, this.root);
    }

    /**
     * Performs a recursive search to find the correct insertion spot for a new node.
     * Compares the new node's data with the current subtree's root 
     * to decide whether to go left or right.
     *
     * @param newNode   The new node to be inserted.
     * @param subtree   The root of the current subtree being considered.
     */
    protected void insertHelper(BinaryNode<T> newNode, BinaryNode<T> subtree) {
        // Use compareTo to determine the relative order of the data.
        int compareResult = newNode.getData().compareTo(subtree.getData());

        // If the new data is less than or equal to the current node's data, go left.
        if (compareResult <= 0) {
            // If the left child is null, insert the new node here.
            if (subtree.getLeft() == null) {
                subtree.setLeft(newNode);
                newNode.setParent(subtree);
            } else {
                // Otherwise, continue the search recursively down the left subtree.
                insertHelper(newNode, subtree.getLeft());
            }
        } 
        // If the new data is greater than the current node's data, go right.
        else {
            // If the right child is null, insert the new node here.
            if (subtree.getRight() == null) {
                subtree.setRight(newNode);
                newNode.setParent(subtree);
            } else {
                // Otherwise, continue the search recursively down the right subtree.
                insertHelper(newNode, subtree.getRight());
            }
        }
    }

    /**
     * Checks whether a specific data value is stored in the tree.
     *
     * @param data The value to check for in the collection.
     * @return true if the collection contains the data, and false otherwise.
     * @throws NullPointerException if the data argument is null.
     */
    @Override
    public boolean contains(Comparable<T> data) throws NullPointerException {
        // Throw exception if the data is null.
        if (data == null) {
            throw new NullPointerException("Data cannot be null.");
        }
        
        // Start searching from the root of the tree.
        BinaryNode<T> current = root;
        
        // Traverse the tree until a null node is reached or the data is found.
        while (current != null) {
            // Compare the search data with the current node's data.
            int compareResult = data.compareTo(current.getData());
            
            if (compareResult == 0) {
                // Data is found.
                return true;
            } else if (compareResult < 0) {
                // Search data is smaller, so go left.
                current = current.getLeft();
            } else {
                // Search data is larger, so go right.
                current = current.getRight();
            }
        }
        // Data was not found in the tree.
        return false;
    }

    /**
     * Counts the number of values in the collection by traversing all nodes.
     *
     * @return The number of values in the collection, including duplicates.
     */
    @Override
    public int size() {
        // Return 0 if the tree is empty.
        if (root == null) {
            return 0;
        }
        
        // Use a queue for a level-order traversal to count all nodes.
        int count = 0;
        Queue<BinaryNode<T>> queue = new LinkedList<>();
        queue.add(root);
        
        while (!queue.isEmpty()) {
            // Dequeue a node and increment the count.
            BinaryNode<T> current = queue.poll();
            count++;
            
            // Add the left child to the queue if it exists.
            if (current.getLeft() != null) {
                queue.add(current.getLeft());
            }
            // Add the right child to the queue if it exists.
            if (current.getRight() != null) {
                queue.add(current.getRight());
            }
        }
        return count;
    }

    /**
     * Checks if the collection is empty.
     *
     * @return true if the collection contains 0 values, false otherwise.
     */
    @Override
    public boolean isEmpty() {
        return this.root == null;
    }

    /**
     * Removes all values and duplicates from the collection.
     * This is done by simply setting the root to null, allowing the
     * Java garbage collector to remove the disconnected nodes.
     */
    @Override
    public void clear() {
        this.root = null;
    }

    // --- Testing methods (Required by assignment) ---
    /**
     * Tests insertion, size, and contains methods with Integer data.
     *
     * @return true if all test assertions pass, false otherwise.
     */
    public boolean test1() {
        System.out.println("Test 1: Insert integers, test size and contains.");
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.insert(10);
        tree.insert(5);
        tree.insert(15);
        tree.insert(3);
        tree.insert(7);
        tree.insert(12);
        tree.insert(17);
        
        if (tree.size() != 7) {
            System.out.println("Test 1 FAILED: size() is incorrect. Expected: 7, Actual: " + tree.size());
            return false;
        }
        
        if (!tree.contains(7) || !tree.contains(10) || !tree.contains(17) || tree.contains(99)) {
            System.out.println("Test 1 FAILED: contains() is incorrect.");
            return false;
        }
        
        System.out.println("Test 1 PASSED");
        return true;
    }
    
    /**
     * Tests insertion, size, and clear methods with String data.
     *
     * @return true if all test assertions pass, false otherwise.
     */
    public boolean test2() {
        System.out.println("\nTest 2: Insert strings and test clear().");
        BinarySearchTree<String> tree = new BinarySearchTree<>();
        tree.insert("apple");
        tree.insert("banana");
        tree.insert("orange");
        tree.insert("grape");
        
        if (tree.size() != 4) {
            System.out.println("Test 2 FAILED: size() is incorrect. Expected: 4, Actual: " + tree.size());
            return false;
        }
        
        tree.clear();
        
        if (tree.size() != 0 || !tree.isEmpty()) {
            System.out.println("Test 2 FAILED: clear() or isEmpty() is incorrect.");
            return false;
        }
        
        System.out.println("Test 2 PASSED");
        return true;
    }

    /**
     * Tests insertion of duplicate values and verifies the resulting tree structure.
     *
     * @return true if all test assertions pass, false otherwise.
     */
    public boolean test3() {
        System.out.println("\nTest 3: Insert duplicate values and different orders.");
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.insert(20);
        tree.insert(10);
        tree.insert(30);
        tree.insert(10); 
        tree.insert(5);
        
        if (tree.size() != 5) {
            System.out.println("Test 3 FAILED: size() is incorrect. Expected: 5, Actual: " + tree.size());
            return false;
        }
        
        if (!tree.contains(10) || !tree.contains(5) || !tree.contains(30)) {
            System.out.println("Test 3 FAILED: contains() is incorrect.");
            return false;
        }
        
        try {
            if (tree.root.getData() != 20) return false;
            if (tree.root.getLeft().getData() != 10) return false;
            if (tree.root.getRight().getData() != 30) return false;
            if (tree.root.getLeft().getLeft().getData() != 10) return false;
            if (tree.root.getLeft().getLeft().getLeft().getData() != 5) return false;
        } catch (NullPointerException e) {
            System.out.println("Test 3 FAILED: Node connections are incorrect.");
            return false;
        }

        System.out.println("Test 3 PASSED");
        return true;
    }

    /**
     * The main method to run all test cases.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        BinarySearchTree<Integer> testTree = new BinarySearchTree<>();
        testTree.test1();
        testTree.test2();
        testTree.test3();
    }
}