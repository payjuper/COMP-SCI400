import java.util.List;
import java.util.NoSuchElementException;

/**
 * Frontend implementation that produces small HTML fragments
 * for shortest-path prompts/responses and "longest location list" prompts/responses.
 */
public class Frontend implements FrontendInterface {
    private final BackendInterface backend;

    // Implementing classes should support: public Frontend(BackendInterface backend)
    public Frontend(BackendInterface backend) {
        if (backend == null) throw new IllegalArgumentException("backend must not be null");
        this.backend = backend;
    }

    // Prompt fragment: start/end inputs and a button
    @Override
    public String generateShortestPathPromptHTML() {
        StringBuilder sb = new StringBuilder();
        sb.append("<section>\n");
        sb.append("  <h2>Find Shortest Path</h2>\n");
        sb.append("  <label for=\"start\">Start location:</label>\n");
        sb.append("  <input type=\"text\" id=\"start\" name=\"start\" />\n");
        sb.append("  <label for=\"end\">Destination:</label>\n");
        sb.append("  <input type=\"text\" id=\"end\" name=\"end\" />\n");
        sb.append("  <button>Find Shortest Path</button>\n");
        sb.append("</section>");
        return sb.toString();
    }

    // Response fragment: paragraph describing endpoints, ordered list of locations, and total time paragraph
    @Override
    public String generateShortestPathResponseHTML(String start, String end) {
        String s = start == null ? "" : start;
        String e = end == null ? "" : end;

        // Query backend
        List<String> path = backend.findLocationsOnShortestPath(s, e);
        List<Double> times = backend.findTimesOnShortestPath(s, e);

        // No path / empty path case
        if (path == null || path.isEmpty()) {
            return "<section><p>No path found between \"" + esc(s) + "\" and \"" + esc(e) + "\".</p></section>";
        }

        // Sum times (defensive for length mismatches)
        double total = 0.0;
        if (times != null) {
            for (Double d : times) {
                if (d != null) total += d.doubleValue();
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<section>\n");
        sb.append("  <p>Shortest path from <strong>").append(esc(s)).append("</strong> to <strong>").append(esc(e)).append("</strong>:</p>\n");
        sb.append(htmlOrderedList(path));
        sb.append("  <p>Total travel time: ").append(String.format("%.1f", total)).append(" seconds</p>\n");
        sb.append("</section>");
        return sb.toString();
    }

    // Prompt fragment: single "from" input and a button
    @Override
    public String generateLongestLocationListFromPromptHTML() {
        StringBuilder sb = new StringBuilder();
        sb.append("<section>\n");
        sb.append("  <h2>Longest Location List From</h2>\n");
        sb.append("  <label for=\"from\">Start location:</label>\n");
        sb.append("  <input type=\"text\" id=\"from\" name=\"from\" />\n");
        sb.append("  <button>Longest Location List From</button>\n");
        sb.append("</section>");
        return sb.toString();
    }

    // Response fragment: paragraph describing start/end (end is last in list), ordered list, and total count paragraph
    @Override
    public String generateLongestLocationListFromResponseHTML(String start) {
        String s = start == null ? "" : start;

        List<String> list;
        try {
            list = backend.getLongestLocationListFrom(s);
        } catch (NoSuchElementException ex) {
            return "<section><p>Could not find any destinations reachable from \"" + esc(s) + "\".</p></section>";
        }

        if (list == null || list.isEmpty()) {
            return "<section><p>No path could be found starting at \"" + esc(s) + "\".</p></section>";
        }

        String end = list.get(list.size()-1);

        StringBuilder sb = new StringBuilder();
        sb.append("<section>\n");
        sb.append("  <p>Longest list of locations along a shortest path from <strong>")
          .append(esc(s)).append("</strong> to <strong>").append(esc(end)).append("</strong>:</p>\n");
        sb.append(htmlOrderedList(list));
        sb.append("  <p>Total locations on path: ").append(list.size()).append("</p>\n");
        sb.append("</section>");
        return sb.toString();
    }

    // -------- helpers --------

    private static String esc(String s) {
        // minimal HTML escape
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;");
    }

    private static String htmlOrderedList(List<String> items) {
        StringBuilder sb = new StringBuilder();
        sb.append("  <ol>\n");
        for (String it : items) {
            sb.append("    <li>").append(esc(it == null ? "" : it)).append("</li>\n");
        }
        sb.append("  </ol>\n");
        return sb.toString();
    }
}
