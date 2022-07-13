package comp261.assig2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javafx.scene.paint.Color; 

// structure for holding stop information 

public class Stop {
    // location of the stop
    private GisPoint loc;
    private String name;
    private String id;

    // data structure for holding a link to the trips that stop is part of
    private ArrayList<Trip> trips;
    // data structure for holding the edges connecting to the stop
    private ArrayList<Edge> neighbours;

    // temp flags used for path finding
    private boolean visited; // a temp visited flag useful for DFS, connected components and pathfinding
    private double cost; // temp cost associated with a path finding task

    private int subGraphId; // used to denote which subgraph the stop belongs to
    private Stop root; // used for finding connected components.

    private String zoneId; // used for calculating a trips fare

    private int depth; // used for making connected components

    /**
     * Constructor for a stop
     * 
     * @param id   4 or 5 digit stop id
     * @param name Long name for the stop
     * @param lat
     * @param lon
     */
    public Stop(double lon, double lat, String name, String id, String zone) {
        this.loc = new GisPoint(lon, lat);
        this.name = name;
        this.id = id;
        this.depth = Integer.MAX_VALUE; 
        this.zoneId = zone;      
    }

    /**
     * Stop with no name
     * 
     * @param lon
     * @param lat
     * @param id  4 or 5 digit stop id
     */
    public Stop(double lon, double lat, String id) {
        this.loc = new GisPoint(lon, lat);
        this.id = id;
        this.name = "";
        this.depth = Integer.MAX_VALUE; 
    }

    /**
     * Stop with no name or id - just a location give it an id based on the
     * goegraphic position
     * 
     * @param lon
     * @param lat
     */
    public Stop(double lon, double lat) {
        this.loc = new GisPoint(lon, lat);
        this.name = "";
        this.id = "(lon:" + lon + ",lat:" + lat + ")";
        this.depth = Integer.MAX_VALUE; 
        
    }

    public Color getColor(){
        if(this.zoneId.equals("1")){return Color.DEEPSKYBLUE;}
        if(this.zoneId.equals("2")){return Color.DARKBLUE;}
        if(this.zoneId.equals("3")){return Color.LIGHTSEAGREEN;}
        if(this.zoneId.equals("4")){return Color.SEAGREEN;}
        if(this.zoneId.equals("5")){return Color.GREEN;}
        if(this.zoneId.equals("6")){return Color.STEELBLUE;}
        if(this.zoneId.equals("7")){return Color.LIGHTBLUE;}
        if(this.zoneId.equals("8")){return Color.SLATEBLUE;}
        if(this.zoneId.equals("9")){return Color.NAVY;}
        if(this.zoneId.equals("10")){return Color.DARKGREEN;}
        if(this.zoneId.equals("11")){return Color.AQUA;}
        if(this.zoneId.equals("12")){return Color.DARKCYAN;}
        if(this.zoneId.equals("13")){return Color.LIGHTGREEN;}
        if(this.zoneId.equals("14")){return Color.DARKBLUE;}
        return Color.BLUE; 
    }

    /**
     * Get the location of the stop
     * 
     * @return GisPoint object of location on earth
     */
    public GisPoint getPoint() {
        return this.loc;
    }

    /**
     * Get the longitude of the stop - prefer to use getPoint() as it maintains the
     * GisPoint object type checking
     * 
     * @return longitude (X) of the stop - prefer to use getPoint() as it maintains
     *         the GisPoint object type checking
     */
    public double getLon() {
        return this.loc.getLon();
    }

