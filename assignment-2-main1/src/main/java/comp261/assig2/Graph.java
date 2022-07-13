package comp261.assig2;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.scene.paint.Color;

public class Graph {

    private HashMap<String, Stop> stopsMap;
    private HashMap<String, Trip> tripsMap;

    private ArrayList<Stop> stopList;
    public Trie trie;

    public Zoning geoJson;

    private int subGraphs = 0;

    public Graph(HashMap<String, Stop> stops, HashMap<String, Trip> trips) {
        this.stopsMap = stops;
        this.tripsMap = trips;
        buildStopList();
        createNeighbours();
    }

    // constructor with parsing
    public Graph(File stopFile, File tripFile, File geoJsonFile) {
        stopsMap = new HashMap<String, Stop>();
        tripsMap = new HashMap<String, Trip>();
        stopsMap = Parser.parseStops(stopFile);
        tripsMap = Parser.parseTrips(tripFile);
        geoJson = Parser.parseGeoJson(geoJsonFile);
        // caclculate exact memory usage of the graph

        buildStopList();
        attachTripsToStops();
        createNeighbours();

    }

    // buildStoplist from hashmap
    private void buildStopList() {
        stopList = new ArrayList<Stop>();
        for (Stop s : stopsMap.values()) {
            stopList.add(s);
        }
    }



    // attach trip data to each stop
    private void attachTripsToStops() {
        for (Trip trip : tripsMap.values()) {
            for (String stopId : trip.getStopIds()) {
                Stop stop = stopsMap.get(stopId);
                if (stop != null) {
                    // add the trip to the stop
                    stop.addTrip(trip);
                } else {
                    System.out.println("Missing stop pattern id: " + stopId);
                }
            }
        }
    }

    /**
     * For every stop tell it to construct the edges associated with the trips that have been stored in it.
     */
    private void createNeighbours() {
        for (Stop stop : stopsMap.values()) {
            stop.makeNeighbours(this.stopsMap);
        }
    }

    // get first stop that starts with a search string
    public Stop getFirstStop(String search) {
        // Search for the first stop matching the search string
        // This is slow and would be faster with a Trie 
        Stop firstStop = null;
        for (Stop stop : stopList) {
            if (stop.getName().startsWith(search)) {
                firstStop = stop;
                break;
            }
        }
        return firstStop;
        // This would be the call to the Trie from Assignment 1
        //return trie.getAll(search).get(0);
    }

    // get all stops that start with a search string
    public List<Stop> getAllStops(String search) {
        //search for all stops matching the search string
        List<Stop> allStops = new ArrayList<Stop>();
        for (Stop stop : stopList) {
            if (stop.getName().startsWith(search)) {
                allStops.add(stop);
            }
        }
        return allStops;
        // This would be the call to the Trie from Assignment 1
        // return trie.getAll(search);
    }

    // getter for stopList
    public ArrayList<Stop> getStopList() {
        return stopList;
    }

    public HashMap<String, Stop> getStops() {
        return stopsMap;
    }

    public void setStops(HashMap<String, Stop> stops) {
        this.stopsMap = stops;
    }

    public HashMap<String, Trip> getTrips() {
        return tripsMap;
    }

    public void setTrips(HashMap<String, Trip> trips) {
        this.tripsMap = trips;
    }



    public void resetVisited() {
        for (Stop stop : stopList) {
            stop.setVisited(false);
            stop.setCost(Double.MAX_VALUE);
        }
    }

    // I have used Kosaraju's_algorithm from https://en.wikipedia.org/wiki/Kosaraju%27s_algorithm
    // You can use this or use Tarjan's algorithm for strongly connected components https://en.wikipedia.org/wiki/Tarjan%27s_strongly_connected_components_algorithm
    // find graph components and lable the nodes in each component
    public int findComponents() {

        int subGraph = 0; 
        Stop root = null; 
     
        resetVisited();
        resetRoot();
        

        ArrayList<Stop> visitOrder = new ArrayList<Stop>(); 
        for(Stop stop: stopList){
            if(!stop.isVisited()){
                visitAllConnections(stop, visitOrder);
            }
        } 
        
        for(int i = visitOrder.size()-1; i > -1; i--){
            Stop stop = visitOrder.get(i);
            if(stop.getRoot() == null){
                subGraph++; 
                assignRoot(stop, root, subGraph);   
            }
        }
        
        System.out.println(subGraph);
        subGraphs = subGraph;  
        return subGraphs;
    }

    /**
     * Recursively visit all connections of a stop and build a list of stops visited from this stop
     */
    private void visitAllConnections(Stop stop, ArrayList<Stop> visitOrder) {
        stop.setVisited(true);
        visitOrder.add(stop);
        for(Edge edge: stop.getNeighbours()){
            if(!edge.getToStop().isVisited()){
                visitAllConnections(edge.getToStop(), visitOrder); 
            }
        }      
    }

    /**
     * Recursively assign the root node of a subgraph
     */
    public void assignRoot(Stop stop, Stop root, int component) {
        
        if(root == null){root = stop;}
        stop.setRoot(root);
        stop.setSubGraphId(component);

        for(Edge edge: stop.getNeighbours()){
            Stop toStop = edge.getToStop(); 
            if(toStop.getRoot() == null){
                assignRoot(toStop, root, component); 
            }
        }
    }

    /**
     * reset the root and the subgraph ID of all stops
     */
    public void resetRoot() {
        for (Stop stop : stopList) {
            stop.setRoot(null);
            stop.setSubGraphId(-1);
        }
    }


    public int getSubGraphCount() {
        return subGraphs;
    }

    // add walking edges
    public void addWalkingEdges(double walkingDistance) {
        int count = 0;
        for(Stop stop: stopList)
         {
             for(Stop toStop: stopList)
             {
                 if(stop.distance(toStop) < walkingDistance){
                    stop.addNeighbour( new Edge(stop, toStop, Transport.WALKING_TRIP_ID, stop.distance(toStop)/Transport.WALKING_SPEED_MPS));
                    count++;
                 }
                

            }
        }
        System.out.println("Walking edges added: " + count);
    }

    // remove walking edges  - could just make them invalid or check the walking_checkbox
    public void removeWalkingEdges() {
        for (Stop stop : stopList) {
            stop.deleteAllEdges(Transport.WALKING_TRIP_ID);// remove all edges with the walking trip id
        }
    }

    // A nicer way to build a string of a list of stops
    public String DisplayStops(List<Stop> listOfStops) {
        String returnString = "";
        for (Stop stop : listOfStops) {
            returnString += stop.getName() + "\n";
        }
        return returnString;
    }

}
