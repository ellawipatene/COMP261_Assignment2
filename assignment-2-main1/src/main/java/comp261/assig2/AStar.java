package comp261.assig2;

/*
Dijkstra search alogrithm

@author: Simon McCallum, Github Copilot
*/

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.ResourceBundle.Control;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

// dijkstra class for path finding in the graph
public class AStar {

    public static boolean isDistance = false; 
 

    public static ArrayList<Edge> findShortestPathEdges(Graph graph, Stop start, Stop end) {

        // check if the start and end stops are null
        if (start == null || end == null) {
            return null;
        }
        // used to check how many nodes you visited. Lower is better H.
        int totalExplored = 0;

        ArrayList<Stop> stops = graph.getStopList(); 
        
        // creating a comparison function for the priority queue based on Path
        Comparator<PathItem> pathStopCompare = new PathCostComparator();
        // create a new priority queue for the finge        
        PriorityQueue<PathItem> fringe = new PriorityQueue<PathItem>(pathStopCompare);
        
        // create a new array list for the path to be extracted
        ArrayList<PathItem> visitedStops = new ArrayList<PathItem>();
       
        int currentFingeCost = 0;

        // vital step to make sure you can find the new path
        graph.resetVisited();

        start.setCost(0);

        double f = g(start) + heuristic(start, end, null); // also could be f(start,start, 0, start,end);
        fringe.add(new PathItem(start, 0, f, null, null)); // the input is (node, cost, f, prev, edge)

        // while the queue is not empty
        while (!fringe.isEmpty()) {
            PathItem current = fringe.poll();
            Stop currentStop = current.getStop(); 

            if(!currentStop.isVisited()){
                currentStop.setVisited(true);
                visitedStops.add(current);
                double currentCost = current.getCost(); 
                totalExplored++;

                if(currentStop.equals(end)){
                    ArrayList<Edge> shortestEdgePath = makeEdgePath(graph, visitedStops, start, end);
                    return shortestEdgePath;
                }

                for(Edge edge: currentStop.getNeighbours()){
                    Stop toStop = edge.getToStop();
                    if(!toStop.isVisited()){

                        double cost = 0;
                        if(isDistance){ // for changing between sorting with distance or time
                            cost = currentCost + edge.getDistance(); 
                        }else{
                            cost = currentCost + edge.getTime(); 
                        }

                        if(cost < 0){
                            System.out.println("Error: negative cost");
                        }

                        f = currentCost + heuristic(toStop, end, edge);

                        fringe.add(new PathItem(toStop, cost, f, currentStop, edge)); 
                    }
                }
            }    
        }

        // comment on failue
        System.out.println("Error: " + start + " to " + end + " not found");
        return new ArrayList<Edge>();
    }



    /**
     * build a path from the end back to the start from the PathItem data
     * 
     * @param graph
     * @param visited the nodes visited while searching for this trip
     * @param start
     * @param goal
     * @return the list of stops in the path
     */
    private static ArrayList<Edge> makeEdgePath(Graph graph, ArrayList<PathItem> visited, Stop start, Stop goal) {
        ArrayList<Edge> path = new ArrayList<Edge>();
        Edge currentItem = visited.get(visited.size() - 1).getEdge(); // the last edge added is on the path

        // while the current item is not the starttripID??? 
        while (currentItem.getFromStop() != start) {
            path.add(currentItem);
            // find the stop from the visited PathItems
            for (PathItem visitedItems : visited) {
                if (visitedItems.getEdge() == null) {
                    continue;
                }
                if (visitedItems.getEdge().getToStop() == currentItem.getFromStop()) {
                    if (visitedItems.getEdge().getToStop() == null) {
                        return null;
                    }
                    currentItem = visitedItems.getEdge();
                }
            }
            if (currentItem == null) { // if not invalid path
                System.out.println("error: ");
                return null;
            }
        }
        // finally add the start and return
        path.add(currentItem);
        return path;
    }


    public static double f( Stop current, double edgeCost, Stop neighbour, Stop end, Edge edge) {
        return g(current) + edgeCost + heuristic(neighbour, end, edge);
    }

    public static double g( Stop current) {
        return current.getCost();
    }

    public static double heuristic(Stop current, Stop goal, Edge edge) {
        double distance = current.distance(goal);
        double time = 1; 

        if(distance < 0){
            System.out.println("Error: negative distance.");
        }

        // if they want to search using time
        if(isDistance == false){
            if(edge != null){
                double velocity = Transport.getSpeedMPS(edge.getTripId());
                time = distance/velocity;
                return time; 
            }
        }

        return current.distance(goal);
    }

}
