import java.util.*;

public class bfs {
    public static void main(String[] args) {
        // Hash Maps of Hash Maps where, each key is a city, and the Value is a Map off Cities and Distances to those Cities
        // OuterMap<City1, InnerMap>
        // OuterMap<City1, Map<City2, (Distance to that City)>> 
        Map<String, Map<String, Integer>> outerMap = new HashMap<>();

        // Execute all addPaths()
        createMap(outerMap);

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter start city: ");
        String startCity = scanner.nextLine();
        System.out.print("Enter goal city: ");
        String goalCity = scanner.nextLine();

        // Finding the optimal path from startCity to goalCity using BFS
        List<String> pathStartToFinish = breadthFirstSearch(outerMap, startCity, goalCity);
        if (pathStartToFinish.isEmpty()) 
            System.out.println("\nNo path exists from " + startCity + " to " + goalCity);
         else 
            System.out.println("\nOptimal Path from " + startCity + " to " + goalCity + ":\n" + pathStartToFinish);
        
    }
    
    // Add Paths to outerMap
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

    public static void createMap(Map<String, Map<String, Integer>> outerMap) {
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
    }
    
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
}