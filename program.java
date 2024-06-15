import java.util.*;

public class program {
    public static void main(String[] args) {
        // Hash Maps of Hash Maps where, each key is a city, and the Value is a Map of Cities and Distances to those Cities
        // OuterMap<City1, InnerMap>
        // OuterMap<City1, Map<City2, (Distance to that City)>> 
        Map<String, Map<String, Integer>> outerMap = new HashMap<>();
        Map<String, Integer> heuristicMap = new HashMap<>();
        int[] heuristicVal = new int[7];

        // User defines start and goal
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter start city: ");
        String startCity = scanner.nextLine();
        System.out.print("Enter goal city: ");
        String goalCity = scanner.nextLine();

        // User enters heuristic values
        System.out.println("\nEnter heuristic values (Goal must be zero):");
        System.out.print("Miami to " + goalCity + ": ");
        heuristicVal[0] = scanner.nextInt();
        System.out.print("New York to " + goalCity + ": ");
        heuristicVal[1] = scanner.nextInt();
        System.out.print("Boston to " + goalCity + ": ");
        heuristicVal[2] = scanner.nextInt();
        System.out.print("Dallas to " + goalCity + ": ");
        heuristicVal[3] = scanner.nextInt();
        System.out.print("San Francisco to " + goalCity + ": ");
        heuristicVal[4] = scanner.nextInt();
        System.out.print("Los Angeles to " + goalCity + ": ");
        heuristicVal[5] = scanner.nextInt();
        System.out.print("Chicago to " + goalCity + ": ");
        heuristicVal[6] = scanner.nextInt();

        // Create maps
        createMap(outerMap, heuristicMap, heuristicVal);

        // //debugging
        // for (Map.Entry<String, Integer> entry : heuristicMap.entrySet()) {
        //     String city = entry.getKey();
        //     Integer heuristicValue = entry.getValue();
        //     System.out.println("City: " + city + ", Heuristic value: " + heuristicValue);
        // }
        // //debugging ^

        // execute BFS
        List<String> pathStartToFinish = breadthFirstSearch(outerMap, startCity, goalCity);
        if (pathStartToFinish.isEmpty()){
            System.out.println("\nNo path exists from " + startCity + " to " + goalCity);
        }
        else {
            System.out.println("\n[BFS] Path from " + startCity + " to " + goalCity + ":" + pathStartToFinish);
            int BFScost = calculateTotalPathCost(pathStartToFinish, outerMap);
            System.out.println("Total path cost: " + BFScost);
        }
            
        // execute Greedy best-first search
        List<String> pathStartToFinishGreedy = greedyBestFirstSearch(outerMap, heuristicMap, startCity, goalCity);
        if (pathStartToFinishGreedy.isEmpty()) {
            System.out.println("\nNo path exists from " + startCity + " to " + goalCity);
        } 
        else { 
            System.out.println("\n[Greedy] Path from " + startCity + " to " + goalCity + ": " + pathStartToFinishGreedy);
            int Greedycost = calculateTotalPathCost(pathStartToFinishGreedy, outerMap);
            System.out.println("Total path cost: " + Greedycost);
        }
    }

    //--------------------------------------------------------------------------------------------
    //Utility methods below


    // Turning the map into a graph
    public static void addPath( Map<String, Map<String, Integer>> outerMap,
                                String city1,
                                String city2,
                                int distance) {
        //If city1 doesn't already exist as a key on the outerMap, create it with an empty Hashmap as its value
        //Else, Add city2 to city1's inner map with the distance.
        outerMap.computeIfAbsent(city1, city -> new HashMap<>()).put(city2, distance);
        //Identical^^
        outerMap.computeIfAbsent(city2, city -> new HashMap<>()).put(city1, distance);
    }

    public static void createMap(Map<String, Map<String, Integer>> outerMap, 
                                 Map<String, Integer> heuristicMap,
                                 int[] heuristicVal) {
        // Adding Paths to outerMap
        addPath(outerMap, "Dallas", "Miami", 1200);
        addPath(outerMap, "Dallas", "Los Angeles", 1700);
        addPath(outerMap, "Dallas", "New York", 1500);
        addPath(outerMap, "Los Angeles", "New York", 3000);
        addPath(outerMap, "Los Angeles", "San Francisco", 500);
        addPath(outerMap, "Miami", "New York", 1000);
        addPath(outerMap, "New York", "Boston", 250);
        addPath(outerMap, "New York", "Chicago", 800);
        addPath(outerMap, "San Francisco", "Chicago", 2200);

        //adding heuristic values
        heuristicMap.put("Miami", heuristicVal[0]);
        heuristicMap.put("New York", heuristicVal[1]);
        heuristicMap.put("Boston", heuristicVal[2]);
        heuristicMap.put("Dallas", heuristicVal[3]);
        heuristicMap.put("San Francisco", heuristicVal[4]);
        heuristicMap.put("Los Angeles", heuristicVal[5]);
        heuristicMap.put("Chicago", heuristicVal[6]);
    }

