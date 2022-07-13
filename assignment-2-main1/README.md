Username: wipateella
Id: 300558005

## Description of the Code
Minimum:
* Updated the required functions with the assignment One code, E.g: 
    * Filled out the Parser.java to create the stops & trips and add them to their appropriate lists. 
    * Filled out the 'moveLeft' and 'zoomIn' functions in GraphControler.java
* Coded the Dijkstra algorithm, with help from the tutorial three code. (Is now deleted due to being replaced with A*)
* Replaced the Dijkstra algorithm with the A* search. This involved filling out all of the 'TODO' comments in the A* findShortestPathEdges() 
  function, and then using distance for my heuristic by returning current.distance(goal). This is explained in more detail bellow in the disscusion section. 

Core:
* To create and set the connected components, I used the methods findComponents(), visitAllConnections() and assignRoot() in Graph.java. 
  I used Kosaraju's algorithm for this.
    * findComponents() will first clear everything and reset the visited variable in all of the stops. 
    * It will then call visitAllConnections() for every stop that has not yet been visited.
    * visitAllConnections() is a recursive function, that will add stops to the arrayList visitOrder for as long as the stops are connected.
    * After all stops have been 'visited' by visitAllConnections(), findComponents() will then call assignRoot() by running through visitOrder in reverse
    * assignRoot() is also a recursive function. It will run through all of the stops, and if the root has been passed through as null, then 
      it will assign the current stop as the new root. It will then assign all of the stops connected to that root with the same SubGraphId. 
* To label/colour the connected components, in Graph.java, the assignRoot() function gives each stop its own SubGraphId. 
  I then used the line: gc.setFill(Color.hsb((stop.getSubGraphId() * (360 / (graph.getSubGraphCount()))) % 360, 1,1)); which will give 
  the stop a certain colour based on its SubGraphId. 
* To add walking edges, I filled out the addWalkingEdges() function in Graph.java. In this, I have coded a double for loop, which runs through 
  all of the stops twice. This function compares the distance between all of the stops, and if that distance is less than the 'walkingDistance'
  that is passed in as the parameter, then it will create a new walking edge between those two stops. 
  
Completion:
* Changed the heuristic function in AStar.java to search based on time instead of distance. To do this, I used the function Transport.getSpeedMPS()
  to get the appropriate velocity based on the transportation type. Then used time = distance/velocity to get the time estimate. 

Challenge:
* In the MapView.fxml file, I added a button to change the search heuristic from distance to time. This would connect to a method in GraphControler.java
  called handle distance, which changes the boolean variable 'isDistance' in AStar.java.
* Added in flexible walking distance using the slider. This is shown by printing out the number of walking edges added in the terminal. For example, 
  when the slider is at 80, 5852 walking edges are added. But when the slider is at 200, 10942 walking edges are added. 
* I also added colours to the stops, depending on which zone they are located in. To do this, I added in another parameter in the parser for stops. This 
  will add in each stops zone to their object. Stop.java has a method in it called getColor(), which will return a certain colour based on its zoneId. 

### Minimum: A* search [0-40] 
1. [X] Find any path between two nodes (could use dfs to make any path between nodes)
2. [X] Find the shortest path (distance or time) between two nodes (Dijkstra from Tutorial 3)
3. [X] Find the shortest path (distance or time) limiting the number of nodes visited by using a distance heuristic

### Core: Components and Connections [40-65]
1. [X] Show differentiation in the components
2. [X] Label the components and displays number of components
3. [X] Minimised the connected components using algorithm to correctly build components in directed multigraph
4. [X] Connect the components with **walking edges**. Adds walking edges to the graph to connect stops with walking edges.

### Complete[75-85]: Time taken
1. [X] Show time taken for the trip
2. [X] Update the A* to use time as the cost rather than distance

### Challenge[85-100]: Fun stuff do something interesting
* [X] Allow time or distance in A* search
* [ ] Limit travel by transportation type
* [X] Flexible walking distance calculated within A*
* [ ] Implement pin dropping for start and goal points
* [ ] Implement an on route stop (ie start to mid then mid to goal)
* [ ] Download the Metlink data and implement the Trips and Stoptime to actually use the real data to make a time of Day A*
* ... Other stuff that impresses the marker for the final interesting algorithm or data stucture:
* [X] Coloured the stops based on what zone they are in



## Question
Discussing directed multigraphs and why the edges have to be stored as the path is generated.
    This assignment is an example of a multigraph because the Metlink bus system has dirrected edges which connect nodes/stops. 
    We use A* search to find the shortest path between two nodes. This is how our search algorithm works:
        A priorityQueue, called 'fringe' is used to store paths, with paths with the lowest 'cost' having the highest priority.
        While the finge is not empty, the top path, i.e. the path with the lowest cost is popped off. 
        If that current stop has not yet been visited, it will check if it is the goal. If it is, it will return that path.
        Else, it will add all of those stops neighbours with their current cost + estimated cost to the fringe.
    It is important to store all of the visited edges while this path is being generated, because the estimated cost/heuristic
    could have predicted wrong. I.e. The path that we thought was the shortest at the start, was not the actual shortest path. 
    Therefore, it is important to store all of the edges, so that you can 'backtrack' and check if there are shorter paths
    from different stops along the graph. 

