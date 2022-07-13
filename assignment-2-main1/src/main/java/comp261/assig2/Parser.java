package comp261.assig2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * This utility class parses the files, and return the relevant data structure.
 * Internally it uses BufferedReaders instead of Scanners to read in the files,
 * as Scanners are slow.
 * 
 * @author tony, Simon
 */
public class Parser {

    public static HashMap<String, Stop> parseStops(File nodeFile) {
        HashMap<String, Stop> stops = new HashMap<String, Stop>();

        // read the node file
        // tab separated stop descriptions
        // stop_id stop_code stop_name stop_desc stop_lat stop_lon zone_id stop_url
        // location_type parent_station stop_timezone

        try {
            // make a reader
            BufferedReader br = new BufferedReader(new FileReader(nodeFile));
            br.readLine(); // throw away the top line of the file
            String line;
            // read in each line of the file
            int count = 0;
            while ((line = br.readLine()) != null) {
                // tokenise the line by splitting it on tabs
                String[] tokens = line.split("[\t]");
                if (tokens.length >= 6) {
                    // process the tokens
                    String stopId = tokens[0];
                    String stopName = tokens[2];
                    double lat = Double.valueOf(tokens[4]);
                    double lon = Double.valueOf(tokens[5]);
                    String zone = tokens[6];
                    stops.put(stopId, new Stop(lon, lat, stopName, stopId, zone));
                }
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException("file reading failed.");
        }
        return stops;
    }

    // parse the trip from the stopPattern file
    // file contais: stop_pattern_id,stop_id,stop_sequence,timepoint
    public static HashMap<String, Trip> parseTrips(File tripFile) {
        HashMap<String, Trip> tripMap = new HashMap<String, Trip>();
        try {
            // make a reader
            BufferedReader br = new BufferedReader(new FileReader(tripFile));
            br.readLine(); // throw away the top line of the file.
            String line;
            // read in each line of the file
            while ((line = br.readLine()) != null) {
                // tokenise the line by splitting it at ",".
                String[] tokens = line.split("[,]");
                if (tokens.length >= 4) {
                    // process the tokens
                    String stopPatternId = tokens[0];
                    String stopId = tokens[1];
                    int stopSequence = Integer.parseInt(tokens[2]);
                    String time_str = tokens[3];
                    Double time = null;
                    if (!time_str.equals("null")) {
                        time = Double.parseDouble(tokens[3]);
                    }

                    Trip trip = tripMap.get(stopPatternId);
                    if (trip == null) {
                        trip = new Trip(stopPatternId);
                        tripMap.put(stopPatternId, trip);
                    }
                    trip.addStopId(stopId, stopSequence, time);

                    
                }
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException("file reading failed.");
        }
        return tripMap;
    }



    // load and parse a geojson file into a hashmap
    // ID, Zone, Shape_Length, Shape_Area, coordinates
    public static Zoning parseGeoJson(File geoJsonFile) {
        Zoning geoJson;
        try {
            // make a reader
            BufferedReader br = new BufferedReader(new FileReader(geoJsonFile));
            br.readLine(); // throw away the top line of the file.
            br.readLine(); // throw away the top two line of the file.
            geoJson = new Zoning("shape file", "Wellington");
            String line;

            // read in each line of the file
            while ((line = br.readLine()) != null) {
                // tokenise the line by splitting it at ",".

                String[] tokens = line.split("[,]");
                if (tokens.length >= 5) {
                    // process the tokens
                    String id = tokens[0];
                    String zone_str = tokens[1];

                    double shapeLength = Double.parseDouble(tokens[2]);
                    double shapeArea = Double.parseDouble(tokens[3]);
                    Shape zone = new Shape(Integer.valueOf(zone_str), shapeLength, shapeArea);
                    // for loop through the coordinates
                    GeoPoly poly = new GeoPoly();
                    for (int i = 4; i < tokens.length; i = i + 2) {
                        double lon = Double.parseDouble(tokens[i]);
                        double lat = Double.parseDouble(tokens[i + 1]);
                        poly.add(new GisPoint(lon, lat));
                    }
                    // zone.addShape(poly);
                    if (geoJson.getZones().get(zone_str) == null) {
                        geoJson.getZones().put(zone_str, zone);
                    }
                    geoJson.getZones().get(zone_str).addShape(poly);

                }
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException("file reading failed.");
        }
        return geoJson;

    }

}
