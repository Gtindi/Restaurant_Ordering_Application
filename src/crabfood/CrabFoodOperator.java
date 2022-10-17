package crabfood;

import crabfood.DeliveryGuy.DeliverySession;
import static crabfood.Main.clock;
import crabfood.MyGoogleMap.Position;
import crabfood.Restaurant.Dish;
import crabfood.Restaurant.RestaurantOrder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

class CrabFoodOperator {

    private static IntegerProperty totalCrabFoodOrder;
    private static ArrayList<CrabFoodOrder> allPresetCrabFoodOrders;
    private static ArrayList<Restaurant> partnerRestaurants;
    private static ArrayList<DeliveryGuy> allDeliveryGuys;
    private static ArrayList<CrabFoodOrder> allCrabFoodOrders;
    private static StringProperty log;
    private static StringProperty process;
    private static MyGoogleMap masterMap;

    public CrabFoodOperator() {
        CrabFoodOperator.totalCrabFoodOrder = new SimpleIntegerProperty(0);

        // set partner partner restaurants (read now & update later)
        CrabFoodOperator.partnerRestaurants = new ArrayList<>();
        readPartnerRestaurants();

        // set & update master map (read now & update now)
        CrabFoodOperator.masterMap = new MyGoogleMap();
        masterMap.updateMap();

        // set all delivery guys (read now & update later)
        CrabFoodOperator.allDeliveryGuys = new ArrayList<>();
        readAllDeliveryGuys();

        // set all preset CrabFood orders
        CrabFoodOperator.allPresetCrabFoodOrders = new ArrayList<>();
        readAllPresetCrabFoodOrders();

        // initalize CrabFood orders
        CrabFoodOperator.allCrabFoodOrders = new ArrayList<>();

        // set log (read now & update later)
        log = new SimpleStringProperty("");
        readLog();

        // set process (update later)
        process = new SimpleStringProperty("");
    }

    /**
     * Allocate CrabFood orders to delivery men by their empty slots
     */
    public static void allocateDeliveryByFinishTime(CrabFoodOrder cfOrder) {
        // find earliest delivery end time among all delivery guys
        String earliest = "23:59";
        for (DeliveryGuy deliveryGuy : CrabFoodOperator.getAllDeliveryGuys()) {
            if (deliveryGuy.getAllDeliverySession().isEmpty()) {
                earliest = clock.getTime();
                break;
            } else {
                String deliveryEndTime = deliveryGuy.getAllDeliverySession().get(deliveryGuy.getAllDeliverySession().size() - 1).getDeliveryEndTime();
                if (SimulatedTime.compareStringTime(earliest, deliveryEndTime) > 0) {
                    earliest = deliveryGuy.getAllDeliverySession().get(deliveryGuy.getAllDeliverySession().size() - 1).getDeliveryEndTime();
                }
            }
        }

        // find delivery men with earliest delivery end time
        ArrayList<DeliveryGuy> menWithEarliest = new ArrayList<>();
        if (earliest.equals(clock.getTime())) {
            for (DeliveryGuy deliveryGuy : CrabFoodOperator.getAllDeliveryGuys()) {
                if (deliveryGuy.getAllDeliverySession().isEmpty()) {
                    menWithEarliest.add(deliveryGuy);
                }
            }
        } else {
            for (DeliveryGuy deliveryGuy : CrabFoodOperator.getAllDeliveryGuys()) {
                String prevDeliveryEndTime = deliveryGuy.getAllDeliverySession().get(deliveryGuy.getAllDeliverySession().size() - 1).getDeliveryEndTime();
                if (earliest.equals(prevDeliveryEndTime)) {
                    menWithEarliest.add(deliveryGuy);
                }
            }
        }

        // if more than 1 man has earliest delivery end time, allocate delivery by closest distance ONLY to these men
        if (menWithEarliest.size() > 1) {
            CrabFoodOperator.allocateDeliveryByDistance(menWithEarliest, cfOrder);
        } else {
            // only 1 man has earliest delivery end time
            DeliveryGuy theGuy = null;
            if (earliest.equals(clock.getTime())) {
                for (DeliveryGuy deliveryGuy : CrabFoodOperator.getAllDeliveryGuys()) {
                    if (deliveryGuy.getAllDeliverySession().isEmpty()) {
                        theGuy = deliveryGuy;
                        break;
                    }
                }
            } else {
                for (DeliveryGuy deliveryGuy : CrabFoodOperator.getAllDeliveryGuys()) {
                    String prevDeliveryEndTime = deliveryGuy.getAllDeliverySession().get(deliveryGuy.getAllDeliverySession().size() - 1).getDeliveryEndTime();
                    if (earliest.equals(prevDeliveryEndTime)) {
                        theGuy = deliveryGuy;
                        break;
                    }
                }
            }

            int goToBranchDuration = 0;
            if (theGuy.getAllDeliverySession().size() >= 1) {
                goToBranchDuration = MyGoogleMap.getTravelDuration(
                        theGuy.getAllDeliverySession().get(theGuy.getAllDeliverySession().size() - 1).getDeliveryEndPosition(),
                        cfOrder.getBranchLocation());
            } else {
                goToBranchDuration = MyGoogleMap.getTravelDuration(theGuy.getCurrentPosition(), cfOrder.getBranchLocation());
            }
            int deliverDuration = MyGoogleMap.getTravelDuration(cfOrder.getBranchLocation(), cfOrder.getDeliveryLocation());
            String startTime = SimulatedTime.getTimeAfter(earliest, goToBranchDuration);
            String endTime = SimulatedTime.getTimeAfter(startTime, deliverDuration);
            theGuy.getAllDeliverySession().add(new DeliverySession(cfOrder, startTime, endTime));

            // update process
            CrabFoodOperator.appendToProcess(String.format("Delivery man %d at %s takes the order of customer %d at branch of %s at %s.",
                    theGuy.getDeliveryGuyId(),
                    theGuy.getCurrentPosition(),
                    cfOrder.getCustomerId(),
                    cfOrder.getRestaurantName(),
                    cfOrder.getBranchLocation().toString()));

        }
    }

