package comp261.assig2;

// A path stop for Dijkstra search

public class PathItem {
    // Values in the Dijkstra / AStar search
    public Stop stop = null;
    public double cost = -1; // use -1 as the error value
    public double f = 0; // used to AStar search
    public Stop prev = null;
    public Edge edge = null;// the edge used to get to this stop

    // construct with all values
    public PathItem(Stop stop, double cost, double f, Stop prev, Edge edge) {
        this.stop = stop;
        this.cost = cost;
        this.f = f;
        this.prev = prev;
        this.edge = edge;
    }

    // constructor for dijkstra
    public PathItem(Stop stop, double cost, Stop prev) {
        this.stop = stop;
        this.cost = cost;
        f = cost; // there is no heuristic set to cost
        this.prev = prev;
        this.edge = null; // for dijkstra assume this is not a multigraph
    }

    // set the vaules back to default
    public void resetPathStop() {
        cost = -1;
        prev = null;
        f = 0;
        edge = null;
    }

    public Stop getStop() {
        return stop;
    }

    public double getCost() {
        return cost;
    }

    public Edge getEdge() {
        return edge;
    }

    public Stop getPrev() {
        return prev;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setPrev(Stop prev) {
        this.prev = prev;
    }

    // useful for printing to text areas
    public String toString() {
        String prevName = "null";
        // check if there is a previous stop
        if (prev != null) {
            prevName = prev.getName();
        }

        if (f != 0) { // remove the f value if it is 0
            return "<" + stop.getName() + " g:" + String.format("%,.2f", cost) + " f: " + String.format("%,.2f", f)
                    + "  " + prevName + ">";
        } else {
            return "<" + stop.getName() + " g:" + String.format("%,.2f", cost) + " prev: " + prevName + ">";
        }
    }

}
