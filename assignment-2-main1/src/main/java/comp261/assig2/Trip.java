package comp261.assig2;

import java.util.ArrayList;

// structure for holding trip information
public class Trip {
    
    private String stop_pattern_id;

    // paired lists with stop id and stop times.  need to make sure they remain in order
    ArrayList<String> stopIds;
    ArrayList<Double> times;



    // constructor post parsing
    public Trip(String stop_pattern_id, ArrayList<String> stopIds, ArrayList<Double> times) {
        this.stop_pattern_id = stop_pattern_id;
        this.stopIds = stopIds;
    }

    // constructor pre parsing used to create and then add stops to the trip
    public Trip(String stop_pattern_id) {
        this.stop_pattern_id = stop_pattern_id;
        this.stopIds = new ArrayList<String>();
        this.times = new ArrayList<Double>();
    }

    /**
     * Add a stop to the end of the current trip
     * @param stopId the 4/5 digit stop id
     * @param stop_sequence the stop sequence number not used in this function but could be used to check if the stop is in the correct order
     * @param time  the time from the start of the trip to the current stop
     */
    public void addStopId(String stopId, Integer stop_sequence, Double time) {
        this.stopIds.add(stopId);
        this.times.add(time);
    }

    public String getStop_pattern_id() {
        return stop_pattern_id;
    }

    /* do not need a setStop_pattern_id as they should not be mutated but creating a new trip for a new stoppattern.
    public void setStop_pattern_id(String stop_pattern_id) {
        this.stop_pattern_id = stop_pattern_id;
    }
    */

    public ArrayList<String> getStopIds() {
        return stopIds;
    }

    public void setStopIds(ArrayList<String> stopIds) {
        this.stopIds = stopIds;
    }

    // to string
    public String toString() {
        String s = "";
        s += "Trip: " + stop_pattern_id + "\t stops: " + stopIds.toString() + "\t times: " + times.toString();
        return s;
    }

    /**
     * Return the times for each stop in the trip.
     * @return the list of times in seconds
     */
    public ArrayList<Double> getTimes() {
        return times;
    }

    public void setTimes(ArrayList<Double> times) {
        this.times = times;
    }

}
