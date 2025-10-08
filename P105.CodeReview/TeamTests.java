import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.*;

/**
 * CS400 Project 1: iSongly — P105 Code Review
 * --------------------------------------------
 * Author: Youngkyo Kim (NetID: ykim938)
 *
 * This class contains three JUnit 5 test cases that evaluate the functionality and
 * stability of my teammates’ Backend implementations. Each test only uses the public
 * methods defined in BackendInterface — no internal implementation details are accessed.
 *
 * These tests ensure that each teammate’s Backend behaves correctly for basic operations:
 * - reading data safely,
 * - handling null inputs,
 * - and returning valid results within constraints.
 */
public class TeamTests {

    /**
     * A minimal dummy tree implementation used to compile and run Backends during testing.
     * This class satisfies the IterableSortedCollection interface but only stores items
     * in a simple ArrayList. The iterator bounds (min and max) are recorded but not enforced,
     * since these tests focus on Backend correctness, not tree structure.
     */
    static class DummyTree implements IterableSortedCollection<Song> {
        private List<Song> list = new ArrayList<>();
        private Comparable<Song> minBound = null;
        private Comparable<Song> maxBound = null;

        @Override public void insert(Song data) { list.add(data); }

        // The real interface expects a Comparable<Song> parameter.
        // We manually compare against all stored songs to simulate "contains" behavior.
        @Override public boolean contains(Comparable<Song> data) { 
            for (Song s : list) {
                if (data.compareTo(s) == 0) return true;
            }
            return false;
        }

        @Override public int size() { return list.size(); }
        @Override public boolean isEmpty() { return list.isEmpty(); }
        @Override public void clear() { list.clear(); }

        @Override public Iterator<Song> iterator() { return list.iterator(); }
        @Override public void setIteratorMin(Comparable<Song> min) { this.minBound = min; }
        @Override public void setIteratorMax(Comparable<Song> max) { this.maxBound = max; }
    }

    /**
     * Test 1 — Verifies that readData() runs safely.
     * This test checks that the Backend can handle missing or invalid CSV files gracefully
     * without crashing the program or throwing unexpected exceptions.
     */
    @Test
    public void testReadDataRunsSafely() {
        BackendInterface backend = new Backend(new DummyTree());
        try {
            backend.readData("nonexistent.csv");
        } catch (IOException e) {
            // IOException is acceptable (expected behavior)
            assertTrue(true);
            return;
        } catch (Exception e) {
            fail("Unexpected exception type: " + e);
        }
        assertTrue(true);
    }

    /**
     * Test 2 — Ensures that getAndSetRange() can safely handle null boundaries.
     * Passing null for both low and high values should not cause a crash or exception.
     */
    @Test
    public void testSetRangeHandlesNulls() {
        BackendInterface backend = new Backend(new DummyTree());
        try {
            List<String> result = backend.getAndSetRange(null, null);
            assertNotNull(result, "Returned list should not be null.");
        } catch (Exception e) {
            fail("getAndSetRange() threw an exception with null bounds: " + e);
        }
    }

    /**
     * Test 3 — Ensures that fiveMost() never returns more than five results.
     * The Backend should correctly limit output even if more than five songs
     * are available in the dataset.
     */
    @Test
    public void testFiveMostReturnsAtMostFive() {
        DummyTree tree = new DummyTree();
        BackendInterface backend = new Backend(tree);

        // Insert 10 dummy songs for testing
        for (int i = 0; i < 10; i++) {
            tree.insert(new Song(
                "Song" + i, "Artist" + i, "Genre", 2000 + i, 100, 50 + i, 60, -5, 20,
                Comparator.comparingInt(Song::getEnergy)
            ));
        }

        List<String> result = backend.fiveMost();
        assertTrue(result.size() <= 5, "fiveMost() returned more than 5 songs.");
    }
}
