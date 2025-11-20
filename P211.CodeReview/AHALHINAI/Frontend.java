import java.util.List;
import java.util.NoSuchElementException;

/**
 * This Frontend class implements the FrontendInterface and
 * provides HTML snippets that interact with the Backend.
 * Each method generates part of a web page that allows users
 * to find shortest paths or longest location lists between nodes.
 */
public class Frontend implements FrontendInterface {

    // Reference to the backend, which performs the path computations
    private BackendInterface backend;

    /**
     * Constructor that accepts a backend object. The backend
     * provides the data and computations that this frontend
     * will display through generated HTML.
     * @param backend the backend used for shortest path computations
     */
    public Frontend(BackendInterface backend) {
        this.backend = backend;
    }

    /**
     * Generates an HTML form where the user can input
     * start and end locations and click a button to find
     * the shortest path between them.
     * @return HTML string for the shortest path prompt
     */
    @Override
    public String generateShortestPathPromptHTML() {
        StringBuilder sb = new StringBuilder();
        sb.append("<div>\n");
        sb.append("  <label for=\"start\">Start Location:</label>\n");
        sb.append("  <input type=\"text\" id=\"start\" name=\"start\" />\n");
        sb.append("  <label for=\"end\">Destination:</label>\n");
        sb.append("  <input type=\"text\" id=\"end\" name=\"end\" />\n");
        sb.append("  <button>Find Shortest Path</button>\n");
        sb.append("</div>\n");
        return sb.toString();
    }

    /**
     * Uses the backend to get the list of locations and times
     * along the shortest path between the two specified locations.
     * Returns an HTML fragment describing that path and its total time.
     * If no path exists, a paragraph is returned describing the issue.
     *
     * @param start the start location
     * @param end the destination
     * @return HTML string describing the shortest path results
     */
    @Override
    public String generateShortestPathResponseHTML(String start, String end) {
        // Request path data from backend
        List<String> locations = backend.findLocationsOnShortestPath(start, end);
        List<Double> times = backend.findTimesOnShortestPath(start, end);

        // If no path is available, return an error paragraph
        if (locations == null || locations.isEmpty()) {
            return "<p>No path could be found from " + start + " to " + end + ".</p>";
        }

        // Compute total travel time (sum of the seconds between nodes)
        double totalSeconds = 0.0;
        if (times != null) {
            for (Double d : times) {
                if (d != null) totalSeconds += d;
            }
        }

        // Build the HTML response
        StringBuilder sb = new StringBuilder();
        sb.append("<div>\n");
        sb.append("  <p>Shortest path from ").append(start).append(" to ").append(end).append(":</p>\n");
        sb.append("  <ol>\n");
        for (String loc : locations) {
            sb.append("    <li>").append(loc).append("</li>\n");
        }
        sb.append("  </ol>\n");
        sb.append("  <p>Total travel time: ").append(totalSeconds).append(" seconds</p>\n");
        sb.append("</div>\n");
        return sb.toString();
    }

    /**
     * Generates HTML for the prompt that allows the user to input
     * a starting location and request the "Longest Location List From" result.
     * @return HTML string for that user input prompt
     */
    @Override
    public String generateLongestLocationListFromPromptHTML() {
        StringBuilder sb = new StringBuilder();
        sb.append("<div>\n");
        sb.append("  <label for=\"from\">Start Location:</label>\n");
        sb.append("  <input type=\"text\" id=\"from\" name=\"from\" />\n");
        sb.append("  <button>Longest Location List From</button>\n");
        sb.append("</div>\n");
        return sb.toString();
    }

    /**
     * Uses the backend to retrieve the longest list of locations
     * along any shortest path that starts at the specified location.
     * Returns an HTML fragment with that list and count.
     * If no such path can be found, returns an explanatory paragraph.
     *
     * @param start the starting location
     * @return HTML string describing the longest list of locations
     */
    @Override
    public String generateLongestLocationListFromResponseHTML(String start) {
        List<String> locations;
        try {
            // ask backend for data
            locations = backend.getLongestLocationListFrom(start);
        } catch (NoSuchElementException e) {
            // backend throws if location missing or no path reachable
            return "<p>Could not find any locations reachable from " + start + ".</p>";
        }

        if (locations == null || locations.isEmpty()) {
            return "<p>No locations were found starting from " + start + ".</p>";
        }

        // Get the last location on this list
        String end = locations.get(locations.size() - 1);

        // Build the HTML output
        StringBuilder sb = new StringBuilder();
        sb.append("<div>\n");
        sb.append("  <p>Longest location list along shortest paths starting from ")
          .append(start)
          .append(" and ending at ")
          .append(end)
          .append(":</p>\n");
        sb.append("  <ol>\n");
        for (String loc : locations) {
            sb.append("    <li>").append(loc).append("</li>\n");
        }
        sb.append("  </ol>\n");
        sb.append("  <p>Total locations on path: ").append(locations.size()).append("</p>\n");
        sb.append("</div>\n");
        return sb.toString();
    }
}
