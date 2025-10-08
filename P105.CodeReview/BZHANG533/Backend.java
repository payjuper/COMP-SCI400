import java.util.*;
import java.io.*;

/**
 * Backend - CS400 Project 1: iSongly
 * Implements BackendInterface using an IterableSortedCollection<Song>.
 * - All Songs are inserted with an energy->title Comparator, ensuring the "natural order" is energy first
 * - Uses tree.setIteratorMin/Max() to set the energy range (with optional unbounded ends)
 * - Supports optional danceability threshold filtering
 * - fiveMost() returns up to 5 most recent songs (by year, descending) within the current range+filter
 */
public class Backend implements BackendInterface {

    private final IterableSortedCollection<Song> tree;

    // Persistent state across multiple calls
    private Integer rangeLow = null;              // Minimum energy (inclusive), null = no lower bound
    private Integer rangeHigh = null;             // Maximum energy (inclusive), null = no upper bound
    private Integer danceabilityThreshold = null; // null = no danceability filter

    // Unified sorting: energy first, then title (case-insensitive)
    private static final Comparator<Song> BY_ENERGY_THEN_TITLE =
            Comparator.comparingInt(Song::getEnergy)
                      .thenComparing(Song::getTitle, String.CASE_INSENSITIVE_ORDER);

    /**
     * REQUIRED: constructor signature must match interface specification
     */
    public Backend(IterableSortedCollection<Song> tree) {
        this.tree = tree;
    }

    // CSV Loading
    @Override
    public void readData(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String header = br.readLine();
            if (header == null) return;
    
            String[] cols = splitCSV(header);
            Map<String,Integer> idx = new HashMap<>();
            for (int i = 0; i < cols.length; i++) {
                idx.put(cols[i].trim().toLowerCase(), i);
            }
    
            // Match CSV columns by name: energy=nrgy, danceability=dnce, loudness=dB ("db" lowercased)
            String keyTitle = firstPresent(idx, "title", "track_name", "name");
            String keyArtist = firstPresent(idx, "artist", "artists", "artist_name");
            String keyGenre  = firstPresent(idx, "top genre", "genre", "genres", "top_genre");
            String keyYear   = firstPresent(idx, "year", "release_year");
            String keyBPM    = firstPresent(idx, "bpm");
            String keyEnergy = firstPresent(idx, "nrgy", "energy");         // ★
            String keyDance  = firstPresent(idx, "dnce", "danceability");   // ★
            String keyLoud   = firstPresent(idx, "db", "loudness");         // ★ "dB" -> "db" after lowercasing
            String keyLive   = firstPresent(idx, "live", "liveness");       // CSV uses "live"
    
            if (keyTitle == null || keyArtist == null || keyGenre == null ||
                keyYear  == null || keyBPM   == null || keyEnergy == null ||
                keyDance == null || keyLoud  == null || keyLive   == null) {
                throw new IOException("CSV missing required columns for Song fields.");
            }
    
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) continue;
                String[] parts = splitCSV(line);
    
                String title  = get(parts, idx.get(keyTitle));
                String artist = get(parts, idx.get(keyArtist));
                String genre  = get(parts, idx.get(keyGenre));
                Integer year  = parseInt(get(parts, idx.get(keyYear)));
                Integer bpm   = parseInt(get(parts, idx.get(keyBPM)));
                Integer energy = parseInt(get(parts, idx.get(keyEnergy)));
                Integer dance  = parseInt(get(parts, idx.get(keyDance)));
                Integer loud   = parseInt(get(parts, idx.get(keyLoud)));
                Integer live   = parseInt(get(parts, idx.get(keyLive)));
    
                if (title == null || artist == null || genre == null ||
                    year == null || bpm == null || energy == null ||
                    dance == null || loud == null || live == null) continue;
    
                Song s = new Song(title, artist, genre,
                                  year, bpm, energy, dance, loud, live,
                                  BY_ENERGY_THEN_TITLE);
                tree.insert(s);
            }
        }
    }
    

    // Query & Filtering 

    @Override
    public List<String> getAndSetRange(Integer low, Integer high) {
        this.rangeLow = low;
        this.rangeHigh = high;
        return collectTitlesRespectingState();
    }

    @Override
    public List<String> applyAndSetFilter(Integer threshold) {
        this.danceabilityThreshold = threshold; // null = clear filter
        return collectTitlesRespectingState();
    }

    @Override
    public List<String> fiveMost() {
        // Collect songs based on current range + filter, then sort by year descending, return top 5
        List<Song> passing = collectSongsRespectingState();
        passing.sort((a, b) -> Integer.compare(b.getYear(), a.getYear()));
        List<String> out = new ArrayList<>(Math.min(5, passing.size()));
        for (int i = 0; i < Math.min(5, passing.size()); i++) {
            out.add(passing.get(i).getTitle());
        }
        return out;
    }

    // Internal Helpers

    private List<String> collectTitlesRespectingState() {
        List<String> titles = new ArrayList<>();
        for (Song s : collectSongsRespectingState()) titles.add(s.getTitle());
        return titles;
    }

    /**
     * Applies energy range (via setIteratorMin/Max) and danceability filter to collect songs.
     * Note: Placeholder tree's iterator order is not guaranteed to be by energy,
     * so we explicitly sort by energy ascending (then title) here to meet specification.
     */
    private List<Song> collectSongsRespectingState() {
        // Apply energy boundaries (null means no bound on that side)
        if (rangeLow == null) {
            tree.setIteratorMin(null);
        } else {
            tree.setIteratorMin(boundarySong(rangeLow));
        }
        if (rangeHigh == null) {
            tree.setIteratorMax(null);
        } else {
            tree.setIteratorMax(boundarySong(rangeHigh));
        }

        List<Song> acc = new ArrayList<>();
        for (Song s : tree) {
            if (passesDanceability(s)) acc.add(s);
        }
        // Explicitly sort by energy (then title) to guarantee correct order
        acc.sort(BY_ENERGY_THEN_TITLE);
        return acc;
    }

    private boolean passesDanceability(Song s) {
        if (danceabilityThreshold == null) return true;
        return s.getDanceability() > danceabilityThreshold;
    }

    /** Constructs a "boundary Song" used only for energy comparisons */
    private Song boundarySong(int energy) {
        // Other fields can be dummy values; Comparator must be consistent
        return new Song(
            "BOUNDARY", "N/A", "N/A",
            0, 0, energy, 0, 0, 0,
            BY_ENERGY_THEN_TITLE
        );
    }

    // CSV Utility Helpers

    /** Simple CSV split (supports quoted commas) */
    private static String[] splitCSV(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                out.add(cur.toString().trim());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        out.add(cur.toString().trim());
        return out.toArray(new String[0]);
    }

    private static String firstPresent(Map<String,Integer> idx, String... keys) {
        for (String k : keys) if (idx.containsKey(k)) return k;
        return null;
    }

    private static String get(String[] parts, Integer i) {
        if (i == null || i < 0 || i >= parts.length) return null;
        String v = parts[i];
        if (v == null) return null;
        v = v.trim();
        return v.isEmpty() ? null : v;
    }

    private static Integer parseInt(String s) {
        if (s == null) return null;
        try {
            return Integer.valueOf(s.replaceAll("[^0-9-]", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