    /**
     * Allocate CrabFood orders to Delivery Men by their distance with branch &
     * customer
     *
     * @param guys List of delivery guys with same previous order finish
     * delivery time
     * @param order Order to be allocated to one of these delivery guys
     */
    public static void allocateDeliveryByDistance(ArrayList<DeliveryGuy> guys, CrabFoodOrder cfOrder) {
        // get min distance for delivery guy to get to restaurant branch & then customer
        int minDistance = Integer.MAX_VALUE;
        for (DeliveryGuy guy : guys) {
            int distance = MyGoogleMap.getDistance(guy.getCurrentPosition(), cfOrder.getBranchLocation());
            minDistance = minDistance > distance ? distance : minDistance;
        }

        // allocate to first delivery guy with min distance
        for (DeliveryGuy deliveryGuy : guys) {
            int distance = MyGoogleMap.getDistance(deliveryGuy.getCurrentPosition(), cfOrder.getBranchLocation());
            if (minDistance == distance) {
                String earliest = deliveryGuy.getAllDeliverySession().isEmpty()
                        ? clock.getTime()
                        : deliveryGuy.getAllDeliverySession().get(deliveryGuy.getAllDeliverySession().size() - 1).getDeliveryEndTime();

                int goToBranchDuration = MyGoogleMap.getTravelDuration(deliveryGuy.getCurrentPosition(), cfOrder.getBranchLocation());
                int deliverDuration = MyGoogleMap.getTravelDuration(cfOrder.getBranchLocation(), cfOrder.getDeliveryLocation());

                String startTime = SimulatedTime.getTimeAfter(earliest, goToBranchDuration);
                String endTime = SimulatedTime.getTimeAfter(startTime, deliverDuration);

                deliveryGuy.getAllDeliverySession().add(new DeliverySession(cfOrder, startTime, endTime));

                // update process
                CrabFoodOperator.appendToProcess(String.format("Delivery man %d at %s takes the order of customer %d at branch of %s at %s.",
                        deliveryGuy.getDeliveryGuyId(),
                        deliveryGuy.getCurrentPosition(),
                        cfOrder.getCustomerId(),
                        cfOrder.getRestaurantName(),
                        cfOrder.getBranchLocation().toString()));

                break;
            }
        }
    }

