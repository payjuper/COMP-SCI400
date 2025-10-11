import java.util.Iterator;
import java.util.Stack;
import java.util.NoSuchElementException;

/**
 * This class extends RedBlackTree into a tree that supports iterating over the values it
 * stores in sorted, ascending order.
 */
public class RBTreeIterable<T extends Comparable<T>>
        extends RedBlackTree<T> implements IterableSortedCollection<T> {

    // Stores the global lower and upper bounds for iterators created from this tree.
    // Only these two fields are allowed per the assignment requirements.
    private Comparable<T> iteratorMin = null;
    private Comparable<T> iteratorMax = null;

    /**
     * Allows setting the start (minimum) value of the iterator. When this method is called,
     * every iterator created after it will use the minimum set by this method until this method
     * is called again to set a new minimum value.
     *
     * @param min the minimum for iterators created for this tree, or null for no minimum
     */
    @Override
    public void setIteratorMin(Comparable<T> min) {
        this.iteratorMin = min;
    }

    /**
     * Allows setting the stop (maximum) value of the iterator. When this method is called,
     * every iterator created after it will use the maximum set by this method until this method
     * is called again to set a new maximum value.
     *
     * @param max the maximum for iterators created for this tree, or null for no maximum
     */
    @Override
    public void setIteratorMax(Comparable<T> max) {
        this.iteratorMax = max;
    }

    /**
     * Returns an iterator over the values stored in this tree. The iterator uses the
     * start (minimum) value set by a previous call to setIteratorMin, and the stop (maximum)
     * value set by a previous call to setIteratorMax. If setIteratorMin has not been called
     * before, or if it was called with a null argument, the iterator uses no minimum value
     * and starts with the lowest value that exists in the tree. If setIteratorMax has not been
     * called before, or if it was called with a null argument, the iterator uses no maximum
     * value and finishes with the highest value that exists in the tree.
     */
    @Override
    public Iterator<T> iterator() {
        return new TreeIterator<T>(this.root, iteratorMin, iteratorMax);
    }

    /**
     * Nested class for Iterator objects created for this tree and returned by the iterator method.
     * This iterator follows an in-order traversal of the tree and returns the values in sorted,
     * ascending order.
     */
    protected static class TreeIterator<R extends Comparable<R>> implements Iterator<R> {

        // stores the start point (minimum) for the iterator
        Comparable<R> min = null;
        // stores the stop point (maximum) for the iterator
        Comparable<R> max = null;
        // stores the stack that keeps track of the inorder traversal
        Stack<BinaryNode<R>> stack = null;

        /**
         * Constructor for a new iterator if the tree with root as its root node, and
         * min as the start (minimum) value (or null if no start value) and max as the
         * stop (maximum) value (or null if no stop value) of the new iterator.<br/>
         * Time complexity should be <b>O(log n)</b>
         *
         * @param root root node of the tree to traverse
         * @param min  the minimum value that the iterator will return
         * @param max  the maximum value that the iterator will return
         */
        public TreeIterator(BinaryNode<R> root, Comparable<R> min, Comparable<R> max) {
            this.min = min;
            this.max = max;
            this.stack = new Stack<>();
            // Initialize the stack so that the next() call returns the smallest
            // value >= min (or the overall smallest if min is null).
            updateStack(root);
        }

        /**
         * Helper method for initializing and updating the stack. This method both<br/>
         * - finds the next data value stored in the tree (or subtree) that is between
         * start(minimum) and stop(maximum) point (including start and stop points
         * themselves), and<br/>
         * - builds up the stack of ancestor nodes that contain values between
         * start(minimum) and stop(maximum) values (including start and stop values
         * themselves) so that those nodes can be visited in the future.
         *
         * @param node the root node of the subtree to process
         */
        private void updateStack(BinaryNode<R> node) {
            // Descend the tree while respecting min/max bounds.
            // When a node is within [min, max], push it and go left to find smaller candidates.
            while (node != null) {
                if (min != null && min.compareTo(node.data) > 0) {
                    // node.data < min: everything in the left subtree is < node.data < min,
                    // so skip left and go right.
                    node = node.right;
                } else if (max != null && max.compareTo(node.data) < 0) {
                    // node.data > max: everything in the right subtree is > node.data > max,
                    // so skip right and go left.
                    node = node.left;
                } else {
                    // node.data is within [min, max] (or min/max is not set):
                    // push this node as a candidate and continue left.
                    stack.push(node);
                    node = node.left;
                }
            }
        }

        /**
         * Returns true if the iterator has another value to return, and false otherwise.
         */
        @Override
        public boolean hasNext() {
            return stack != null && !stack.isEmpty();
        }

        /**
         * Returns the next value of the iterator.<br/>
         * Amortized time complexity should be <b>O(1)</b><br/>
         * Worst case time complexity <b>O(log n)</b><br/>
         * <p><b>Do not</b> implement this method by linearly walking through the
         * entire tree from the smallest element until the start bound is reached.
         * That process should occur <b>only once</b> during construction of the
         * iterator object.</p>
         *
         * @throws NoSuchElementException if the iterator has no more values to return
         */
        @Override
        public R next() {
            if (!hasNext())
                throw new NoSuchElementException();

            // The top of the stack is the next in-order node within bounds.
            BinaryNode<R> node = stack.pop();
            R value = node.data;

            // Prepare future nodes from the right subtree.
            updateStack(node.right);

            // Safety: ensure we do not return a value beyond the configured maximum.
            if (max != null && max.compareTo(value) < 0)
                throw new NoSuchElementException();

            return value;
        }
    }

}