    //Rebuild Optimal Path based on paths taken by bfs
    private static List<String> rebuildPath(Map<String, String> previousCity,
                                            String goalCity) {
        //Start at goal city
        List<String> pathStartToFinish = new ArrayList<>();
        String currentCity = goalCity;

        while (currentCity != null) {
            //Add current city to path and move to previous
            pathStartToFinish.add(currentCity);
            currentCity = previousCity.get(currentCity);
        }

        Collections.reverse(pathStartToFinish);
        return pathStartToFinish;
    }

    //Calculate path cost from the path returned by the search
    public static int calculateTotalPathCost(List<String> path, Map<String, Map<String, Integer>> outerMap) {
        int currentTotal = 0;

        //loops through the cities on the path, gets each city and the city after it
        for(int x = 0; x<path.size()-1; x++){
            String city1 = path.get(x);
            String city2 = path.get(x+1);

            currentTotal += outerMap.get(city1).get(city2); //uses the pair of cities to retrieve the associated path cost from the graph
        }

        return currentTotal;
    }
    
    //--------------------------------------------------------------------------------------------

    //BREADTH FIRST SEARCH
    public static List<String> breadthFirstSearch(  Map<String, Map<String, Integer>> outerMap, 
                                                    String startCity,
                                                    String goalCity) {
    
        Queue<String> cityQueue = new LinkedList<>();
        Set<String> alreadyVisitedCities = new HashSet<>();

        //Track Paths Taken
        Map<String, String> previousCity = new HashMap<>();
        
        cityQueue.offer(startCity);
        alreadyVisitedCities.add(startCity);
        
        while (!cityQueue.isEmpty()) {
            //Set next city in cityQueue as current
            String currentCity = cityQueue.poll();
            if (currentCity.equals(goalCity)) 
                return rebuildPath(previousCity, goalCity);
            
            //Get the cities neighboring the current
            Map<String, Integer> neighborCities = outerMap.get(currentCity);

            //Proceed to next city if no neighbors exist
            if (neighborCities == null) 
                continue;

            //Iterate over each neigboring city using only its name
            for (String neighborCity : neighborCities.keySet()) 
                bfsExploreUnvisitedNeighbors(neighborCity, currentCity, alreadyVisitedCities, cityQueue, previousCity);
            
        }
        //No Path Available
        return Collections.emptyList(); 
    }
    
    public static void bfsExploreUnvisitedNeighbors(String neighborCity,
                                                    String currentCity,
                                                    Set<String> alreadyVisitedCities,
                                                    Queue<String> cityQueue,
                                                    Map<String, String> previousCity) {
        //If neighbor has been visited, skip to next neighbor
        if (alreadyVisitedCities.contains(neighborCity)) 
            return;

        //Explore if Unvisited
        cityQueue.offer(neighborCity);
        alreadyVisitedCities.add(neighborCity);

        previousCity.put(neighborCity, currentCity);
    }

    //--------------------------------------------------------------------------------------------

    //GREEDY BEST FIRST SEARCH
    public static List<String> greedyBestFirstSearch(Map<String, Map<String, Integer>> outerMap, Map<String, Integer> heuristicMap, String start, String goal){
        PriorityQueue<String> availableList = new PriorityQueue<>(Comparator.comparingInt(heuristicMap::get)); //min-heap to store available cities
        Set<String> visitedList = new HashSet<>(); //stores expanded cities
        Map<String, String> previousCityPath = new HashMap<>(); //stores the path taken per to reach a city

        availableList.add(start); //The starting city is the only expandable node, add it to the availableList

        //Loops as long as there are available cities to expand
        while(!availableList.isEmpty()){
            String thisCity = availableList.poll(); //gets the city with the lowest heuristic value (since it's a min-heap)

            //if the current city is already the goal, rebuild the path and return it
            if(thisCity.equals(goal)){
                return rebuildPath(previousCityPath, thisCity);
            }

            visitedList.add(thisCity); //add the current city to the visited list

            //iterates through all the neighbors of the current city to add them to the availableList
            for(Map.Entry<String, Integer> neighborCity : outerMap.get(thisCity).entrySet()){
                String neighborCityName = neighborCity.getKey();

                if(visitedList.contains(neighborCityName)){ //if the neighbor has already been visited, ignore it
                    continue;
                }

                if(!visitedList.contains(neighborCityName)){ //if the neighbor is not yet in the available list...
                    previousCityPath.put(neighborCityName, thisCity); //1. record its parent in the path
                    availableList.add(neighborCityName); //2. add it to the list of available cities
                }

            }

        }

        //return an empty list = no path to the goal
        return Collections.emptyList();
    }
}