    /**
     * Allocate CrabFood orders to restaurants by distance
     */
    public static void allocateOrderByDistance(CrabFoodOrder cfOrder) {
        // find closest branch
        int smallestDistance = Integer.MAX_VALUE;
        if (!CrabFoodOperator.getPartnerRestaurants().isEmpty()) {
            for (Restaurant restaurant : CrabFoodOperator.getPartnerRestaurants()) {
                if (cfOrder.getRestaurantName().equals(restaurant.getName())) {
                    int distance = MyGoogleMap.getDistance(cfOrder.getDeliveryLocation(), restaurant.getPosition());
                    smallestDistance = smallestDistance > distance ? distance : smallestDistance;
                }
            }
        }

        // allocate to closest branch
        if (!CrabFoodOperator.getPartnerRestaurants().isEmpty()) {
            for (Restaurant restaurant : CrabFoodOperator.getPartnerRestaurants()) {
                if (cfOrder.getRestaurantName().equals(restaurant.getName())) {
                    int distance = MyGoogleMap.getDistance(cfOrder.getDeliveryLocation(), restaurant.getPosition());
                    if (smallestDistance == distance) {
                        // tell crabfoodorder where will it take its food from
                        cfOrder.setBranchLocation(restaurant.getPosition());

                        // make allocation
                        String startTime = restaurant.getNextOrderStartPrepTime();
                        String endTime = SimulatedTime.getTimeAfter(startTime, cfOrder.getCookTime());
//                    System.out.println(startTime + " " + endTime);
                        restaurant.getAllRestaurantOrders().add(restaurant.new RestaurantOrder(startTime,
                                endTime,
                                cfOrder.getCustomerId()));

                        // update process
                        CrabFoodOperator.appendToProcess(String.format("Branch of %s at %s takes the order.",
                                cfOrder.getRestaurantName(),
                                cfOrder.getBranchLocation()));

                        break;
                        /**
                         * if one or more branch have same distance, maybe we
                         * could allocate by time, but for now, just break loop
                         */
                    }
                }
            }
        }
    }

    /**
     * Add new strings to process
     *
     * @param lineToAppend
     */
    public static void appendToProcess(String lineToAppend) {
        // append internally to log
        process.set(process.concat(clock.getTime() + " " + lineToAppend).get() + "\n");
    }

    /**
     * load previously saved "log.txt"
     */
    public static void readLog() {
        try {
            Scanner s = new Scanner(new FileInputStream("crabfood-io/log.txt"));
            while (s.hasNextLine()) {
                log.set(log.concat(s.nextLine() + "\n").get());
            }
        } catch (FileNotFoundException ex) {
            System.out.println("\"log.txt\" not found.");
        }
    }

