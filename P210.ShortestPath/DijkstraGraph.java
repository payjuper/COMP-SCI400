import java.util.PriorityQueue;
import java.util.List;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import java.util.Arrays;
import java.util.Stack;

/**
 * This class extends the BaseGraph data structure with additional methods for
 * computing the total cost and list of node data along the shortest path
 * connecting a provided starting to ending nodes. This class makes use of
 * Dijkstra's shortest path algorithm.
 */
public class DijkstraGraph<NodeType, EdgeType extends Number>
        extends BaseGraph<NodeType, EdgeType>
        implements GraphADT<NodeType, EdgeType> {

    /**
     * While searching for the shortest path between two nodes, a SearchNode
     * contains data about one specific path between the start node and another
     * node in the graph. The final node in this path is stored in its node
     * field. The total cost of this path is stored in its cost field. And the
     * predecessor SearchNode within this path is referenced by the predecessor
     * field (this field is null within the SearchNode containing the starting
     * node in its node field).
     *
     * SearchNodes are Comparable and are sorted by cost so that the lowest cost
     * SearchNode has the highest priority within a java.util.PriorityQueue.
     */
    protected class SearchNode implements Comparable<SearchNode> {
        public Node node;
        public double cost;
        public SearchNode pred;

        public SearchNode(Node startNode) {
            this.node = startNode;
            this.cost = 0;
            this.pred = null;
        }

        public SearchNode(SearchNode pred, Edge newEdge) {
            this.node = newEdge.succ;
            this.cost = pred.cost + newEdge.data.doubleValue();
            this.pred = pred;
        }

        public int compareTo(SearchNode other) {
            if (cost > other.cost)
                return +1;
            if (cost < other.cost)
                return -1;
            return 0;
        }
    }

    /**
     * Constructor that sets the map that the graph uses.
     */
    public DijkstraGraph() {
        super(new PlaceholderMap<>());
    }

    /**
     * This helper method creates a network of SearchNodes while computing the
     * shortest path between the provided start and end locations. The
     * SearchNode that is returned by this method represents the end of the
     * shortest path that is found: it's cost is the cost of that shortest path,
     * and the nodes linked together through predecessor references represent
     * all of the nodes along that shortest path (ordered from end to start).
     *
     * @param start the starting node for the path
     * @param end   the destination node for the path
     * @return SearchNode for the final end node within the shortest path
     * @throws NoSuchElementException when no path from start to end is found
     *                                or when either start or end data do not
     *                                correspond to a graph node
     */
    protected SearchNode computeShortestPath(Node start, Node end) {
        // Create priority queu to explore cheapest paths first
        PriorityQueue<SearchNode> queue = new PriorityQueue<>();

        // Track visited nodes using PlaceholderMap as a set
        PlaceholderMap<Node, Node> visited = new PlaceholderMap<>();

        // Create start node and add to queue
        SearchNode startNode = new SearchNode(start);
        queue.add(startNode);
        visited.put(start, start);

        // DIjkstra's algorithm main loop
        while(!queue.isEmpty()) {
            //Get the cheapest path from queue
            SearchNode current = queue.poll();

            // Check if we reached the destination
            if(current.node == end) {
                return current;
            }

            //Explore all neighbors through outgoing edges
            for (Edge edge : current.node.edgesLeaving) {
                Node neighbor = edge.succ;

                //Skip if neighbor already visited
                if (visited.containsKey(neighbor)){
                    continue;
                }

                // Create new SearchNode for this path to neighbor
                SearchNode neighborNode = new SearchNode(current, edge);

                //Add to priority queue and mark as visited
                queue.add(neighborNode);
                visited.put(neighbor, neighbor);
                

            }
        }

        // No path found
        throw new NoSuchElementException("No path from " + start.data + " to " + end.data);


    }

    /**
     * Returns the list of data values from nodes along the shortest path
     * from the node with the provided start value through the node with the
     * provided end value. This list of data values starts with the start
     * value, ends with the end value, and contains intermediary values in the
     * order they are encountered while traversing this shortest path. This
     * method uses Dijkstra's shortest path algorithm to find this solution.
     *
     * @param start the data item in the starting node for the path
     * @param end   the data item in the destination node for the path
     * @return list of data item from nodes along this shortest path
     */
    public List<NodeType> shortestPathData(NodeType start, NodeType end) {
        // Get start and end nodes from the graph
        Node startNode = nodes.get(start);
        Node endNode = nodes.get(end);

        //Compute Shortest Path using the computeShortestPath method of Dijkstra's algorithm
        SearchNode result = computeShortestPath(startNode, endNode);

        //Reconstruct path from SearchNode Chain
        List<NodeType> path = new ArrayList<>();
        Stack<NodeType> stack = new Stack<>();

        SearchNode current = result;
        while (current != null) {
            stack.push(current.node.data);
            current = current.pred;
        }

        while (!stack.isEmpty()) {
            path.add(stack.pop());
        }

        return path;
    }

    /**
     * Returns the cost of the path (sum over edge weights) of the shortest
     * path from the node containing the start data to the node containing the
     * end data. This method uses Dijkstra's shortest path algorithm to find
     * this solution.
     *
     * @param start the data item in the starting node for the path
     * @param end   the data item in the destination node for the path
     * @throws NoSuchElementException if either the start of the end node
                                      cannot be found, or there is no path
                                      from the start to the end node
     * @return the cost of the shortest path between these nodes
     */
    public double shortestPathCost(NodeType start, NodeType end) {
        // Get start and end nodes from the graph
        Node startNode = nodes.get(start);
        Node endNode = nodes.get(end);

        //Compute Shortest Path using the computeShortestPath method of Dijkstra's algorithm
        SearchNode result = computeShortestPath(startNode, endNode);

        // For midweek submission: simple implementation
        return result.cost;
    }
    public static class DijkstraGraphTest {
        /**
         * Test case based on lecture example: simple graph with known shortest path
         * Tests both path data and cost calculation
         */
        @Test
        public void testLectureExample() {
            // Create a simple graph: A → B → C
            DijkstraGraph<String, Integer> graph = new DijkstraGraph<>();
            graph.insertNode("A");
            graph.insertNode("B");
            graph.insertNode("C");
            
            // Add edges with weights
            graph.insertEdge("A", "B", 5);
            graph.insertEdge("B", "C", 3);
            
            // Test shortest path from A to C
            List<String> path = graph.shortestPathData("A", "C");
            double cost = graph.shortestPathCost("A", "C");
            
            // Verify the path and cost
            Assertions.assertEquals(Arrays.asList("A", "B", "C"), path);
            Assertions.assertEquals(8.0, cost, 0.001);
        }

        /**
         * Test case with multiple possible paths to verify algorithm 
         * chooses the cheapest one
         */
        @Test
        public void testMultiplePaths() {
            DijkstraGraph<String, Integer> graph = new DijkstraGraph<>();
            graph.insertNode("Start");
            graph.insertNode("A");
            graph.insertNode("B"); 
            graph.insertNode("End");
            
            // Start→A→End (cost: 2+3=5) 와 Start→B→End (cost: 3+2=5)
            graph.insertEdge("Start", "A", 2);
            graph.insertEdge("A", "End", 3);
            graph.insertEdge("Start", "B", 3);
            graph.insertEdge("B", "End", 2);

            List<String> path = graph.shortestPathData("Start", "End");
            double cost = graph.shortestPathCost("Start", "End");
            
            Assertions.assertEquals(5.0, cost, 0.001);
        }

        /**
         * Test case where no path exists between start and end nodes
         * Verifies that NoSuchElementException is thrown
         */
        @Test
        public void testNoPathScenario() {
            DijkstraGraph<String, Integer> graph = new DijkstraGraph<>();
            graph.insertNode("X");
            graph.insertNode("Y");
            graph.insertNode("Z");
            
            // Create disconnected graph: X→Y but no connection to Z
            graph.insertEdge("X", "Y", 5);
            
            // Should throw exception when no path exists
            Assertions.assertThrows(NoSuchElementException.class, () -> {
                graph.shortestPathCost("X", "Z");
            });
            
            Assertions.assertThrows(NoSuchElementException.class, () -> {
                graph.shortestPathData("X", "Z");
            });
        }
    }
}