    /**
     * Get the latitude of the stop - prefer to use getPoint() as it maintains the
     * GisPoint object type checking
     * 
     * @return latitude (Y) of the stop - prefer to use getPoint() as it maintains
     *         the GisPoint object type checking
     */
    public double getLat() {
        return this.loc.getLat();
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getZone(){
        return this.zoneId; 
    }


    /**
     * returns distance in meters between this stop and a GisPoint
     * 
     * @param loc 
     * @return distance in meters between this stop and another stop
     */
    public double distance(GisPoint loc) {
        return this.loc.distance(loc);
    }
    
    /**
     * returns distance in meters between this stop and another stop
     * @param toStop
     * @return distance in meters between this stop and another stop
     */
    public double distance(Stop toStop) {
        return this.loc.distance(toStop.loc);
    }

    /** 
     * Display a stop
     * 
     * @return string of the stop information in the format: XXXX: long name at (lon,lat)
     */
    public String toString() {
        // TODO: decide how you want to print a stop
        return id + ": " + name + " at (" + loc.getLon() + ", " + loc.getLat() + ")";
    }

    /**
     * @return if the ids of the two stops are identical
     */
    public boolean equals(Stop node) {
        return this.id == node.id;
    }

    /** 
     * @param name the name to check 
     * @return if this is the same name as the name passed in
    */
    public boolean equals(String name) {
        return this.name.equals(name);
    }

    /**
     * @param a GisPoint to check if the stop is in an **identical** location
     * @return is this stop in the same location as the given point
     */
    public boolean equals(GisPoint point) {
        return this.loc.equals(point);
    }


    /**
     * adding a trip that goes through this stop
     * @param trip
     */
    public void addTrip(Trip trip) {
        // check if it is the first trip being added
        if (this.trips == null) {
            this.trips = new ArrayList<Trip>();
        }
        this.trips.add(trip);
    }

    // get trips
    public ArrayList<Trip> getTrips() {
        return this.trips;
    }


    /**
     * large function that turns trips associated with the stop into edges for outgoing neighbour list
     * @param graph
     */
    public void makeNeighbours(HashMap<String,Stop> allStops) {

        // new neighbours list
        neighbours = new ArrayList<Edge>();
        
        // if there are trips associated with the stop
        if (trips != null) {
            for (Trip t : trips) {
                // step through the stops in the trip adding edges - remember to be -1 on size as we are going i to i+1
                for (int i = 0; i < t.getStopIds().size() - 1; i++) {
                    // if the stop id is the same as this stop id
                    String tripFromStopId = t.getStopIds().get(i);
                    String tripToStopId = t.getStopIds().get(i+1);
                    if (tripFromStopId.equals(this.id)) {
                        Stop neighbour = allStops.get(tripToStopId);
                        // start with infinit cost/time
                        Double time = Double.POSITIVE_INFINITY;
                        // if there are nulls then we need to create a new time
                        // manages the times being null or incorrect
                        if (t.getTimes().get(i) == null || t.getTimes().get(i + 1) == null) {
                            double speed = Transport.getSpeedMPS(t.getStop_pattern_id());
                            System.out.println("Time is null");
                            time = this.distance(neighbour) / speed;
                        } else {
                            time = t.getTimes().get(i + 1) - t.getTimes().get(i);
                            if (time < 0) {
                                double speed = Transport.getSpeedMPS(t.getStop_pattern_id());
                                System.out.println("Time is negative");
                                time = this.distance(neighbour) / speed;
                            }
                        }

                        Edge edge = new Edge(this, neighbour, t.getStop_pattern_id(), time);
                        this.neighbours.add(edge);
                    }
                }
            }
        } else {
            System.out.println("No trips for stop: " + this.id);
        }
    }

    // used for path finding
    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    /**
     * if visited during component or path finding
     * @return visited flag
     */
    public boolean isVisited() {
        return visited;
    }


    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getCost() {
        return cost;
    }

    public ArrayList<Edge> getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(ArrayList<Edge> neighbours) {
        this.neighbours = neighbours;
    }

    /** 
     * @param subGraphId the id of the graph so stops in the game subgraph can be drawn in the same colour or highlighted
     */
    public void setSubGraphId(int subGraphId) {
        this.subGraphId = subGraphId;
    }

    public int getSubGraphId() {
        return subGraphId;
    }

    /**
     * @param root - used for finding components of the graph.  The root is where the compent started searching
     */
    public void setRoot(Stop root) {
        this.root = root;
    }

    public Stop getRoot() {
        return root;
    }

    // add neighbour edge
    public void addNeighbour(Edge edge) {
        if (this.neighbours == null) {
            this.neighbours = new ArrayList<Edge>();
        }
        this.neighbours.add(edge);
    }

    /**
     * used to delete a specific connection from a specific trip
     * @param to the stop to which the edge is being removed
     * @param tripId which trip to remove the edge from
     */
    public void deleteEdge(Stop to, String tripId) {
        Iterator<Edge> edge_itr = this.neighbours.iterator();
        while (edge_itr.hasNext()) {
            Edge edge = edge_itr.next();
            if (edge.getTripId().equals(tripId)&& edge.getToStop().equals(to)) {
                edge_itr.remove();
            }
        }
    }

    // Read more:
    // https://www.java67.com/2014/03/2-ways-to-remove-elementsobjects-from-ArrayList-java.html#ixzz7OrkLI5fh
    /**
     * 
     */
    public void deleteAllEdges(String tripId) {
        // remove edges that match the trip pattern id
        Iterator<Edge> edge_itr = this.neighbours.iterator();
        while (edge_itr.hasNext()) {
            Edge edge = edge_itr.next();
            if (edge.getTripId().equals(tripId)) {
                edge_itr.remove();
            }
        }

    }

    public int getDepth(){
        return depth;
    }

    public void setDepth(int depth){
        this.depth = depth; 
    }
}
