import java.util.List;
import java.util.NoSuchElementException;

/**
 * Frontend Implementation for a webapp users can find the shortest
 * paths to locations on campus followed with other relevant data
 *
 */
public class Frontend implements FrontendInterface {

    //instance variable fields
    protected BackendInterface backend;
    /**
     * Class constructor that instantiates backend
     * Currently will use placeholder
     * @param backend - backend implementation which handles graph methods and computations
     */
    public Frontend(BackendInterface backend){
    this.backend = backend;
    }


    @Override
    /**
     * Returns an HTML fragment that can be embedded within the body of a
     * larger html page.  This HTML output should include:
     * - a text input field with the id="start", for the start location
     * - a text input field with the id="end", for the destination
     * - a button labelled "Find Shortest Path" to request this computation
     * Ensure that these text fields are clearly labelled, so that the user
     * can understand how to use them.
     * @return an HTML string that contains input controls that the user can
     *         make use of to request a shortest path computation
     */
    public String generateShortestPathPromptHTML() {
        // simple, labeled inputs
        String html = "";
        //start input
        html += ("<input type=\"text\" id=\"start\" name=\"start\" />\n");
        // end input
        html += ("<input type=\"text\" id=\"end\" name=\"end\" />\n");
        // shortest path button
        html += ("<button>Find Shortest Path</button>\n");
        return html;
    }

    @Override
    /**
     * Returns an HTML fragment that can be embedded within the body of a
     * larger html page.  This HTML output should include:
     * - a paragraph (p) that describes the path's start and end locations
     * - an ordered list (ol) of locations along that shortest path
     * - a paragraph (p) that includes the total travel time along this path
     * Or if there is no such path, the HTML returned should instead indicate
     * the kind of problem encountered.
     * @param start is the starting location to find a shortest path from
     * @param end is the destination that this shortest path should end at
     * @return an HTML string that describes the shortest path between these
     *         two locations
     */
    public String generateShortestPathResponseHTML(String start, String end) {
        // make sure input is valid
        if (start == null || start.trim().isEmpty()
                || end == null || end.trim().isEmpty()) {
            return "<p>Both a start location and a destination are required.</p>";
        }

        // private helper to make sure the HTML isn't broken when building the string
        String safeStart = escapeHtml(start.trim());
        String safeEnd   = escapeHtml(end.trim());

        //use try/catch incase any errors come about
        try {
            // get the locations on the path
            List<String> path = backend.findLocationsOnShortestPath(start, end);

            // if backend returns empty list, then there is no valid path
            if (path == null || path.isEmpty()) {
                return "<p>No path could be found from " + safeStart + " to " + safeEnd + ".</p>";
            }

            // get the times along that path (seconds between each pair)
            List<Double> times = backend.findTimesOnShortestPath(start, end);
            double totalSeconds = 0.0;
            if (times != null) {
                for (Double d : times) {
                    if (d != null) {
                        totalSeconds += d;
                    }
                }
            }

            // 3) build HTML string; we will use StringBuilder to keep code clean and easy to read
            StringBuilder html = new StringBuilder();
            html.append("<p>Shortest path from ")
                    .append(safeStart)
                    .append(" to ")
                    .append(safeEnd)
                    .append(":</p>\n");

            // create ordered list
            html.append("<ol>\n");
            for (String loc : path) {
                html.append("<li>")
                        .append(escapeHtml(loc)) /*again, make sure our strings are proper HTML*/
                        .append("</li>\n");
            }
            html.append("</ol>\n");

            html.append("<p>Total travel time: ")
                    .append(totalSeconds)
                    .append(" seconds</p>\n");

            return html.toString();

            // error handling if something has gone wrong
        } catch (NoSuchElementException e) {
            // backend says one or both locations don’t exist
            return "<p>Could not compute shortest path: "
                    + escapeHtml(e.getMessage() == null ? "location not found" : e.getMessage())
                    + "</p>";
        } catch (Exception e) {
            // generic fallback
            return "<p>There was a problem computing the shortest path: "
                    + escapeHtml(e.getMessage() == null ? "unknown error" : e.getMessage())
                    + "</p>";
        }
    }

    @Override
    /**
     * Returns an HTML fragment that can be embedded within the body of a
     * larger html page.  This HTML output should include:
     * - a text input field with the id="from", for the start location
     * - a button labelled "Longest Location List From" to submit this request
     * Ensure that this text field is clearly labelled, so that the user
     * can understand how to use it.
     * @return an HTML string that contains input controls that the user can
     *         make use of to request a longest location list calculation
     */
    public String generateLongestLocationListFromPromptHTML() {
        //simple, labeled outputs
        String html = "";
        //text field with id "from"
        html += ("<input type=\"text\" id=\"from\" name=\"from\" />\n");
        //button with appropriate label
        html += ("<button>Longest Location List From</button>\n");
        return html;
    }

    @Override
    /**
     * Returns an HTML fragment that can be embedded within the body of a
     * larger html page.  This HTML output should include:
     * - a paragraph (p) that describes the path's start and end locations
     * - an ordered list (ol) of locations along that shortest path
     * - a paragraph (p) that includes the total number of locations on path
     * Or if no such path can be found, the HTML returned should instead
     * indicate the kind of problem encountered.
     * @param start is the starting location to find the longest list from
     * @return an HTML string that describes the longest list of locations
     *        along a shortest path starting from the specified location
     */
    public String generateLongestLocationListFromResponseHTML(String start) {
        //valid our string before proceeding
        if (start == null || start.trim().isEmpty()) {
            return "<p>A starting location is required.</p>\n";
        }

        // use private helper to ensure proper HTML
        String safeStart = escapeHtml(start.trim());

        //use try/catch incase any errors come about
        try {
            List<String> locations = backend.getLongestLocationListFrom(start);
            //make sure out list is not empty or null
            if (locations == null || locations.isEmpty()) {
                return "<p>No locations could be found starting from " + safeStart + ".</p>";
            }

            // last location in this list is where that longest path ends
            String end = locations.getLast();
            //quick string check
            String safeEnd = escapeHtml(end);

            //build HTML, using StringBuilder for organization
            StringBuilder html = new StringBuilder();
            html.append("<p>Longest location list along a shortest path starting at ")
                    .append(safeStart)
                    .append(" and ending at ")
                    .append(safeEnd)
                    .append(":</p>\n");

            //create ordered list
            html.append("<ol>\n");
            for (String loc : locations) {
                html.append("<li>")
                        .append(escapeHtml(loc))
                        .append("</li>\n");
            }
            html.append("</ol>\n");

            //show total number of locations on given path
            html.append("<p>Total number of locations on this path: ")
                    .append(locations.size())
                    .append("</p>\n");

            return html.toString();

            //error handling should one come about
        } catch (NoSuchElementException e) {
            return "<p>Could not compute a longest location list from "
                    + safeStart
                    + ": "
                    + escapeHtml(e.getMessage() == null ? "location not found" : e.getMessage())
                    + "</p>";
            //generic catch
        } catch (Exception e) {
            return "<p>There was a problem computing the longest location list: "
                    + escapeHtml(e.getMessage() == null ? "unknown error" : e.getMessage())
                    + "</p>";
        }
    }
    /**
     * Private helper to make sure we don't break HTML when building strings.
     */
    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}

