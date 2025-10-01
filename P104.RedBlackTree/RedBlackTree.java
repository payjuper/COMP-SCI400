/**
 * Red-Black Tree implementation for CS400 P104.
 * Extends BSTRotation (from P102), which itself extends BinarySearchTree (from P101).
 * Only insertion (no deletion) is required for this assignment.
 */
public class RedBlackTree<T extends Comparable<T>> extends BSTRotation<T> {

    public RedBlackTree() {
        super();
    }

    /**
     * Repairs red-red violations after inserting a new red node.
     * Standard cases handled:
     *   - Case 1: Parent is black → no violation.
     *   - Case 2: Parent and uncle are red → recolor and move up to grandparent.
     *   - Case 3/4: Parent is red, uncle is black (or null) → perform rotation(s) and recolor.
     *
     * @param newNode the newly inserted red node, or a node recolored to red during fix-up
     */
    @SuppressWarnings("unchecked")
    protected void ensureRedProperty(RedBlackNode<T> newNode) {
        if (newNode == null) return;

        while (newNode != this.root) {
            RedBlackNode<T> parent = newNode.getParent();
            if (parent == null) break;

            if (parent.isBlackNode) break; // parent is black → no violation

            RedBlackNode<T> grand = parent.getParent();
            if (grand == null) break;

            // determine uncle
            RedBlackNode<T> uncle = (grand.getLeft() == parent)
                    ? grand.getRight()
                    : grand.getLeft();

            boolean uncleIsRed = (uncle != null) && (!uncle.isBlackNode);

            if (uncleIsRed) {
                // Case 2: recolor and continue upward
                parent.isBlackNode = true;
                uncle.isBlackNode = true;
                grand.isBlackNode = false;
                newNode = grand;
            } else {
                boolean parentIsLeft = (grand.getLeft() == parent);
                if (parentIsLeft) {
                    if (parent.getRight() == newNode) {
                        // Left-Right case → rotate parent and newNode
                        rotate(newNode, parent);
                        newNode = parent;
                        parent = newNode.getParent();
                    }
                    // Left-Left case → rotate parent and grand
                    rotate(parent, grand);
                    parent.isBlackNode = true;
                    grand.isBlackNode = false;
                } else {
                    if (parent.getLeft() == newNode) {
                        // Right-Left case → rotate parent and newNode
                        rotate(newNode, parent);
                        newNode = parent;
                        parent = newNode.getParent();
                    }
                    // Right-Right case → rotate parent and grand
                    rotate(parent, grand);
                    parent.isBlackNode = true;
                    grand.isBlackNode = false;
                }
                break; // violation fixed
            }
        }
    }

    /**
     * Inserts a new value into the RedBlackTree and ensures
     * the red-black tree properties are maintained.
     *
     * @param value the value to insert
     * @throws NullPointerException if value is null
     */
    @Override
    @SuppressWarnings("unchecked")
    public void insert(T value) {
        if (value == null) {
            throw new NullPointerException("Cannot insert null value into RedBlackTree.");
        }

        RedBlackNode<T> newNode = new RedBlackNode<>(value);
        newNode.isBlackNode = false; // new nodes are red by default

        super.insertHelper(newNode);

        if (newNode != this.root) {
            ensureRedProperty(newNode);
        }

        // root must always be black
        ((RedBlackNode<T>) this.root).isBlackNode = true;
    }
}
