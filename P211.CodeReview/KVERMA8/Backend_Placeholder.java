import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Arrays;

/**
 * This is a placeholder for the fully working Backend that will be developed
 * by one of your teammates this week and then integrated with your role code
 * in a future week.  It is designed to help develop and test the functionality
 * of your own Frontend role code this week.  Note the limitations described
 * below.
 */
public class Backend_Placeholder implements BackendInterface {

  // Oct 29 Correction (Frontend): maintain a local list of locations
  List<String> LocationsInGraph = new ArrayList<>(
      Arrays.asList("Union South", "Computer Sciences and Statistics",
                    "Weeks Hall for Geological Sciences"));

  // Presumably this placeholder is using a placeholder graph that is itself
  // not fully functional.
  GraphADT<String,Double> graph;
  public Backend_Placeholder(GraphADT<String,Double> graph) {
    this.graph = graph; 
  }

  // this method adds a single extra location to the collection when called
  public void loadGraphData(String filename) throws IOException {
    // original placeholder added to the graph:
    // graph.insertNode("Mosse Humanities Building");
    // per the correction, append to LocationsInGraph:
    LocationsInGraph.add("Mosse Humanities Building");
  }

  public List<String> getListOfAllLocations() {
    // per the correction, return the local list
    return LocationsInGraph;
  }

  public List<String> findLocationsOnShortestPath(String startLocation, String endLocation) {
    // leave placeholder path behavior as-is
    return graph.shortestPathData(startLocation,endLocation);
  }

  // returns list of increasing values
  public List<Double> findTimesOnShortestPath(String startLocation, String endLocation) {
    List<String> locations = graph.shortestPathData(startLocation,endLocation);
    List<Double> times = new ArrayList<>();
    for(int i=0;i<locations.size();i++) times.add(i+1.0);
    return times;
  }

  // always returns the locations leading to the last node
  public List<String> getLongestLocationListFrom(String startLocation) throws NoSuchElementException {
    // per the correction, use LocationsInGraph instead of graph.getAllNodes()
    List<String> all = LocationsInGraph;
    String lastLocation = all.get(all.size()-1);
    return graph.shortestPathData(startLocation,lastLocation);
  }

}