    /**
     * Add new strings to log file
     *
     * @param lineToAppend
     */
    public static void appendToLog(String lineToAppend) {
        // append internally to log
        log.set(log.concat(lineToAppend + "\n").get());

        // append externally to "log.txt"
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileOutputStream("crabfood-io/log.txt", true));
            pw.print(lineToAppend);
        } catch (FileNotFoundException ex) {
            System.out.println("\"log.txt\" not found.");
        } finally {
            pw.close();
        }
    }

    /**
     * update partner restaurants to "partner-restaurant.txt"
     */
    public static void updatePartnerRestaurants() {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileOutputStream("crabfood-io/partner-restaurant.txt"));
            ArrayList<String> printedRes = new ArrayList<>();
            for (Restaurant restaurant : CrabFoodOperator.getPartnerRestaurants()) {
                if (!printedRes.contains(restaurant.getName())) {
                    printedRes.add(restaurant.getName());
                    pw.println(restaurant.getName());
                    pw.print(Restaurant.toTxtPositions(restaurant.getName()));
                    pw.print(Restaurant.toTxtDishes(restaurant.getName()) + "\n");
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("\"partner-restaurant.txt\" not found.");
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }

    /**
     * load previously saved "partner-restaurant.txt"
     */
    public static void readPartnerRestaurants() {
        if (!CrabFoodOperator.getPartnerRestaurants().isEmpty()) {
            CrabFoodOperator.getPartnerRestaurants().clear();
        }

        try {
            Scanner s = new Scanner(new FileInputStream("crabfood-io/partner-restaurant.txt"));

            while (s.hasNextLine()) {
                Restaurant restaurant = new Restaurant();

                // read restaurant name
                String restaurantName = s.nextLine();

                // read restaurant map symbol
                Character restaurantMapSymbol = restaurantName.charAt(0);

                // read restaurant positions & dishes
                ArrayList<Position> restaurantPositions = new ArrayList<>();
                ArrayList<Dish> dishes = new ArrayList<>();
                int posCount = 0;
                while (s.hasNextLine()) {
                    String input = s.nextLine();

                    if (Pattern.matches("(\\s)*([0-9])+(\\s)+([0-9])+(\\s)*", input)) {
                        String[] coordinateStr = input.trim().split("\\s");
                        int posX = Integer.parseInt(coordinateStr[0]);
                        int posY = Integer.parseInt(coordinateStr[1]);
                        restaurantPositions.add(new Position(posX, posY));
                        posCount++;
                    } else {
                        if (!input.isEmpty()) {
                            dishes.add(new Dish(input, Integer.parseInt(s.nextLine())));
                        } else {
                            break;
                        }
                    }
                }

                // after reading, set name, map symbol, positions & dishes
                for (int i = 0; i < posCount; i++) {
                    partnerRestaurants.add(new Restaurant(restaurantName,
                            restaurantMapSymbol, restaurantPositions.get(i),
                            (ArrayList<Dish>) dishes.clone()));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("\"partner-restaurant.txt\" not found.");
        }
    }

    /**
     * load previously saved "delivery-guy.txt"
     */
    public static void readAllDeliveryGuys() {
        if (!CrabFoodOperator.getAllDeliveryGuys().isEmpty()) {
            CrabFoodOperator.getAllDeliveryGuys().clear();
        }

        try {
            Scanner s = new Scanner(new FileInputStream("crabfood-io/delivery-guy.txt"));

            int numDeliveryGuy = 0;
            while (s.hasNextInt()) {
                numDeliveryGuy = s.nextInt();
            }

            for (int i = 1; i <= numDeliveryGuy; i++) {
                DeliveryGuy deliveryGuy = new DeliveryGuy(i);
                allDeliveryGuys.add(deliveryGuy);
            }

            DeliveryGuy.initPosition();
        } catch (FileNotFoundException ex) {
            System.out.println("\"delivery-guy.txt\" not found.");
        }
    }

    /**
     * Update "delivery-guy.txt"
     */
    public static void updateAllDeliveryGuys() {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileOutputStream("crabfood-io/delivery-guy.txt"));
            pw.print(allDeliveryGuys.size());
            clock.resetTime();
        } catch (FileNotFoundException ex) {
            System.out.println("\"log.txt\" not found.");
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }

    /**
     * Load preset CrabFood orders from "crabfood-order.txt" Time must be sorted
     */
    public static void readAllPresetCrabFoodOrders() {
        if (!CrabFoodOperator.getAllPresetCrabFoodOrders().isEmpty()) {
            CrabFoodOperator.getAllPresetCrabFoodOrders().clear();
        }

        try {
            Scanner s = new Scanner(new FileInputStream("crabfood-io/preset-crabfood-order.txt"));

            while (s.hasNextLine()) {
                CrabFoodOrder crabFoodOrder = new CrabFoodOrder();

                // read order time
                String orderTime = SimulatedTime.parseTimeToString(s.nextLine());

                // read restaurant
                String restaurantName = s.nextLine();

                // read dish orders & quantity
                HashMap<String, Integer> dishOrders = new HashMap<>();

                // read delivery location
                Position deliveryLocation = new Position();

                while (s.hasNextLine()) {
                    String input = s.nextLine();
                    if (Pattern.matches("((\\s)*[A-Za-z]+(\\s)*)+((\\s)+[0-9]+(\\s)*)$", input)) {
                        String dishName = input.replaceFirst("[0-9]+(\\s)*$", "").trim();
                        int quanitity = Integer.parseInt(input.replaceAll("\\D+", ""));

                        dishOrders.put(dishName, quanitity);
                    } else if (Pattern.matches("((\\s)*[A-Za-z]+(\\s)*)+", input)) {
                        dishOrders.put(input.trim(), 1);
                    } else if (Pattern.matches("(\\s)*([0-9])+(\\s)+([0-9])+(\\s)*", input)) {
                        String[] coordinateStr = input.trim().split("\\s");
                        int posX = Integer.parseInt(coordinateStr[0]);
                        int posY = Integer.parseInt(coordinateStr[1]);
                        deliveryLocation.setPosition(posX, posY);
                    } else if (input.isEmpty()) {
                        break;
                    }
                }

                crabFoodOrder.setRestaurantName(restaurantName);
                crabFoodOrder.setDishOrders(dishOrders);
                crabFoodOrder.setDeliveryLocation(deliveryLocation);
                crabFoodOrder.setCookTime(crabFoodOrder.calculateCookTime());
                crabFoodOrder.setOrderTime(orderTime);

                allPresetCrabFoodOrders.add(crabFoodOrder);
            }
        } catch (FileNotFoundException e) {
            System.out.println("\"crabfood-order.txt\" not found.");
        }
    }

    /**
     * sort CrabFood orders whenever any new order is inserted
     */
    public static void sortCfOrders() {
        if (!allCrabFoodOrders.isEmpty()) {
            // sort allCrabFoodOrders by order time 
            ArrayList<String> timeList = new ArrayList<>();
            for (CrabFoodOrder cfOrder : allCrabFoodOrders) {
                if (!timeList.contains(cfOrder.getOrderTime())) {
                    timeList.add(cfOrder.getOrderTime());
                }
            }
            CrabFoodOperator.selectionSort(timeList);

            ArrayList<CrabFoodOrder> sortedCfOrders = new ArrayList<>();
            Iterator itrTimeList = timeList.iterator();
            while (itrTimeList.hasNext()) {
                String time = (String) itrTimeList.next();
                for (CrabFoodOrder cfOrder : allCrabFoodOrders) {
                    if (cfOrder.getOrderTime().equals(time)) {
                        sortedCfOrders.add(cfOrder);
                    }
                }
                itrTimeList.remove();
            }

            allCrabFoodOrders.clear();
            allCrabFoodOrders = sortedCfOrders;

            // rearrange customerId according to order time
            ArrayList<Integer> sortedCusId = new ArrayList<>();
            for (CrabFoodOrder cfOrder : allCrabFoodOrders) {
                sortedCusId.add(cfOrder.getCustomerId());
            }

            Collections.sort(sortedCusId);

            int i = 0;
            for (CrabFoodOrder cfOrder : allCrabFoodOrders) {
                cfOrder.setCustomerId(sortedCusId.get(i));
                i++;
            }
        }
    }

    public static void selectionSort(ArrayList<String> timeList) {
        for (int i = 0; i < timeList.size() - 1; i++) {
            // find min in the list[i...list.length-1]
            String currentMin = timeList.get(i);
            int currentMinIndex = i;
            for (int j = i + 1; j < timeList.size(); j++) {
                if (SimulatedTime.compareStringTime(currentMin, timeList.get(j)) > 0) {
                    currentMin = timeList.get(j);
                    currentMinIndex = j;
                }
            }

            // swap timeList[i] with timeList[currentMinIndex] if necessary;
            if (currentMinIndex != i) {
                timeList.set(currentMinIndex, timeList.get(i));
                timeList.set(i, currentMin);
            }
        }
    }

    public static IntegerProperty getTotalCrabFoodOrder() {
        return totalCrabFoodOrder;
    }

    public static void setTotalCrabFoodOrder(IntegerProperty totalCrabFoodOrder) {
        CrabFoodOperator.totalCrabFoodOrder = totalCrabFoodOrder;
    }

    public static ArrayList<CrabFoodOrder> getAllPresetCrabFoodOrders() {
        return allPresetCrabFoodOrders;
    }

    public static void setAllPresetCrabFoodOrders(ArrayList<CrabFoodOrder> allPresetCrabFoodOrders) {
        CrabFoodOperator.allPresetCrabFoodOrders = allPresetCrabFoodOrders;
    }

    public static StringProperty getProcess() {
        return process;
    }

    public static void setProcess(StringProperty process) {
        CrabFoodOperator.process = process;
    }

    public static StringProperty getLog() {
        return log;
    }

    public static void setLog(StringProperty log) {
        CrabFoodOperator.log = log;
    }

    public static MyGoogleMap getMasterMap() {
        return masterMap;
    }

    public static void setMasterMap(MyGoogleMap masterMap) {
        CrabFoodOperator.masterMap = masterMap;
    }

    public static ArrayList<Restaurant> getPartnerRestaurants() {
        return partnerRestaurants;
    }

    public static void setPartnerRestaurants(ArrayList<Restaurant> partnerRestaurants) {
        CrabFoodOperator.partnerRestaurants = partnerRestaurants;
    }

    public static ArrayList<CrabFoodOrder> getAllCrabFoodOrders() {
        return allCrabFoodOrders;
    }

    public static void setAllCrabFoodOrders(ArrayList<CrabFoodOrder> allCrabFoodOrders) {
        CrabFoodOperator.allCrabFoodOrders = allCrabFoodOrders;
    }

    public static ArrayList<DeliveryGuy> getAllDeliveryGuys() {
        return allDeliveryGuys;
    }

    public static void setAllDeliveryGuys(ArrayList<DeliveryGuy> allDeliveryGuys) {
        CrabFoodOperator.allDeliveryGuys = allDeliveryGuys;
    }

    public static class CrabFoodOrder {

        // if not stated "later", then set upon creation
        private Integer customerId; // set later
        private String orderTime;
        private String restaurantName;
        private HashMap<String, Integer> dishOrders;
        private Position deliveryLocation;
        private Position branchLocation; // set later
        private StringProperty status; // set later
        private int cookTime;

        public CrabFoodOrder(String restaurantName, HashMap<String, Integer> dishOrders, Position deliveryLocation) {
            this.customerId = -1;
            this.orderTime = clock.getTime();
            this.restaurantName = restaurantName;
            this.dishOrders = dishOrders;
            this.deliveryLocation = deliveryLocation;
            this.branchLocation = new Position(0, 0);
            this.status = new SimpleStringProperty("no status");
            this.cookTime = -1;
        }

        public CrabFoodOrder() {
            this.customerId = -1;
            this.orderTime = clock.getTime();
            this.restaurantName = "no name";
            this.dishOrders = new HashMap<>();
            this.deliveryLocation = new Position(0, 0);
            this.branchLocation = new Position(0, 0);
            this.status = new SimpleStringProperty("no status");
            this.cookTime = -1;
        }

        public Position getBranchLocation() {
            return branchLocation;
        }

        public void setBranchLocation(Position branchLocation) {
            this.branchLocation = branchLocation;
        }

        public StringProperty getStatus() {
            return status;
        }

        public void setStatus(StringProperty status) {
            this.status = status;
        }

        public int calculateCookTime() {
            int duration = -1;
            for (Restaurant restaurant : CrabFoodOperator.getPartnerRestaurants()) {
                if (restaurantName.trim().equals(restaurant.getName().trim())) {
                    for (Map.Entry dish : dishOrders.entrySet()) {
                        int timeNeeded = restaurant.getCookTime(dish.getKey().toString()) * Integer.parseInt(dish.getValue().toString());
                        if (duration < timeNeeded) {
                            duration = timeNeeded;
                        }
                        duration = duration < timeNeeded ? timeNeeded : duration;
                    }
                    break;
                }
            }
            return duration;
        }

        public int getCookTime() {
            return cookTime;
        }

        public void setCookTime(int cookTime) {
            this.cookTime = cookTime;
        }

        public String toString() {
            String result = "";
            result += customerId + "\n";
            result += orderTime + "\n";
            result += restaurantName + "\n";
            for (Map.Entry<String, Integer> entry : dishOrders.entrySet()) {
                result += entry.getKey() + " ";
                result += entry.getValue() + "\n";
            }
            result += deliveryLocation + "\n";
            return result;
        }

        /**
         * Just in case if need to write to "preset-crabfood-order.txt"
         *
         * @return CrabFoodOrder string
         */
        public String toTxtString() {
            String result = "";
            result += orderTime + "\n";
            result += restaurantName + "\n";
            for (Map.Entry<String, Integer> entry : dishOrders.entrySet()) {
                result += entry.getKey() + " ";
                result += entry.getValue() + "\n";
            }
            result += deliveryLocation + "\n";
            return result;
        }

        public Integer getCustomerId() {
            return customerId;
        }

        public void setCustomerId(Integer customerId) {
            this.customerId = customerId;
        }

        public String getOrderTime() {
            return orderTime;
        }

        public void setOrderTime(String orderTime) {
            this.orderTime = orderTime;
        }

        public String getRestaurantName() {
            return restaurantName;
        }

        public void setRestaurantName(String restaurantName) {
            this.restaurantName = restaurantName;
        }

        public HashMap<String, Integer> getDishOrders() {
            return dishOrders;
        }

        public void setDishOrders(HashMap<String, Integer> dishOrders) {
            this.dishOrders = dishOrders;
        }

        public Position getDeliveryLocation() {
            return deliveryLocation;
        }

        public void setDeliveryLocation(Position deliveryLocation) {
            this.deliveryLocation = deliveryLocation;
        }

    }
}
