package comp261.assig2;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.beans.value.ObservableValue;
import javafx.event.*;

public class GraphController {

    // save the graph datastructure here as it is easy to get to using Main.graph
    public Graph graph;
    // names from the items defined in the FXML file
    @FXML
    private TextField startText;
    @FXML
    private TextField goalText;
    @FXML
    private Label Start;
    @FXML
    private Label Goal;
    @FXML
    private Button load;
    @FXML
    private Button quit;
    @FXML
    private Button english_bt;
    @FXML
    private Button maori_bt;
    @FXML
    private Button up;
    @FXML
    private Button down;
    @FXML
    private Button left;
    @FXML 
    private Button distChange; 
    @FXML
    private Button right;
    @FXML
    private Canvas mapCanvas;
    @FXML
    private Label nodeDisplay;
    @FXML
    private TextArea tripText;

    @FXML
    private CheckBox walking_ch;
    @FXML
    private Button connectedComponents_bt;
    @FXML
    private Slider walkingDistance_sl;
    @FXML
    private TextField walkingDistance_tf;

    // These are used to map the nodes to the location on screen
    private Double scale = 5000.0; // 5000 gives 1 pixel ~ 20 meters
    private static final double ratioLatLon = 0.73; // in Wellington ratio of latitude to longitude
    private GisPoint mapOrigin = new GisPoint(174.77, -41.3); // Lon Lat for Wellington
    private final GisPoint VUWSA_OFFICE = new GisPoint(174.769163, -41.288875); // Lon Lat for VUWSA office
    private final GisPoint SIMON_OFFICE = new GisPoint(174.768795, -41.289576); // Lon Lat for Simon's office

    private static int stopSize = 5; // drawing size of stops
    private static int moveDistance = 100; //
    private static double zoomFactor = 1.1; // zoom in/out factor

    // used for A*
    private Stop startLocation;
    private Stop goalLocation;
    // used to prevent drag from creating a click
    private Boolean dragActive = false;

    private ArrayList<Stop> highlightNodes = new ArrayList<Stop>();
    private ArrayList<Stop> pathStops = null;
    private ArrayList<Edge> pathEdges = null;

    // set up connections between the buttons and the methods
    public void initialize() {

        // read the three input files
        this.graph = new Graph(new File("data/stops.txt"),
                new File("data/stop_pattern_times.txt"),
                new File("data/Wellington.gis.json.csv"));
        drawGraph(graph);
    }

    // get scale
    public double getScale() {
        return scale;
    }

    // get origin
    public GisPoint getOrigin() {
        return mapOrigin;
    }

    // get mapCanvas
    public Canvas getMapCanvas() {
        return mapCanvas;
    }

    // get ratLatLon
    public double getRatioLatLon() {
        return ratioLatLon;
    }

