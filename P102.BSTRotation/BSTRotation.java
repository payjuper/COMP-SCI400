/**
 * File name: BSTRotation.java
 * Author: Youngkyo Kim
 * Course: CS400 - Fall 2025
 * Description: This is a university assignment for CS400, 
 * providing a rotate method that performs left and right rotations 
 * on a binary search tree without creating new nodes.
 */

public class BSTRotation<T extends Comparable<T>> extends BinarySearchTree_Placeholder<T> {

    /**
     * Constructs an empty BSTRotation tree.
     * The root of the tree is initialized to null.
     */
    public BSTRotation() {
        super();
    }

    /**
     * Performs the rotation operation on the provided nodes within this tree.
     * - Right rotation when the child is the left child of the parent.
     * - Left rotation when the child is the right child of the parent.
     * Updates references between parent, child, and grandparent to 
     * maintain the tree structure.
     *
     * @param child  the node being rotated into the parent position
     * @param parent the node being rotated into the child position
     * @throws NullPointerException if either argument is null
     * @throws IllegalArgumentException if the provided nodes are not 
     *         in a valid parent-child relationship
     */
    protected void rotate(BinaryNode<T> child, BinaryNode<T> parent)
            throws NullPointerException, IllegalArgumentException {

        // check for null arguments
        if (child == null || parent == null) {
            throw new NullPointerException("child or parent is null");
        }

        // reference to the parent of the parent (grandparent)
        BinaryNode<T> grandParent = parent.getParent();

        // CASE 1: Right rotation (child is the left child of parent)
        if (parent.getLeft() == child) {
            // move child's right subtree into parent's left
            parent.setLeft(child.getRight());
            if (child.getRight() != null) {
                child.getRight().setParent(parent);
            }
            // link parent as right child of child
            child.setRight(parent);

        // CASE 2: Left rotation (child is the right child of parent)
        } else if (parent.getRight() == child) {
            // move child's left subtree into parent's right
            parent.setRight(child.getLeft());
            if (child.getLeft() != null) {
                child.getLeft().setParent(parent);
            }
            // link parent as left child of child
            child.setLeft(parent);

        // if not a valid parent-child relationship
        } else {
            throw new IllegalArgumentException("child is not a direct child of parent");
        }

        // update parent references
        child.setParent(grandParent);
        parent.setParent(child);

        // update root if parent was previously the root
        if (grandParent == null) {
            this.root = child;
        }
        // otherwise connect child to its grandparent
        else if (grandParent.getLeft() == parent) {
            grandParent.setLeft(child);
        } else {
            grandParent.setRight(child);
        }
    }

    // --- Testing Methods (Required by Assignment) ---

    /**
     * Test1: Performs a right rotation at the root node.
     * @return true if the structure after rotation is correct, false otherwise.
     */
    public boolean test1() {
        BSTRotation<Integer> tree = new BSTRotation<>();
        BinaryNode<Integer> p = new BinaryNode<>(10);
        BinaryNode<Integer> c = new BinaryNode<>(5);
        p.setLeft(c); c.setParent(p);
        tree.root = p;

        tree.rotate(c, p);
        return tree.root == c && c.getRight() == p && p.getParent() == c;
    }

    /**
     * Test2: Performs a left rotation at the root node.
     * @return true if the structure after rotation is correct, false otherwise.
     */
    public boolean test2() {
        BSTRotation<Integer> tree = new BSTRotation<>();
        BinaryNode<Integer> p = new BinaryNode<>(10);
        BinaryNode<Integer> c = new BinaryNode<>(15);
        p.setRight(c); c.setParent(p);
        tree.root = p;

        tree.rotate(c, p);
        return tree.root == c && c.getLeft() == p && p.getParent() == c;
    }

    /**
     * Test3: Performs a right rotation on a non-root subtree.
     * @return true if the structure after rotation is correct, false otherwise.
     */
    public boolean test3() {
        BSTRotation<Integer> tree = new BSTRotation<>();
        BinaryNode<Integer> grand = new BinaryNode<>(20);
        BinaryNode<Integer> p = new BinaryNode<>(10);
        BinaryNode<Integer> c = new BinaryNode<>(5);

        grand.setLeft(p); p.setParent(grand);
        p.setLeft(c); c.setParent(p);
        tree.root = grand;

        tree.rotate(c, p);
        return grand.getLeft() == c && c.getRight() == p && p.getParent() == c;
    }

    /**
     * Test4: Performs rotation when parent has an additional child.
     * @return true if the structure after rotation is correct, false otherwise.
     */
    public boolean test4() {
        BSTRotation<Integer> tree = new BSTRotation<>();
        BinaryNode<Integer> p = new BinaryNode<>(10);
        BinaryNode<Integer> c = new BinaryNode<>(5);
        BinaryNode<Integer> rightChild = new BinaryNode<>(12);

        p.setLeft(c); c.setParent(p);
        p.setRight(rightChild); rightChild.setParent(p);
        tree.root = p;

        tree.rotate(c, p);
        return tree.root == c && c.getRight() == p && p.getRight() == rightChild;
    }

    /**
     * Test5: Performs rotation when both parent and child have extra children.
     * @return true if the structure after rotation is correct, false otherwise.
     */
    public boolean test5() {
        BSTRotation<Integer> tree = new BSTRotation<>();
        BinaryNode<Integer> p = new BinaryNode<>(10);
        BinaryNode<Integer> c = new BinaryNode<>(5);
        BinaryNode<Integer> cRight = new BinaryNode<>(7);
        BinaryNode<Integer> pRight = new BinaryNode<>(15);

        p.setLeft(c); c.setParent(p);
        p.setRight(pRight); pRight.setParent(p);
        c.setRight(cRight); cRight.setParent(c);
        tree.root = p;

        tree.rotate(c, p);
        return tree.root == c && c.getRight() == p && p.getLeft() == cRight && p.getRight() == pRight;
    }

    /**
     * The main method to run all test cases for this assignment.
     * Prints the result of each test to the console.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        BSTRotation<Integer> tester = new BSTRotation<>();
        System.out.println("Test1 (Right rotation at root): " + tester.test1());
        System.out.println("Test2 (Left rotation at root): " + tester.test2());
        System.out.println("Test3 (Rotation in subtree): " + tester.test3());
        System.out.println("Test4 (Parent has another child): " + tester.test4());
        System.out.println("Test5 (Parent+Child have extra children): " + tester.test5());
    }
}
