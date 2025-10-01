import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * JUnit tests for RedBlackTree insertion and red-black property repairs.
 * At least 3 tests as required by assignment.
 */
public class RedBlackTreeTest {

    /**
     * Test that the root is always black after the first insertion.
     */
    @Test
    public void testRootIsBlackAfterFirstInsert() {
        RedBlackTree<Integer> tree = new RedBlackTree<>();
        tree.insert(10);
        assertTrue(((RedBlackNode<Integer>) tree.root).isBlackNode,
                "Root should be black after first insertion");
    }

    /**
     * Test a recoloring case where both parent and uncle are red.
     * This mirrors an example from the Q03.RBTInsert quiz.
     */
    @Test
    public void testRecoloringCase() {
        RedBlackTree<Integer> tree = new RedBlackTree<>();
        tree.insert(10);
        tree.insert(5);
        tree.insert(15); // parent=5, uncle=15, both red
        tree.insert(1);  // triggers recoloring
        assertTrue(((RedBlackNode<Integer>) tree.root).isBlackNode,
                "Root should remain black after recoloring");
    }

    /**
     * Test a rotation case (LL imbalance) where a single right rotation is needed.
     */
    @Test
    public void testRotationCaseLL() {
        RedBlackTree<Integer> tree = new RedBlackTree<>();
        tree.insert(10);
        tree.insert(5);
        tree.insert(1); // triggers right rotation at root
        assertEquals(5, tree.root.data,
                "After LL case, root should be 5");
    }
}
