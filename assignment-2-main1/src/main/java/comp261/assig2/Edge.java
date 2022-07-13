package comp261.assig2;

public class Edge {

    private Stop fromStop;
    private Stop toStop;
    
    private String tripId; // this could be a tripId or "walking" from Transport.WALKING_TRIP_ID

    private double distance; // distance between the two stops of the edge
    private double time; // in seconds between the two stops of the edge
    private double cost; // a cost for the edge for other costs

    // todo: add a constructor
    public Edge(Stop fromStop, Stop toStop, String tripId, double time) {
        this.fromStop = fromStop;
        this.toStop = toStop;
        this.time = time;
        this.distance = fromStop.distance(toStop);
        this.tripId = tripId;
    }

    // todo add getters and setters

    public Stop getFromStop() {
        return fromStop;
    }

    public void setFromStop(Stop fromStop) {
        this.fromStop = fromStop;
    }

    public Stop getToStop() {
        return toStop;
    }

    public void setToStop(Stop toStop) {
        this.toStop = toStop;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public String toString() {
        return "e " + fromStop.getId() + " to " + toStop.getId() + " id " + tripId + " t:" + time;
    }

}
