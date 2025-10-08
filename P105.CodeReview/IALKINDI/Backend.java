import java.util.List;
import java.util.ArrayList;

/**
 * Backend for Project 1: iSongly
 * 
 * Stores and retrieves Song objects using the tree that gets passed into the
 * constructor. Supports loading data from CSV, setting an energy range, 
 * applying a danceability filter, and getting the five most recent songs.
 */
public class Backend implements BackendInterface {

    // The tree structure used to store all songs (placeholder for now).
    private final IterableSortedCollection<Song> songTree;

    // Remember the most recently requested energy range (null = no bound).
    private Integer minEnergy = null;
    private Integer maxEnergy = null;

    // Remember the most recently set danceability filter (null = no filter).
    private Integer minDanceability = null;

    /**
     * Constructor required by the interface. The provided tree will be used 
     * to store songs, keep them in order, and support iteration.
     * 
     * @param tree the data structure where songs are kept
     */
    public Backend(IterableSortedCollection<Song> tree) {
        this.songTree = tree;
    }

    /**
     * Read songs from a CSV file and add them to the tree.
     * The CSV has headers like title, artist, energy, danceability, etc.
     * After reading each line, make a Song object and insert it into the tree.
     * 
     * @param filename the name of the CSV file to load
     */
    @Override
    public void readData(String filename) throws java.io.IOException {
        // TODO: open the CSV file
        // TODO: parse headers dynamically (don’t assume order)
        // TODO: create a Song object for each row
        // TODO: insert each Song into songTree
    }

    /**
     * Set the energy range [low..high] that future results should respect,
     * and immediately return all songs in that range (also filtered if a 
     * danceability threshold was set earlier).
     * 
     * Passing null for low or high means "no bound" on that end.
     * 
     * @param low  minimum energy, or null for no minimum
     * @param high maximum energy, or null for no maximum
     * @return a list of song titles that match this range and filter
     */
    @Override
    public List<String> getAndSetRange(Integer low, Integer high) {
        // Save the range for future calls
        this.minEnergy = low;
        this.maxEnergy = high;

        // TODO: walk through the tree in energy order
        // TODO: collect songs that fall in [minEnergy, maxEnergy]
        // TODO: also apply danceability filter if minDanceability != null
        return new ArrayList<>(); // placeholder
    }

    /**
     * Apply a danceability filter to future results. 
     * Passing null clears the filter.
     * 
     * @param threshold only songs with danceability > threshold will be returned
     * @return list of titles that pass both the current range and this filter
     */
    @Override
    public List<String> applyAndSetFilter(Integer threshold) {
        this.minDanceability = threshold;

        // TODO: iterate through the tree
        // TODO: apply both the energy range (if set) and danceability filter
        return new ArrayList<>(); // placeholder
    }

    /**
     * Return up to five of the most recent songs that match both:
     *   - the current energy range, and
     *   - the current danceability filter (if one is set).
     * 
     * If there are fewer than five matches, return them all.
     * If none, return an empty list.
     * 
     * @return list of up to five most recent song titles
     */
    @Override
    public List<String> fiveMost() {
        // TODO: filter songs by current range and danceability
        // TODO: pick up to the 5 most recent ones
        return new ArrayList<>(); // placeholder
    }

    // Helper idea (optional):
    // private boolean passesFilters(Song s) { ... }
}