    /* handle the load button being pressed connected using FXML */
    public void handleLoad(ActionEvent event) {
        Stage stage = (Stage) mapCanvas.getScene().getWindow();
        System.out.println("Handling event " + event.getEventType());
        FileChooser fileChooser = new FileChooser();
        // Set to user directory or go to default if cannot access

        File defaultNodePath = new File("data");
        if (!defaultNodePath.canRead()) {
            defaultNodePath = new File("C:/");
        }
        fileChooser.setInitialDirectory(defaultNodePath);
        FileChooser.ExtensionFilter extentionFilter = new FileChooser.ExtensionFilter("txt files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extentionFilter);

        fileChooser.setTitle("Open Stop File");
        File stopFile = fileChooser.showOpenDialog(stage);

        fileChooser.setTitle("Open Stop Pattern File");
        File tripFile = fileChooser.showOpenDialog(stage);

        extentionFilter = new FileChooser.ExtensionFilter("txt files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().set(0,extentionFilter);
        fileChooser.setTitle("Open Geo File");
        File geoJsonFile = fileChooser.showOpenDialog(stage);

        graph = new Graph(stopFile, tripFile, geoJsonFile);
        drawGraph(graph);
        event.consume(); // this prevents other handlers from being called
    }

    // handle the quit button being pressed connected using FXML
    public void handleQuit(ActionEvent event) {
        System.out.println("Quitting with event " + event.getEventType());
        event.consume();
        System.exit(0); // system exit with status 0 - normal
    }

    // handle the english button being pressed connected using FXML
    public void handleEnglish(ActionEvent event) {
        System.out.println("English with event " + event.getEventType());
        event.consume();
    }

    // handle the maori button being pressed connected using FXML
    public void handleMaori(ActionEvent event) {
        System.out.println("Maori with event " + event.getEventType());
        event.consume();
    }

    // Assignment 1
    public void handleZoomin(ActionEvent event) {
        System.out.println("Zoom in event " + event.getEventType());
        scale *= zoomFactor;
        drawGraph(graph);

        event.consume();
    }

    // Assignment 1
    public void handleZoomout(ActionEvent event) {
        System.out.println("Zoom out event " + event.getEventType());
        scale *= 1.0/zoomFactor;
        drawGraph(graph);

        event.consume();
    }

    // Assignment 1
    public void handleUp(ActionEvent event) {
        System.out.println("Move up event " + event.getEventType());
        GisPoint gp = new GisPoint(0, moveDistance/scale); 
        mapOrigin.add(gp);
        drawGraph(graph);

        event.consume();
    }

    // Assignment 1
    public void handleDown(ActionEvent event) {
        System.out.println("Move Down event " + event.getEventType());
        GisPoint gp = new GisPoint(0, moveDistance/scale); 
        mapOrigin.subtract(gp);
        drawGraph(graph);
        event.consume();
    }

    // Assignment 1
    public void handleLeft(ActionEvent event) {
        System.out.println("Move Left event " + event.getEventType());
        GisPoint gp = new GisPoint(moveDistance/scale, 0);
        mapOrigin.subtract(gp);
        drawGraph(graph);
        event.consume();
    }

    // Assignment 1
    public void handleRight(ActionEvent event) {
        System.out.println("Move Right event " + event.getEventType());
        GisPoint gp = new GisPoint(moveDistance/scale, 0);
        mapOrigin.add(gp);
        drawGraph(graph);

        event.consume();
    }

    // Change from searching with distance VS time
    public void handleDistance(ActionEvent event){
        System.out.println("Changing distance vs time.");
        if(AStar.isDistance){
            AStar.isDistance = false; 
            System.out.println("A* Search using time.");
            tripText.setText("A* Search using time.");
        }else{
            AStar.isDistance = true;
            System.out.println("A* Search using distance.");
            tripText.setText("A* Search using distance.");
        }
    }

    // Key typing event for the Start bus stop
    public void handleStartKey(KeyEvent event) {
        System.out.println("Look up event " + event.getEventType() + "  " + ((TextField) event.getSource()).getText());
        String search = ((TextField) event.getSource()).getText();
        // Display the stops that match in the tripText area to help typing
        tripText.setText(graph.DisplayStops(graph.getAllStops(search)));
        event.consume();
    }

    // Key typing event for the Goal bus stop
    public void handleGoalKey(KeyEvent event) {
        System.out.println("Look up event " + event.getEventType() + "  " + ((TextField) event.getSource()).getText());
        String search = ((TextField) event.getSource()).getText();
        // Display the stops that match in the tripText area to help typing
        tripText.setText(graph.DisplayStops(graph.getAllStops(search)));
        event.consume();
    }

    // When enter is pressed perform an A* search
    public void handleStartAction(ActionEvent event) {
        System.out.println("Look up event " + event.getEventType() + "  " + ((TextField) event.getSource()).getText());
        String search = ((TextField) event.getSource()).getText();
        // set the start search location
        startLocation = graph.getFirstStop(search);

        // perform A* search and get the path edges
        pathEdges = AStar.findShortestPathEdges(graph, startLocation, goalLocation);

        drawGraph(graph); //update the graph
        event.consume();
    }

    // When enter is pressed perform an A* search using the Goal as the destination
    public void handleGoalAction(ActionEvent event) {
        System.out.println("Look up event " + event.getEventType() + "  " + ((TextField) event.getSource()).getText());
        String search = ((TextField) event.getSource()).getText();
        // set the goal search location
        goalLocation = graph.getFirstStop(search);
        // perform A* search and get the path edges
        pathEdges = AStar.findShortestPathEdges(graph, startLocation, goalLocation);

        drawGraph(graph);// update the graph
        event.consume();
    }

    // handleShowConnectedComponents
    public void handleShowConnectedComponents(ActionEvent event) {
        System.out.println("Show connected components event " + event.getEventType());
        //INFO : This is where your find component code is called
        graph.findComponents();
        tripText.setText("Number of Components: " +  (String.valueOf(graph.getSubGraphCount()))); 
        drawGraph(graph);
    }

    // handleAddWalking calls the code to add Walking
    public void handleAddWalking(ActionEvent event) {
        System.out.println("Add walking event " + walking_ch.isSelected());
        if (walking_ch.isSelected()) {
            graph.addWalkingEdges(Double.parseDouble(walkingDistance_tf.getText()));
        } else {
            graph.removeWalkingEdges();
        }
        drawGraph(graph);
    }

    // This handles the connection between the slider and the text field
    public void handleWalkingDistance(ActionEvent event) {
        // devide the text value by two so the slider is 0 - 200
        walkingDistance_sl.setValue(Double.parseDouble(walkingDistance_tf.getText())/2.0);
    }

    // This handles the connection between the slider and the text field
    public void handleWalkingDistanceSlider(ObservableValue<Double> ovn, Double before, Double after) {
        // multiply the slider value by two so the text field is 0 - 200
        walkingDistance_tf.setText(Double.toString(ovn.getValue()*2.0));
    }

    // Mouse scroll for zoom
    public void mouseScroll(ScrollEvent event) {
        // change the zoom level
        double changefactor = 1 + (event.getDeltaY() / 400);
        scale *= changefactor;
        // update the graph
        drawGraph(graph);
        event.consume();
    }

    public double dragStartX = 0;
    public double dragStartY = 0;
    // handle starting drag on canvas
    public void handleMousePressed(MouseEvent event) {
        dragStartX = event.getX();
        dragStartY = event.getY();
        event.consume();
    }

    // handleMouse Drag
    public void handleMouseDrag(MouseEvent event) {
        // pan the map
        double dx = event.getX() - dragStartX;
        double dy = event.getY() - dragStartY;
        dragStartX = event.getX();
        dragStartY = event.getY();
        mapOrigin.move(-dx / (scale * ratioLatLon), (dy / scale));

        drawGraph(graph);
        // set drag active true to avoid clicks highlighting nodes
        dragActive = true;
        event.consume();
    }


    private Stop goalStop = null;
    private Stop prevStartStop = null;

    /*
     * handle mouse clicks on the canvas
     * select the node closest to the click
     */
    public void handleMouseClick(MouseEvent event) {
        if (dragActive) {
            dragActive = false;
            return;
        }

        System.out.println("Mouse click event " + event.getEventType());
        // find node closed to mouse click
        Point2D screenPoint = new Point2D(event.getX(), event.getY());
        GisPoint location = Projection.Screen2Model(screenPoint, this);

        Stop closestStop = findClosestStop(location, graph);
        highlightClosestStop(closestStop);

        highlightNodes.clear();
        highlightNodes.add(closestStop);
        
        // update the start location to be the old goal location
        startLocation = goalLocation;
        // set the new goal location to be the stop just clicked
        goalLocation = closestStop;
            // shortest path planning
        if (startLocation != null && closestStop != startLocation) {
            // INFO: This is where your find path code is called during clicking
            pathEdges = AStar.findShortestPathEdges(graph, startLocation, goalLocation);
        }
        drawGraph(graph);
        event.consume();
    }

    /**
     * Find the closest stop to the given Gis Point location
     * @param x
     * @param y
     * @param graph
     * @return
     */
    public Stop findClosestStop(GisPoint loc, Graph graph) {
        double minDist = Double.MAX_VALUE;
        Stop closestStop = null;
        // This is slow but could be faster if you use a quadtree or KD tree
        for (Stop stop : graph.getStopList()) {
            double dist = stop.distance(loc);
            if (dist < minDist) {
                minDist = dist;
                closestStop = stop;
            };
        }
        if (closestStop != null) {
            return closestStop;
        }
        return null;
    }

    // 
    public void highlightClosestStop(Stop closestStop) {
        if (closestStop != null) {
            highlightNodes.clear();
            highlightNodes.add(closestStop);
            drawGraph(graph);
        }
    }

    /*
     * Drawing the graph on the canvas
     */
    public void drawCircle(double x, double y, int radius) {
        GraphicsContext gc = mapCanvas.getGraphicsContext2D();
        gc.fillOval(x - radius / 2, y - radius / 2, radius, radius);
    }

    public void drawLine(double x1, double y1, double x2, double y2) {
        mapCanvas.getGraphicsContext2D().strokeLine(x1, y1, x2, y2); //set fill colour for the subgraph
        // something keLine(x1, y1, x2, y2);
    }

    /**
     * Draw the Path Edges returned from A* search
     * @param pathEdges
     * @param gc
     */
    public void drawPathEdges(ArrayList <Edge> pathEdges, GraphicsContext gc) {
        double time = 0; 
        String path = "Goal " + goalLocation.getName();
        gc.setLineWidth(3);
        for (Edge edge : pathEdges) {
            if (edge.getTripId() == Transport.WALKING_TRIP_ID) {
                gc.setStroke(Color.BLACK);
            } else {
                if (Transport.isTrain(edge.getTripId())) {
                    gc.setStroke(Color.GREEN);
                } else {
                    gc.setStroke(Color.RED);
                }
            }
            Point2D screenFromPoint = Projection.model2Screen(edge.getFromStop().getPoint(), this);
            Point2D screenToPoint = Projection.model2Screen(edge.getToStop().getPoint(), this);
            drawLine(screenFromPoint.getX(), screenFromPoint.getY(), screenToPoint.getX(), screenToPoint.getY());

            time += edge.getTime(); 
            path = "FROM: " + edge.getFromStop().getId() + " TO: " + edge.getToStop().getId() + ". " + path; 
            path = "    Trip id: " + edge.getTripId() + " Trip Time in Seconds: " + edge.getTime() + " Total time: " + Math.round(time/60) + "min\n" + path;
        }
        path = path + "Total Time: " + Math.round(time/60) + "min\n"; 
        tripText.setText("Start: " + startLocation.getName() + "\n" + path);
    }

    // draw the fare zone
    // INFO: This is to show you the outline of Wellington.
    public void drawFareZones(Zoning geoJson, GraphicsContext gc) {
        HashMap<String, Shape> zones = geoJson.getZones();
        gc.setFill(Color.BLUE);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(1);

        for (Shape zone : zones.values()) {
            for (GeoPoly poly : zone.getShapes()) {
                for (int k = 0; k < poly.getPoints().size() - 1; k++) {
                    Point2D startPoint = Projection.model2Screen(poly.getPoints().get(k), this);
                    Point2D endPoint = Projection.model2Screen(poly.getPoints().get(k + 1), this);
                    drawLine(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
                }
            }
        }
    }

    public void drawGraph(Graph graph) {
        if (graph == null) {
            return;
        }
        GraphicsContext gc = mapCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());

        // store node list so we can use nodes to find edge end points.
        // TODO: Assignment 1 get nodes
        ArrayList<Stop> stopList = graph.getStopList();

        // draw nodes using a lambda function
        stopList.forEach(stop -> {
            int size = stopSize;
            if (highlightNodes.contains(stop)) {
                gc.setFill(Color.RED);
                size = stopSize * 2;
            } else {
                if (graph.getSubGraphCount() > 0) {
                    // TODO: set fill colour for the subgraph
                    // something like:
                    gc.setFill(Color.hsb((stop.getSubGraphId() * (360 / (graph.getSubGraphCount()))) % 360, 1,1));
                   // gc.setFill(Color.hsb((1*(360/(graph.getSubGraphCount()))) % 360, 1,1));
                } else {
                    
                    gc.setFill(stop.getColor());
                    //gc.setFill(Color.BLUE);
                }
            }
            Point2D screenPoint = Projection.model2Screen(stop.getPoint(), this);
            drawCircle(screenPoint.getX(), screenPoint.getY(), size);
        });

        // draw edges using a lambda function
        graph.getTrips().values().forEach(trip -> {
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            for (int i = 0; i < trip.stopIds.size() - 1; i++) {
                Stop fromStop = graph.getStops().get(trip.stopIds.get(i));
                Stop toStop = graph.getStops().get(trip.stopIds.get(i + 1));
                if (fromStop != null && toStop != null) {
                    if (fromStop.distance(toStop) > 100) { // if longer than 100 meters make it thin
                        gc.setLineWidth(0.5);
                        gc.setStroke(Color.GRAY);
                    } else {
                        gc.setLineWidth(1);
                        gc.setStroke(Color.BLACK);
                    }
                    Point2D startPoint = Projection.model2Screen(fromStop.getPoint(), this);
                    Point2D endPoint = Projection.model2Screen(toStop.getPoint(), this);
                    drawLine(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
                } else {
                    System.out.println("Missing stop pattern id: " + fromStop.getId() + " " + toStop.getId());
                }
            }
        });

        // TODO: set Total time     
        if (pathEdges != null) {
            drawPathEdges(pathEdges, gc);
        }

        drawFareZones(graph.geoJson, gc);

        // display the start and goal stops
        if (startLocation != null) {
            startText.setText(startLocation.getName());
        }
        if (goalLocation != null) {
            goalText.setText(goalLocation.getName());
        }
    }
}
