package crabfood;

/*
 *The first three lines of code import various classes that are needed for the program to run. The CrabFoodOrder class is used to represent an order from a crab food restaurant. The DeliverySession class is used to manage the delivery of orders. The Position class is used to represent a position on a map.
 *The next three lines of code define some variables that will be used throughout the program. The first variable, order, is an instance of the CrabFoodOrder class. The second variable, deliverySession, is an instance of the DeliverySession class. The third variable, position, is an instance of the Position class.
 *The next four lines of code define a list of dishes that can be ordered from the crab food restaurant. The list is represented by an instance of the ArrayList class. The list is populated with instances of the Dish class.
 *The next line of code defines a map of delivery times for each dish. The map is represented by an instance of the HashMap class. The map is populated with key-value pairs, where the key is a dish and the value is the delivery time for that dish.
 *The next line of code defines an observable list of orders. The list is represented by an instance of the ObservableList class. The list is populated with instances of the CrabFoodOrder class.
 *The next line of code defines an observable map of delivery times. The map is represented by an instance of the ObservableMap class. The map is populated with key-value pairs, where the key is a dish and the value is the delivery time for that dish.
 *The next line of code defines an integer property that represents the number of orders in the list. The property is represented by an instance of the IntegerProperty class.
 *The next line of code defines a string property that represents the list of orders as a string. The property is represented by an instance of the StringProperty class.
 *The next line of code defines a change listener that is used to update the string property when the list of orders changes. The listener is an instance of the ChangeListener class.
 *The next line of code defines a list change listener that is used to update the integer property when the list of orders changes. The listener is an instance of the ListChangeListener class.
 *The next line of code defines a map change listener that is used to update the delivery times when the map of delivery times changes. The listener is an instance of the MapChangeListener class.
 *The next line of code defines a button that is used to add an order to the list. The button is an instance of the Button class.
 *The next line of code defines a label that is used to display the number of orders in the list. The label is an instance of the Label class.
 *The next line of code defines a list view that is used to display the list of orders. The list view is an instance of the ListView class.
 *The next line of code defines a scroll pane that is used to scroll the list view. The scroll pane is an instance of the ScrollPane class.
 *The next line of code defines a combo box that is used to select a dish from the list of dishes. The combo box is an instance of the ComboBox class.
 *The next two lines of code set the alignment and padding for the button.
 *The next two lines of code set the alignment and padding for the label.
 *The next two lines of code set the alignment and padding for the list view.
 *The next two lines of code set the alignment and padding for the scroll pane.
 *The next two lines of code set the alignment and padding for the combo box.
 *The next line of code adds the button, label, list view, scroll pane, and combo box to the scene.
 *The next line of code sets the title of the stage.
 *The next line of code sets the scene of the stage.
 *The next line of code shows the stage.
 *The next line of code waits for the stage to be closed before exiting the program. 
 */

import crabfood.CrabFoodOperator.CrabFoodOrder;
import crabfood.DeliveryGuy.DeliverySession;
import crabfood.MyGoogleMap.Position;
import crabfood.Restaurant.Dish;
import crabfood.Restaurant.RestaurantOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class Main extends Application {

    IntegerProperty stageHeight = new SimpleIntegerProperty(828);
    IntegerProperty stageWidth = new SimpleIntegerProperty(1500);

    public static void main(String[] args) {
        launch(args);
    }

    // make clock
    public volatile static SimulatedTime clock = new SimulatedTime();

    // make CrabFood operator
    public static CrabFoodOperator operator = new CrabFoodOperator();

    // closing time
    String closeTime = "21:20";

    // all scenes
    Scene sceneMenu, sceneMR, sceneMD, sceneVOL, sceneSC, sceneVM, sceneER, sceneEDs, sceneED;

    // Menu time stamp
    Text txtTimeStamp = new Text();

    // status table
    TableView tableOS = new TableView();

    // list to update status table
    private ObservableList<CrabFoodOrder> cfOrderList = FXCollections.observableArrayList();

    // Text area to put name of selected restaurant to edit
    TextArea txtareaRestaurantName = new TextArea();

    // Restaurant list in sceneMR
    ObservableList<String> obsListRestaurant = FXCollections.observableArrayList();

    // Restaurant chosen to edit in sceneER
    StringProperty resToEdit = new SimpleStringProperty("");

    // Locations of restaurant chosen to edit in sceneER
    GridPane gridRestaurantLoc = new GridPane();

    // Observable dish list in sceneEDs
    ObservableList<String> obsListDishes = FXCollections.observableArrayList();

    // Actual name dish to edit
    StringProperty dishToEdit = new SimpleStringProperty("");

    // Dish list in makeSceneEDs
    ListView<String> listDishes = new ListView(obsListDishes);

    // Text area to put name of selected dish to edit
    TextArea txtareaDishName = new TextArea();

    // Temporary list of dishes to be added into new restaurant
    ArrayList<Dish> dishesToAddTemp = new ArrayList<>();

    // Spinner to put dish prep time of dish to edit
    Spinner spinnerDishPrepTime = new Spinner(1, 60, 5);

    // Flag to indicate add operation on restaurant
    boolean flagAddRes = false;

    // Flag to indicate add operation on dish
    boolean flagAddDish = false;

    // Button to simulate customer
    Button btnSC = new Button("Simulate Customer");

    // Order time in simulate customer
    Text txtOrderTime = new Text();

    // Grid area for view map
    GridPane gridMap = new GridPane();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Thread timeThread = new Thread(new Runnable() {

            @Override
            public void run() {
                Runnable updater = new Runnable() {

                    @Override
                    public void run() {
                        // things to do when new day comes
                        if (clock.getTime().equals("00:00")) {
                            // new day
                            CrabFoodOperator.getProcess().set("");
                            CrabFoodOperator.appendToProcess("A new day has started!");

                            // reset status table
                            cfOrderList.clear();

                            // new log section
                            String logHeader = String.format("\n\n| %11s | %10s | %21s | %14s | %16s | %16s | %14s | %-20s | %6s ",
                                    "Customer ID", "Order Time", "Finished Cooking Time",
                                    "Delivered Time", "Cooking Duration", "Deliver Duration",
                                    "Total Duration", "Restaurant", "Branch");
                            CrabFoodOperator.appendToLog(logHeader);

                            // enable simulate customer button
                            btnSC.setDisable(false);

                            // partially reset program
                            CrabFoodOperator.getTotalCrabFoodOrder().setValue(0);

                            CrabFoodOperator.getAllPresetCrabFoodOrders().clear();
                            CrabFoodOperator.readAllPresetCrabFoodOrders();

                            CrabFoodOperator.getAllCrabFoodOrders().clear();
                        }

                        if (clock.getTime().equals(closeTime)) {
                            btnSC.setDisable(true);
                            CrabFoodOperator.appendToProcess("CrabFood is done for the day. Stopped accepting new orders.");
                        }

                        if (SimulatedTime.compareStringTime(clock.getTime(), closeTime) < 0) {
                            // check for start of preset CrabFoodOrders to add into main list when time comes
                            if (!CrabFoodOperator.getAllPresetCrabFoodOrders().isEmpty()) {
                                Iterator itrCfOrder = CrabFoodOperator.getAllPresetCrabFoodOrders().iterator();
                                while (itrCfOrder.hasNext()) {
                                    CrabFoodOrder cfOrder = (CrabFoodOrder) itrCfOrder.next();
                                    if (cfOrder.getOrderTime().equals(clock.getTime())) {
                                        // update variables in preset CrabFood order
                                        cfOrder.getStatus().setValue("New order");
                                        cfOrder.setCustomerId(CrabFoodOperator.getTotalCrabFoodOrder().get() + 1);

                                        // update total number of CrabFood orders
                                        CrabFoodOperator.getTotalCrabFoodOrder().set(CrabFoodOperator.getTotalCrabFoodOrder().get() + 1);

                                        // add it to main list of CrabFood orders
                                        CrabFoodOperator.getAllCrabFoodOrders().add(cfOrder);
                                        CrabFoodOperator.sortCfOrders();

                                        // drop it from the preset list
                                        itrCfOrder.remove();
                                    }
                                }
                            }

                            // check for start of CrabFoodOrders (this is the main list)
                            if (!CrabFoodOperator.getAllCrabFoodOrders().isEmpty()) {
                                for (CrabFoodOrder cfOrder : CrabFoodOperator.getAllCrabFoodOrders()) {
                                    if (cfOrder.getOrderTime().equals(clock.getTime())) {
                                        // update process
                                        String processOrder = String.format("Customer %d at %s wants to order ",
                                                cfOrder.getCustomerId(), cfOrder.getDeliveryLocation().toString());
                                        int count = 0;
                                        for (Map.Entry mapElement : cfOrder.getDishOrders().entrySet()) {
                                            if (count == cfOrder.getDishOrders().size() - 1) {
                                                processOrder += cfOrder.getDishOrders().size() == 1 ? "" : " & ";
                                                processOrder += mapElement.getValue() + " " + mapElement.getKey() + " ";
                                            } else {
                                                processOrder += mapElement.getValue() + " " + mapElement.getKey();
                                                processOrder += count == cfOrder.getDishOrders().size() - 2 ? "" : ", ";
                                            }
                                            count++;
                                        }
                                        processOrder += "from " + cfOrder.getRestaurantName() + ".";
                                        CrabFoodOperator.appendToProcess(processOrder);

                                        // allocate order
                                        cfOrder.getStatus().setValue("New order");
                                        CrabFoodOperator.allocateOrderByDistance(cfOrder);

                                        // add to status table
                                        if (!cfOrderList.contains(cfOrder)) {
                                            cfOrderList.add(cfOrder);
                                        }
                                    }
                                }
                            }
                        }

                        // check for start & end of food preparation at all restaurant branches
                        if (!CrabFoodOperator.getPartnerRestaurants().isEmpty()) {
                            for (Restaurant restaurant : CrabFoodOperator.getPartnerRestaurants()) {
                                if (!restaurant.getAllRestaurantOrders().isEmpty()) {
                                    Iterator itrROrder = restaurant.getAllRestaurantOrders().iterator();
                                    while (itrROrder.hasNext()) {
                                        RestaurantOrder rOrder = (RestaurantOrder) itrROrder.next();
                                        if (rOrder.getStartTime().equals(clock.getTime())) {
                                            // update process
                                            CrabFoodOperator.appendToProcess(
                                                    String.format("Branch of %s at %s starts preparing order of customer %d.",
                                                            restaurant.getName(),
                                                            restaurant.getPosition(),
                                                            rOrder.getCustomerId()));

                                            // update status table
                                            if (!CrabFoodOperator.getAllCrabFoodOrders().isEmpty()) {
                                                for (CrabFoodOrder cfOrder : CrabFoodOperator.getAllCrabFoodOrders()) {
                                                    if (cfOrder.getCustomerId() == rOrder.getCustomerId()) {
                                                        cfOrder.getStatus().setValue("Preparing...");
                                                    }
                                                }
                                            }
                                        } else if (rOrder.getEndTime().equals(clock.getTime())) {
                                            // remove restaurant order
                                            itrROrder.remove();

                                            // update process
                                            CrabFoodOperator.appendToProcess(
                                                    String.format("Branch of %s at %s finishes preparing order of customer %d.",
                                                            restaurant.getName(),
                                                            restaurant.getPosition(),
                                                            rOrder.getCustomerId()));

                                            // pass to delivery man (via delivery man allocating algo)
                                            for (CrabFoodOrder cfOrder : CrabFoodOperator.getAllCrabFoodOrders()) {
                                                if (cfOrder.getCustomerId() == rOrder.getCustomerId()) {
                                                    CrabFoodOperator.allocateDeliveryByFinishTime(cfOrder);
                                                }
                                            }

                                            // update status table
                                            if (!CrabFoodOperator.getAllCrabFoodOrders().isEmpty()) {
                                                for (CrabFoodOrder cfOrder : CrabFoodOperator.getAllCrabFoodOrders()) {
                                                    if (cfOrder.getCustomerId() == rOrder.getCustomerId()) {
                                                        cfOrder.getStatus().setValue("Prepared");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // check for start & end of delivery at all delivery guys
                        if (!CrabFoodOperator.getAllDeliveryGuys().isEmpty()) {
                            for (DeliveryGuy deliveryGuy : CrabFoodOperator.getAllDeliveryGuys()) {
                                if (!deliveryGuy.getAllDeliverySession().isEmpty()) {
                                    Iterator itrSession = deliveryGuy.getAllDeliverySession().iterator();
                                    while (itrSession.hasNext()) {
                                        DeliverySession session = (DeliverySession) itrSession.next();
                                        if (session.getDeliveryStartTime().equals(clock.getTime())) {
                                            // update process
                                            CrabFoodOperator.appendToProcess(
                                                    String.format("Delivery man %d at %s starts delivering order to customer %d at %s.",
                                                            deliveryGuy.getDeliveryGuyId(),
                                                            deliveryGuy.getCurrentPosition().toString(),
                                                            session.getCrabFoodOrderTBD().getCustomerId(),
                                                            session.getDeliveryEndPosition()));

                                            // update status table
                                            if (!CrabFoodOperator.getAllCrabFoodOrders().isEmpty()) {
                                                for (CrabFoodOrder cfOrder : CrabFoodOperator.getAllCrabFoodOrders()) {
                                                    if (cfOrder.getCustomerId() == session.getCrabFoodOrderTBD().getCustomerId()) {
                                                        cfOrder.getStatus().setValue("Delivering...");
                                                    }
                                                }
                                            }
                                        } else if (session.getDeliveryEndTime().equals(clock.getTime())) {
                                            CrabFoodOrder endCfOrder = session.getCrabFoodOrderTBD();

                                            // update process
                                            CrabFoodOperator.appendToProcess(
                                                    String.format("Delivery man %d at %s finishes delivering order to customer %d at %s.",
                                                            deliveryGuy.getDeliveryGuyId(),
                                                            deliveryGuy.getCurrentPosition().toString(),
                                                            session.getCrabFoodOrderTBD().getCustomerId(),
                                                            session.getDeliveryEndPosition()));

                                            // update status table
                                            if (!CrabFoodOperator.getAllCrabFoodOrders().isEmpty()) {
                                                for (CrabFoodOrder cfOrder : CrabFoodOperator.getAllCrabFoodOrders()) {
                                                    if (cfOrder.getCustomerId().equals(endCfOrder.getCustomerId())) {
                                                        cfOrder.getStatus().setValue("Delivered");
                                                    }
                                                }
                                            }

                                            // key in to log 
                                            CrabFoodOperator.appendToLog(
                                                    String.format("\n| %-11s | %-10s | %-21s | %-14s | %-16s | %-16s | %-14s | %-20s | %-6s ",
                                                            endCfOrder.getCustomerId(), endCfOrder.getOrderTime(),
                                                            SimulatedTime.getTimeAfter(endCfOrder.getOrderTime(), endCfOrder.getCookTime()),
                                                            session.getDeliveryEndTime(), endCfOrder.getCookTime(),
                                                            session.getDeliveryDuration(),
                                                            SimulatedTime.differenceTime(endCfOrder.getOrderTime(), session.getDeliveryEndTime()),
                                                            endCfOrder.getRestaurantName(),
                                                            endCfOrder.getBranchLocation()));

                                            // remove session
                                            itrSession.remove();

                                            // remove from main CrabFood order list
                                            if (!CrabFoodOperator.getAllCrabFoodOrders().isEmpty()) {
                                                Iterator itrAllCfOrders = CrabFoodOperator.getAllCrabFoodOrders().iterator();
                                                while (itrAllCfOrders.hasNext()) {
                                                    CrabFoodOrder cfOrder = (CrabFoodOrder) itrAllCfOrders.next();
                                                    if (cfOrder.getCustomerId().equals(endCfOrder.getCustomerId())) {
                                                        itrAllCfOrders.remove();
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // update menu time stamp
                        txtTimeStamp.setText(clock.getTime());

                        // update order time in simulate customer
                        txtOrderTime.setText(clock.getTime());

                        // make delivery men move
                        DeliveryGuy.updateAllDeliveryGuyPos();

                        // increase 1 second
                        clock.tick();
                    }
                };

                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                    }

                    Platform.runLater(updater);
                }
            }

        });
        timeThread.setDaemon(true);
        timeThread.start();

        // MENU
        makeSceneMenu(primaryStage);

        // MANAGE RESTAURANT
        makeSceneMR(primaryStage);

        // MANAGE DELIVERY
        makeSceneMD(primaryStage);

        // VIEW ORDER LOG
        makeSceneVOL(primaryStage);

        //VIEW MAP
        makeSceneVM(primaryStage);

        // SIMULATE CUSTOMER
        makeSceneSC(primaryStage);

        // EDIT RESTAURANT
        makeSceneER(primaryStage);

        // EDIT DISHES
        makeSceneEDs(primaryStage);

        // EDIT DISH
        makeSceneED(primaryStage);

        // primary stage property
        primaryStage.setMinHeight(876);
        primaryStage.setMinWidth(802);
        primaryStage.setScene(sceneMenu);
        primaryStage.setTitle("CrabFood");
//        primaryStage.setOnCloseRequest(fn -> {});
        primaryStage.show();
    }

    private void makeSceneMenu(Stage primaryStage) {
        // Manage Restaurants, Manage Delivery, View Order Log
        Button btnMR = new Button("Manage Restaurants");
        btnMR.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnMR.setOnAction(fn -> primaryStage.setScene(sceneMR));

        Button btnMD = new Button("Manage Delivery");
        btnMD.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnMD.setOnAction(fn -> primaryStage.setScene(sceneMD));

        Button btnVOL = new Button("View Order Log");
        btnVOL.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnVOL.setOnAction(fn -> primaryStage.setScene(sceneVOL));

        Button btnVM = new Button("View Map");
        btnVM.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnVM.setOnAction(fn -> {
            ArrayList<Position> branchLoc = new ArrayList<>();
            if (!CrabFoodOperator.getPartnerRestaurants().isEmpty()) {
                for (Restaurant restaurant : CrabFoodOperator.getPartnerRestaurants()) {
                    branchLoc.add(restaurant.getPosition());
                }
            }

            for (int i = 0; i < CrabFoodOperator.getMasterMap().getWidth(); i++) {
                for (int j = 0; j < CrabFoodOperator.getMasterMap().getHeight(); j++) {
                    Tile tile = new Tile(String.valueOf(CrabFoodOperator.getMasterMap().getSymbolAt(i, j)));
                    GridPane.setConstraints(tile, i, j);
                    tile.colorTileGrey();
                    gridMap.getChildren().addAll(tile);

                    for (Position pos : branchLoc) {
                        if (i == pos.getPosX() && j == pos.getPosY()) {
                            tile.colorTileLightGrey();
                        }
                    }
                }
            }
            primaryStage.setScene(sceneVM);
        });

        btnSC.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnSC.setOnAction(fn -> primaryStage.setScene(sceneSC));

        txtTimeStamp.setTextAlignment(TextAlignment.CENTER);
        txtTimeStamp.setFont(Font.font("Monospace", 30));

        // #
        VBox layoutMenuLeft = new VBox(10, btnMR, btnMD, btnVOL, btnVM, btnSC, txtTimeStamp);
        layoutMenuLeft.setAlignment(Pos.TOP_CENTER);

        // Process Log
        TextArea txtareaPL = new TextArea();
        txtareaPL.setMinSize(500, 400);
        txtareaPL.setEditable(false);
        txtareaPL.setFont(Font.font("Monospace", 20));
        txtareaPL.textProperty().bind(CrabFoodOperator.getProcess());
        CrabFoodOperator.getProcess().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                txtareaPL.selectPositionCaret(txtareaPL.getLength());
                txtareaPL.deselect();
            }
        });

        // Order Status
        TableColumn<CrabFoodOrder, Integer> colCustomerId = new TableColumn<>("Customer ID");
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));

        TableColumn<CrabFoodOrder, String> colOrderTime = new TableColumn<>("Order Time");
        colOrderTime.setCellValueFactory(new PropertyValueFactory<>("orderTime"));

        TableColumn<CrabFoodOrder, String> colRestaurant = new TableColumn<>("Restaurant");
        colRestaurant.setCellValueFactory(new PropertyValueFactory<>("restaurantName"));

        TableColumn<CrabFoodOrder, Position> colBranch = new TableColumn<>("Branch");
        colBranch.setCellValueFactory(new PropertyValueFactory<>("branchLocation"));

        TableColumn<CrabFoodOrder, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(cd -> cd.getValue().getStatus());

        tableOS.setMinSize(500, 400);
        tableOS.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableOS.getColumns().addAll(colCustomerId, colOrderTime, colRestaurant, colBranch, colStatus);
        tableOS.setItems(cfOrderList);
        cfOrderList.addListener(new ListChangeListener<CrabFoodOrder>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends CrabFoodOrder> c) {
                tableOS.scrollTo(cfOrderList.size() - 1);
            }
        });

        // #
        VBox layoutMenuRight = new VBox(10, txtareaPL, tableOS);
        VBox.setVgrow(txtareaPL, Priority.ALWAYS);
        VBox.setVgrow(tableOS, Priority.ALWAYS);

        // ##
        GridPane layoutMenu = new GridPane();
        GridPane.setVgrow(layoutMenuRight, Priority.ALWAYS);
        GridPane.setHgrow(layoutMenuRight, Priority.ALWAYS);
        GridPane.setConstraints(layoutMenuLeft, 0, 0);
        GridPane.setConstraints(layoutMenuRight, 1, 0);
        layoutMenu.setPadding(new Insets(10, 10, 10, 10));
        layoutMenu.setHgap(10);
        layoutMenu.getChildren().addAll(layoutMenuLeft, layoutMenuRight);

        sceneMenu = new Scene(layoutMenu, stageWidth.getValue(), stageHeight.getValue());
    }

    private void makeSceneMR(Stage primaryStage) {
        // Restaurant List
        ListView listRestaurant = new ListView(obsListRestaurant);
        if (!CrabFoodOperator.getPartnerRestaurants().isEmpty()) {
            for (Restaurant restaurant : CrabFoodOperator.getPartnerRestaurants()) {
                if (!obsListRestaurant.contains(restaurant.getName())) {
                    obsListRestaurant.add(restaurant.getName());
                }
            }
        }

        // Buttons
        Button btnMR_EDIT = new Button("Edit");
        btnMR_EDIT.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnMR_EDIT.setOnAction(fn -> {
            if (listRestaurant.getSelectionModel().getSelectedItem() != null) {
                // record selected restaurant to edit to be passed to edit restaurant
                resToEdit.setValue(listRestaurant.getSelectionModel().getSelectedItem().toString());

                // get ready sceneER (edit name)
                txtareaRestaurantName.setText(resToEdit.getValue());

                // get ready sceneER (edit branch locations)
                ArrayList<Position> branchLoc = new ArrayList<>(); // branch of restaurant to edit
                ArrayList<Position> otherBranchLoc = new ArrayList<>(); // branch of restaurant besides the one to edit
                if (!resToEdit.getValue().equals("") && resToEdit.getValue() != null) {
                    if (!CrabFoodOperator.getPartnerRestaurants().isEmpty()) {
                        for (Restaurant restaurant : CrabFoodOperator.getPartnerRestaurants()) {
                            if (restaurant.getName().equals(resToEdit.getValue())) {
                                branchLoc.add(restaurant.getPosition());
                            } else if (!restaurant.getName().equals(resToEdit.getValue())) {
                                otherBranchLoc.add(restaurant.getPosition());
                            }
                        }
                    }
                }

                gridRestaurantLoc.getChildren().clear();
                for (int i = 0; i < CrabFoodOperator.getMasterMap().getWidth(); i++) {
                    for (int j = 0; j < CrabFoodOperator.getMasterMap().getHeight(); j++) {
                        Tile tile = new Tile(String.valueOf(CrabFoodOperator.getMasterMap().getSymbolAt(i, j)));
                        GridPane.setConstraints(tile, i, j);
                        gridRestaurantLoc.getChildren().addAll(tile);

                        for (Position pos : branchLoc) {
                            if (i == pos.getPosX() && j == pos.getPosY()) {
                                tile.colorTileBlue();
                            }
                        }

                        for (Position pos : otherBranchLoc) {
                            if (i == pos.getPosX() && j == pos.getPosY()) {
                                tile.colorTileGrey();
                            }
                        }
                    }
                }

                branchLoc.clear();
                otherBranchLoc.clear();

                // get ready sceneEDs
                if (!CrabFoodOperator.getPartnerRestaurants().isEmpty()) {
                    for (Restaurant restaurant : CrabFoodOperator.getPartnerRestaurants()) {
                        if (restaurant.getName().equals(resToEdit.getValue())) {
                            if (!restaurant.getAllAvailableDishes().isEmpty()) {
                                for (Dish dish : restaurant.getAllAvailableDishes()) {
                                    obsListDishes.add(dish.getName());
                                }
                            }
                            break;
                        }
                    }
                }

                primaryStage.setScene(sceneER);
            }
        });

        Button btnMR_DELETE = new Button("Delete");
        btnMR_DELETE.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnMR_DELETE.setOnAction(fn -> {
            if (listRestaurant.getSelectionModel().getSelectedItem() != null) {
                // for now, remove only from observable list
                obsListRestaurant.remove(listRestaurant.getSelectionModel().getSelectedItem().toString());
            }
        });

        Button btnMR_ADD = new Button("Add");
        btnMR_ADD.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnMR_ADD.setOnAction(fn -> {
            // clear selected item 
            if (listRestaurant.getSelectionModel().getSelectedItem() != null) {
                listRestaurant.getSelectionModel().clearSelection();
            }

            // get ready sceneER (add branch locations)
            ArrayList<Position> branchLoc = new ArrayList<>(); // branch of restaurant to add
            if (!CrabFoodOperator.getPartnerRestaurants().isEmpty()) {
                for (Restaurant restaurant : CrabFoodOperator.getPartnerRestaurants()) {
                    branchLoc.add(restaurant.getPosition());
                }
            }
            gridRestaurantLoc.getChildren().clear();
            for (int i = 0; i < CrabFoodOperator.getMasterMap().getWidth(); i++) {
                for (int j = 0; j < CrabFoodOperator.getMasterMap().getHeight(); j++) {
                    Tile tile = new Tile(String.valueOf(CrabFoodOperator.getMasterMap().getSymbolAt(i, j)));
                    GridPane.setConstraints(tile, i, j);
                    gridRestaurantLoc.getChildren().addAll(tile);

                    for (Position pos : branchLoc) {
                        if (i == pos.getPosX() && j == pos.getPosY()) {
                            tile.colorTileGrey();
                        }
                    }
                }
            }
            branchLoc.clear();

            flagAddRes = true;

            primaryStage.setScene(sceneER);
        });

        Button btnMR_DONE = new Button("Done");
        btnMR_DONE.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnMR_DONE.setOnAction(fn -> {
            if (!obsListRestaurant.isEmpty()) {
                // clear selected items
                if (listRestaurant.getSelectionModel().getSelectedItem() != null) {
                    listRestaurant.getSelectionModel().clearSelection();
                }

                // if obsListRestaurant does not contain certain restaurants, delete them
                if (!CrabFoodOperator.getPartnerRestaurants().isEmpty()) {
                    Iterator itrAllPartnerRestaurant = CrabFoodOperator.getPartnerRestaurants().iterator();
                    while (itrAllPartnerRestaurant.hasNext()) {
                        Restaurant restaurant = (Restaurant) itrAllPartnerRestaurant.next();
                        if (!obsListRestaurant.contains(restaurant.getName())) {
                            itrAllPartnerRestaurant.remove();
                        }
                    }
                }

                // update stuff to txt and read again to restart program
                CrabFoodOperator.getTotalCrabFoodOrder().setValue(0);

                CrabFoodOperator.updatePartnerRestaurants();
                CrabFoodOperator.readPartnerRestaurants();

                CrabFoodOperator.getMasterMap().updateMap();

                CrabFoodOperator.getAllDeliveryGuys().clear();
                CrabFoodOperator.readAllDeliveryGuys();

                CrabFoodOperator.getAllPresetCrabFoodOrders().clear();
                CrabFoodOperator.readAllPresetCrabFoodOrders();

                CrabFoodOperator.getAllCrabFoodOrders().clear();

                clock.resetTime();

                primaryStage.setScene(sceneMenu);
            }
        });

        Button btnMR_CANCEL = new Button("Cancel");
        btnMR_CANCEL.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnMR_CANCEL.setOnAction(fn -> {
            // clear selected items
            if (listRestaurant.getSelectionModel().getSelectedItem() != null) {
                listRestaurant.getSelectionModel().clearSelection();
            }

            // reset the list of restaurants
            obsListRestaurant.clear();
            if (!CrabFoodOperator.getPartnerRestaurants().isEmpty()) {
                for (Restaurant restaurant : CrabFoodOperator.getPartnerRestaurants()) {
                    if (!obsListRestaurant.contains(restaurant.getName())) {
                        obsListRestaurant.add(restaurant.getName());
                    }
                }
            }

            // update stuff to txt and read again to restart program
            // SPECIAL CASE, since there might be edits in internal scenes
            CrabFoodOperator.getTotalCrabFoodOrder().setValue(0);

            CrabFoodOperator.updatePartnerRestaurants();
            CrabFoodOperator.readPartnerRestaurants();

            CrabFoodOperator.getMasterMap().updateMap();

            CrabFoodOperator.getAllDeliveryGuys().clear();
            CrabFoodOperator.readAllDeliveryGuys();

            CrabFoodOperator.getAllPresetCrabFoodOrders().clear();
            CrabFoodOperator.readAllPresetCrabFoodOrders();

            CrabFoodOperator.getAllCrabFoodOrders().clear();

            clock.resetTime();

            primaryStage.setScene(sceneMenu);
        });

        // #
        HBox layoutMRBottom = new HBox(10, btnMR_CANCEL, btnMR_DELETE, btnMR_EDIT, btnMR_ADD, btnMR_DONE);
        layoutMRBottom.setAlignment(Pos.CENTER);

        // ##
        GridPane layoutMR = new GridPane();
        GridPane.setVgrow(listRestaurant, Priority.ALWAYS);
        GridPane.setHgrow(listRestaurant, Priority.ALWAYS);
        GridPane.setConstraints(listRestaurant, 0, 0);
        GridPane.setConstraints(layoutMRBottom, 0, 1);
        layoutMR.setPadding(new Insets(10, 10, 10, 10));
        layoutMR.setVgap(10);
        layoutMR.getChildren().addAll(listRestaurant, layoutMRBottom);

        sceneMR = new Scene(layoutMR, stageWidth.getValue(), stageHeight.getValue());
    }

    private void makeSceneMD(Stage primaryStage) {
        // Number of Delivery Man
        Label labelNumDeliveryMan = new Label("Number of Delivery Man : ");

        Spinner spinnerNumDeliveryMan = new Spinner(1, 100, 1);
        spinnerNumDeliveryMan.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        spinnerNumDeliveryMan.setEditable(true);
        spinnerNumDeliveryMan.getValueFactory().setValue(CrabFoodOperator.getAllDeliveryGuys().size());

        // #
        HBox layoutMDTop = new HBox(10, labelNumDeliveryMan, spinnerNumDeliveryMan);
        layoutMDTop.setAlignment(Pos.CENTER);

        // Button
        Button btnMD_DONE = new Button("Done");
        btnMD_DONE.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnMD_DONE.setOnAction(fn -> {

            CrabFoodOperator.getAllDeliveryGuys().clear();
            for (int i = 1; i <= Integer.parseInt(spinnerNumDeliveryMan.getValue().toString()); i++) {
                CrabFoodOperator.getAllDeliveryGuys().add(new DeliveryGuy(i));
            }

            // update stuff to txt and read again to restart program
            CrabFoodOperator.getTotalCrabFoodOrder().setValue(0);

            CrabFoodOperator.updatePartnerRestaurants();
            CrabFoodOperator.readPartnerRestaurants();

            CrabFoodOperator.getMasterMap().updateMap();

            CrabFoodOperator.updateAllDeliveryGuys();
            CrabFoodOperator.readAllDeliveryGuys();

            CrabFoodOperator.getAllPresetCrabFoodOrders().clear();
            CrabFoodOperator.readAllPresetCrabFoodOrders();

            CrabFoodOperator.getAllCrabFoodOrders().clear();

            clock.resetTime();

            primaryStage.setScene(sceneMenu);
        });

        Button btnMD_CANCEL = new Button("Cancel");
        btnMD_CANCEL.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnMD_CANCEL.setOnAction(fn -> {
            spinnerNumDeliveryMan.getValueFactory().setValue(CrabFoodOperator.getAllDeliveryGuys().size());
            primaryStage.setScene(sceneMenu);
        });

        // #
        HBox layoutMDBottom = new HBox(10, btnMD_CANCEL, btnMD_DONE);
        layoutMDBottom.setAlignment(Pos.CENTER);

        // ##
        GridPane layoutMD = new GridPane();
        GridPane.setVgrow(layoutMDTop, Priority.ALWAYS);
        GridPane.setHgrow(layoutMDTop, Priority.ALWAYS);
        GridPane.setConstraints(layoutMDTop, 0, 0);
        GridPane.setConstraints(layoutMDBottom, 0, 1);
        layoutMD.setPadding(new Insets(10, 10, 10, 10));
        layoutMD.getChildren().addAll(layoutMDTop, layoutMDBottom);

        sceneMD = new Scene(layoutMD, stageWidth.getValue(), stageHeight.getValue());
    }

    private void makeSceneVOL(Stage primaryStage) {
        // Order Log
        TextArea txtareaOrderLog = new TextArea();
        txtareaOrderLog.setEditable(false);
        txtareaOrderLog.setFont(Font.font("Monospace", 15));
        txtareaOrderLog.textProperty().bind(CrabFoodOperator.getLog());
        CrabFoodOperator.getLog().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                txtareaOrderLog.selectPositionCaret(txtareaOrderLog.getLength());
                txtareaOrderLog.deselect();
            }
        });

        // Button
        Button btnVOL_BACK = new Button("Back");
        btnVOL_BACK.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnVOL_BACK.setOnAction(fn -> primaryStage.setScene(sceneMenu));

        // #
        HBox layoutVOLBottom = new HBox(btnVOL_BACK);
        layoutVOLBottom.setAlignment(Pos.CENTER);

        // ##
        GridPane layoutVOL = new GridPane();
        GridPane.setVgrow(txtareaOrderLog, Priority.ALWAYS);
        GridPane.setHgrow(txtareaOrderLog, Priority.ALWAYS);
        GridPane.setConstraints(txtareaOrderLog, 0, 0);
        GridPane.setConstraints(layoutVOLBottom, 0, 1);
        layoutVOL.setVgap(10);
        layoutVOL.setPadding(new Insets(10, 10, 10, 10));
        layoutVOL.getChildren().addAll(txtareaOrderLog, layoutVOLBottom);

        sceneVOL = new Scene(layoutVOL, stageWidth.getValue(), stageHeight.getValue());
    }

    private void makeSceneVM(Stage primaryStage) {
        // Button
        Button btnVM_BACK = new Button("Back");
        btnVM_BACK.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnVM_BACK.setOnAction(fn -> primaryStage.setScene(sceneMenu));

        // #
        HBox layoutVMBottom = new HBox(btnVM_BACK);
        layoutVMBottom.setAlignment(Pos.CENTER);

        ScrollPane gridPad = new ScrollPane(gridMap);

        // ##
        GridPane layoutVM = new GridPane();
        GridPane.setVgrow(gridPad, Priority.ALWAYS);
        GridPane.setHgrow(gridPad, Priority.ALWAYS);
        GridPane.setConstraints(gridPad, 0, 0);
        GridPane.setConstraints(layoutVMBottom, 0, 1);
        layoutVM.setVgap(10);
        layoutVM.setPadding(new Insets(10, 10, 10, 10));
        layoutVM.getChildren().addAll(gridPad, layoutVMBottom);

        sceneVM = new Scene(layoutVM, stageWidth.getValue(), stageHeight.getValue());
    }

    private void makeSceneSC(Stage primaryStage) {
        // Ordered dish & its quantity to be put into tableSC
        ObservableMap<String, Integer> mapSC = FXCollections.observableHashMap();
        ObservableList<String> mapSCkeys = FXCollections.observableArrayList();

        mapSC.addListener((MapChangeListener.Change<? extends String, ? extends Integer> change) -> {
            boolean removed = change.wasRemoved();
            if (removed != change.wasAdded()) {
                // no put for existing key
                if (removed) {
                    mapSCkeys.remove(change.getKey());
                } else {
                    mapSCkeys.add(change.getKey());
                }
            }
        });

        // Your Orders
        Label labelYourOrders = new Label("Your orders");
        // 
        TableColumn<String, String> colSCDish = new TableColumn<>("Dish");
        colSCDish.setCellValueFactory(cd -> Bindings.createStringBinding(() -> cd.getValue()));

        TableColumn<String, Integer> colSCQuantity = new TableColumn<>("Quantity");
        colSCQuantity.setCellValueFactory(cd -> Bindings.valueAt(mapSC, cd.getValue()));

        TableView<String> tableSC = new TableView<>(mapSCkeys);
        tableSC.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableSC.getColumns().setAll(colSCDish, colSCQuantity);
        //
        Button btnSC_REMOVE = new Button("Remove");
        btnSC_REMOVE.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnSC_REMOVE.setOnAction(fn -> {
            mapSC.remove(tableSC.getSelectionModel().getSelectedItem());
        });

        // #
        VBox layoutSCTopRight = new VBox(10, labelYourOrders, tableSC, btnSC_REMOVE);
        layoutSCTopRight.setAlignment(Pos.CENTER);
        VBox.setVgrow(tableSC, Priority.ALWAYS);

        // Customer ID
        Label labelCustomerID = new Label("Customer ID : ");

        Text txtCustomerID = new Text();
        txtCustomerID.textProperty().bind(CrabFoodOperator.getTotalCrabFoodOrder().add(1).asString());

        // Order Time
        Label labelOrderTime = new Label("Order Time : ");

        // Restaurant
        Label labelRestaurant = new Label("Restaurant : ");

        ComboBox comboRestaurant = new ComboBox();
        comboRestaurant.setPromptText("Pick a restaurant");
        comboRestaurant.setPrefSize(450, 10);
        for (Restaurant restaurant : CrabFoodOperator.getPartnerRestaurants()) {
            if (!comboRestaurant.getItems().contains(restaurant.getName())) {
                comboRestaurant.getItems().add(restaurant.getName());
            }
        }

        // Dish & Quantity
        Label labelDish = new Label("Dish : ");

        ComboBox comboDish = new ComboBox();
        comboDish.setPromptText("Pick a dish");
        comboDish.setPrefSize(450, 10);

        Label labelQuantity = new Label("Quantity : ");

        Spinner spinnerQuantity = new Spinner(1, 20, 1);
        spinnerQuantity.setEditable(true);
        spinnerQuantity.setPrefSize(450, 10);

        Button btnSC_ADD = new Button("Add");
        btnSC_ADD.setPrefSize(75, 75);
        btnSC_ADD.setOnAction(fn -> {
            if (comboDish.getValue() != null) {
                mapSC.put(comboDish.getValue().toString(),
                        Integer.parseInt(spinnerQuantity.getValue().toString()));
            }
        });

        // listeners
        comboRestaurant.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // reset dish
                if (comboRestaurant.getSelectionModel().getSelectedItem() != null) {
                    for (Restaurant restaurant : CrabFoodOperator.getPartnerRestaurants()) {
                        if (comboRestaurant.getSelectionModel().getSelectedItem().toString().equals(restaurant.getName())) {
                            comboDish.getItems().clear();
                            for (Dish dish : restaurant.getAllAvailableDishes()) {
                                comboDish.getItems().add(dish.getName());
                            }
                        }
                    }
                }

                //reset spinner
                spinnerQuantity.getValueFactory().setValue(1);

                //clear order hash map
                mapSC.clear();
            }
        });

        comboDish.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                //reset spinner
                if (mapSC.containsKey(comboDish.getSelectionModel().getSelectedItem())) {
                    spinnerQuantity.getValueFactory().setValue(mapSC.get(comboDish.getSelectionModel().getSelectedItem()));
                } else {
                    spinnerQuantity.getValueFactory().setValue(1);
                }
            }
        });

        // Delivery Location
        Label labelDeliveryLoc = new Label("Delivery Location : ");
        Label labelX = new Label("X : ");
        Spinner spinnerX = new Spinner(0, 100, 1);
        spinnerX.setEditable(true);
        Label labelY = new Label("Y : ");
        Spinner spinnerY = new Spinner(0, 100, 1);
        spinnerY.setEditable(true);

        HBox coordinateLabels = new HBox(10, labelX, spinnerX, labelY, spinnerY);
        coordinateLabels.setAlignment(Pos.CENTER);

        // #
        GridPane layoutSCTopLeft = new GridPane();
        GridPane.setConstraints(labelCustomerID, 0, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(txtCustomerID, 1, 0);
        GridPane.setConstraints(labelOrderTime, 0, 1, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(txtOrderTime, 1, 1);
        GridPane.setConstraints(labelRestaurant, 0, 2, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(comboRestaurant, 1, 2);
        GridPane.setConstraints(labelDish, 0, 3, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(comboDish, 1, 3);
        GridPane.setConstraints(btnSC_ADD, 2, 3, 1, 2);
        GridPane.setConstraints(labelQuantity, 0, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(spinnerQuantity, 1, 4);
        GridPane.setConstraints(labelDeliveryLoc, 0, 5, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(coordinateLabels, 1, 5);
        layoutSCTopLeft.getChildren().addAll(labelCustomerID, txtCustomerID,
                labelOrderTime, txtOrderTime,
                labelRestaurant, comboRestaurant,
                labelDish, comboDish, btnSC_ADD,
                labelQuantity, spinnerQuantity,
                labelDeliveryLoc, coordinateLabels);
        layoutSCTopLeft.setVgap(10);
        layoutSCTopLeft.setHgap(10);
        layoutSCTopLeft.setAlignment(Pos.CENTER);

        // Button
        Button btnSC_DONE = new Button("Done");
        btnSC_DONE.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnSC_DONE.setOnAction(fn -> {
            if (comboRestaurant.getSelectionModel().getSelectedItem() != null && !mapSC.isEmpty()) {
                // add crabfood order to all crabfood orders
                CrabFoodOrder cfOrder = new CrabFoodOrder();
                cfOrder.setRestaurantName(comboRestaurant.getSelectionModel().getSelectedItem().toString());
                HashMap<String, Integer> dishOrders = new HashMap<>();
                dishOrders.putAll(mapSC);
                cfOrder.setDishOrders(dishOrders);
                cfOrder.setDeliveryLocation(new Position(
                        Integer.parseInt(spinnerX.getValue().toString()),
                        Integer.parseInt(spinnerY.getValue().toString())));
                cfOrder.setCookTime(cfOrder.calculateCookTime());
                cfOrder.setCustomerId(CrabFoodOperator.getTotalCrabFoodOrder().get() + 1);
                CrabFoodOperator.getTotalCrabFoodOrder().set(CrabFoodOperator.getTotalCrabFoodOrder().get() + 1);
                CrabFoodOperator.getAllCrabFoodOrders().add(cfOrder);
                CrabFoodOperator.sortCfOrders();

                // reset all components
                comboRestaurant.getSelectionModel().clearSelection();
                comboDish.getItems().clear();
                spinnerQuantity.getValueFactory().setValue(1);
                spinnerX.getValueFactory().setValue(1);
                spinnerY.getValueFactory().setValue(1);
                mapSC.clear();
                primaryStage.setScene(sceneMenu);
            }
        });

        Button btnSC_CANCEL = new Button("Cancel");
        btnSC_CANCEL.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnSC_CANCEL.setOnAction(fn -> {
            primaryStage.setScene(sceneMenu);
            comboRestaurant.getSelectionModel().clearSelection();
            comboDish.getSelectionModel().clearSelection();
            spinnerQuantity.getValueFactory().setValue(1);
            spinnerX.getValueFactory().setValue(1);
            spinnerY.getValueFactory().setValue(1);
            mapSC.clear();
        });

        // #
        HBox layoutSCBottom = new HBox(10, btnSC_CANCEL, btnSC_DONE);
        layoutSCBottom.setAlignment(Pos.CENTER);

        GridPane layoutSC = new GridPane();
        GridPane.setConstraints(layoutSCTopLeft, 0, 0);
        GridPane.setConstraints(layoutSCTopRight, 1, 0);
        GridPane.setConstraints(layoutSCBottom, 0, 1, 2, 1);
        GridPane.setHgrow(layoutSCTopLeft, Priority.ALWAYS);
        GridPane.setVgrow(layoutSCTopLeft, Priority.ALWAYS);
        GridPane.setHgrow(layoutSCTopRight, Priority.ALWAYS);
        GridPane.setVgrow(layoutSCTopRight, Priority.ALWAYS);
        layoutSC.setVgap(10);
        layoutSC.setHgap(10);
        layoutSC.setPadding(new Insets(10, 10, 10, 10));
        layoutSC.getChildren().addAll(layoutSCTopLeft, layoutSCTopRight, layoutSCBottom);

        sceneSC = new Scene(layoutSC, stageWidth.getValue(), stageHeight.getValue());
    }

    private void makeSceneER(Stage primaryStage) {
        // Restaurant Name
        Label labelRestaurantName = new Label("Name : ");

        txtareaRestaurantName.setPrefHeight(txtareaRestaurantName.DEFAULT_PREF_ROW_COUNT);
        txtareaRestaurantName.setPrefWidth(500);

        // Restaurant Location
        Label labelRestaurantLoc = new Label("Restaurant Location : ");

        gridRestaurantLoc.setPrefSize(700, 600);
        gridRestaurantLoc.setMaxSize(700, 600);

        ScrollPane gridPad = new ScrollPane(gridRestaurantLoc);
        gridPad.setMaxSize(700, 600);

        // Dishes
        Label labelDishes = new Label("Dishes : ");

        Button btnER_EDs = new Button("Edit Dishes");
        btnER_EDs.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnER_EDs.setOnAction(fn -> primaryStage.setScene(sceneEDs));

        // #
        GridPane layoutERTop = new GridPane();
        GridPane.setConstraints(labelRestaurantName, 0, 0);
        GridPane.setConstraints(txtareaRestaurantName, 1, 0);
        GridPane.setConstraints(labelRestaurantLoc, 0, 1);
        GridPane.setConstraints(gridPad, 1, 1);
        GridPane.setConstraints(labelDishes, 0, 2);
        GridPane.setConstraints(btnER_EDs, 1, 2);
        GridPane.setHalignment(labelRestaurantName, HPos.RIGHT);
        GridPane.setHalignment(labelRestaurantLoc, HPos.RIGHT);
        GridPane.setHalignment(labelDishes, HPos.RIGHT);
        layoutERTop.setVgap(10);
        layoutERTop.getChildren().addAll(labelRestaurantName, txtareaRestaurantName,
                labelRestaurantLoc, gridPad,
                labelDishes, btnER_EDs);

        layoutERTop.setAlignment(Pos.CENTER);

        // Button
        Button btnER_DONE = new Button("Done");
        btnER_DONE.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnER_DONE.setOnAction(fn -> {
            // flag to indicate if user inputted location(s)
            boolean hasPos = false;
            for (Node tileObj : gridRestaurantLoc.getChildren()) {
                Tile tile = (Tile) tileObj;
                if (tile.isBlue()) {
                    hasPos = true;
                    break;
                }
            }

            if (!txtareaRestaurantName.getText().isEmpty() && hasPos && !obsListDishes.isEmpty()) {
                if (flagAddRes) {
                    String restaurantName = "";
                    Character restaurantMapSymbol = '0';

                    // read restaurant name
                    restaurantName = txtareaRestaurantName.getText();

                    // read restaurant map symbol
                    restaurantMapSymbol = restaurantName.charAt(0);

                    // read restaurant positions 
                    ArrayList<Position> resLoc = new ArrayList<>();
                    for (Node tileObj : gridRestaurantLoc.getChildren()) {
                        Tile tile = (Tile) tileObj;
                        if (tile.isBlue()) {
                            resLoc.add(new Position(GridPane.getColumnIndex(tileObj),
                                    GridPane.getRowIndex(tileObj)));
                        }
                    }

                    // read restaurant dishes
                    ArrayList<Dish> dishes = new ArrayList<>(dishesToAddTemp);
                    dishesToAddTemp.clear();
                    
                    // after reading, set name, map symbol, positions & dishes
                    for (int i = 0; i < resLoc.size(); i++) {
                        CrabFoodOperator.getPartnerRestaurants().add(new Restaurant(
                                restaurantName,
                                restaurantMapSymbol,
                                resLoc.get(i),
                                (ArrayList<Dish>) dishes.clone()));
                    }

                    flagAddRes = false;
                } else {
                    // update restaurant name
                    if (!CrabFoodOperator.getPartnerRestaurants().isEmpty()) {
                        for (Restaurant restaurant : CrabFoodOperator.getPartnerRestaurants()) {
                            if (restaurant.getName().equals(resToEdit.getValue())) {
                                restaurant.setName(txtareaRestaurantName.getText());
                            }
                        }
                    }
                    // update restaurant locations
                    ArrayList<Position> resLoc = new ArrayList<>();
                    for (Node tileObj : gridRestaurantLoc.getChildren()) {
                        Tile tile = (Tile) tileObj;
                        if (tile.isBlue()) {
                            resLoc.add(new Position(GridPane.getColumnIndex(tileObj),
                                    GridPane.getRowIndex(tileObj)));
                        }
                    }
                    String restaurantName = "";
                    Character restaurantMapSymbol = '0';
                    ArrayList<Dish> dishes = new ArrayList<>();
                    if (!CrabFoodOperator.getPartnerRestaurants().isEmpty()) {
                        Iterator itrRes = CrabFoodOperator.getPartnerRestaurants().iterator();
                        while (itrRes.hasNext()) {
                            Restaurant res = (Restaurant) itrRes.next();
                            if (res.getName().equals(txtareaRestaurantName.getText())) {
                                restaurantName = res.getName();
                                restaurantMapSymbol = res.getMapSymbol();
                                dishes = (ArrayList<Dish>) res.getAllAvailableDishes().clone();
                                itrRes.remove();
                            }
                        }
                    }
                    if (!resLoc.isEmpty()) {
                        for (int i = 0; i < resLoc.size(); i++) {
                            CrabFoodOperator.getPartnerRestaurants().add(new Restaurant(restaurantName,
                                    restaurantMapSymbol, resLoc.get(i),
                                    (ArrayList<Dish>) dishes.clone()));
                        }
                    }
                }

                // reset sceneMR
                obsListRestaurant.clear();
                if (!CrabFoodOperator.getPartnerRestaurants().isEmpty()) {
                    for (Restaurant restaurant : CrabFoodOperator.getPartnerRestaurants()) {
                        if (!obsListRestaurant.contains(restaurant.getName())) {
                            obsListRestaurant.add(restaurant.getName());
                        }
                    }
                }

                // reset input fields
                txtareaRestaurantName.clear();
                for (Object tileObj : gridRestaurantLoc.getChildren()) {
                    Tile tile = (Tile) tileObj;
                    if (!tile.getText().equals("0")) {
                        tile.colorTileNull();
                    } else {
                        tile.colorTileGrey();
                    }
                }

                // update map as soon as map changes
                CrabFoodOperator.getMasterMap().updateMap();

                // clear stuff in edit
                resToEdit.setValue("");
                obsListDishes.clear();

                primaryStage.setScene(sceneMR);
            }
        });

        Button btnER_CANCEL = new Button("Cancel");
        btnER_CANCEL.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnER_CANCEL.setOnAction(fn -> {
            // reset input fields
            txtareaRestaurantName.clear();
            for (Object tileObj : gridRestaurantLoc.getChildren()) {
                Tile tile = (Tile) tileObj;
                if (!tile.getText().equals("0")) {
                    tile.colorTileNull();
                } else {
                    tile.colorTileGrey();
                }
            }

            // clear stuff in edit
            resToEdit.setValue("");
            obsListDishes.clear();
            dishesToAddTemp.clear();

            primaryStage.setScene(sceneMR);
        });

        // #
        HBox layoutERBottom = new HBox(10, btnER_CANCEL, btnER_DONE);
        layoutERBottom.setAlignment(Pos.CENTER);

        // ##
        GridPane layoutER = new GridPane();
        GridPane.setConstraints(layoutERTop, 0, 0);
        GridPane.setConstraints(layoutERBottom, 0, 1);
        GridPane.setVgrow(layoutERTop, Priority.ALWAYS);
        GridPane.setHgrow(layoutERTop, Priority.ALWAYS);
        layoutER.setPadding(new Insets(10, 10, 10, 10));
        layoutER.getChildren().addAll(layoutERTop, layoutERBottom);

        sceneER = new Scene(layoutER, stageWidth.getValue(), stageHeight.getValue());
    }

    private void makeSceneEDs(Stage primaryStage) {
        // Buttons
        Button btnEDs_EDIT = new Button("Edit");
        btnEDs_EDIT.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnEDs_EDIT.setOnAction(fn -> {
            if (listDishes.getSelectionModel().getSelectedItem() != null) {
                // take down dish name to edit for sceneED use
                dishToEdit.setValue(listDishes.getSelectionModel().getSelectedItem().toString());

                // put the dish's name to be edit in sceneED
                txtareaDishName.setText(dishToEdit.getValue());

                // put the dish's prep time to be edit in sceneED
                if (!CrabFoodOperator.getPartnerRestaurants().isEmpty()) {
                    for (Restaurant restaurant : CrabFoodOperator.getPartnerRestaurants()) {
                        if (!restaurant.getAllAvailableDishes().isEmpty()) {
                            for (Dish dish : restaurant.getAllAvailableDishes()) {
                                if (dish.getName().equals(dishToEdit.getValue())) {
                                    spinnerDishPrepTime.getValueFactory().setValue(dish.getFoodPrepareDuration());
                                    break;
                                }
                            }
                        }
                    }
                }

                primaryStage.setScene(sceneED);
            }
        });

        Button btnEDs_DELETE = new Button("Delete");
        btnEDs_DELETE.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnEDs_DELETE.setOnAction(fn -> {
            if (listDishes.getSelectionModel().getSelectedItem() != null) {
                // for now, remove only from observable list
                obsListDishes.remove(listDishes.getSelectionModel().getSelectedItem().toString());
            }
        });

        Button btnEDs_ADD = new Button("Add");
        btnEDs_ADD.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnEDs_ADD.setOnAction(fn -> {
            // clear selected item 
            if (listDishes.getSelectionModel().getSelectedItem() != null) {
                listDishes.getSelectionModel().clearSelection();
            }

            flagAddDish = true;

            primaryStage.setScene(sceneED);
        });

        Button btnEDs_CANCEL = new Button("Cancel");
        btnEDs_CANCEL.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnEDs_CANCEL.setOnAction(fn -> {
            // clear selected items
            if (listDishes.getSelectionModel().getSelectedItem() != null) {
                listDishes.getSelectionModel().clearSelection();
            }

            // reset the dishes of restaurant in edit
            obsListDishes.clear();
            if (!CrabFoodOperator.getPartnerRestaurants().isEmpty()) {
                for (Restaurant restaurant : CrabFoodOperator.getPartnerRestaurants()) {
                    if (!obsListDishes.contains(restaurant.getName())) {
                        obsListDishes.add(restaurant.getName());
                    }
                }
            }

            primaryStage.setScene(sceneER);
        });

        Button btnEDs_DONE = new Button("Done");
        btnEDs_DONE.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnEDs_DONE.setOnAction(fn -> {
            if (!obsListDishes.isEmpty()) {
                // clear selected items
                if (listDishes.getSelectionModel().getSelectedItem() != null) {
                    listDishes.getSelectionModel().clearSelection();
                }

                // if obsListDishes does not contain certain dishes, remove them
                if (!CrabFoodOperator.getPartnerRestaurants().isEmpty()) {
                    for (Restaurant restaurant : CrabFoodOperator.getPartnerRestaurants()) {
                        if (!restaurant.getAllAvailableDishes().isEmpty()) {
                            Iterator itrAllAvailableDishes = restaurant.getAllAvailableDishes().iterator();
                            while (itrAllAvailableDishes.hasNext()) {
                                Dish dish = (Dish) itrAllAvailableDishes.next();
                                if (!obsListDishes.contains(dish.getName())) {
                                    itrAllAvailableDishes.remove();
                                }
                            }
                        }
                    }
                }

                primaryStage.setScene(sceneER);
            }
        });

        // #
        HBox layoutEDsBottom = new HBox(10, btnEDs_CANCEL, btnEDs_DELETE, btnEDs_EDIT, btnEDs_ADD, btnEDs_DONE);
        layoutEDsBottom.setAlignment(Pos.CENTER);

        // ##
        GridPane layoutEDs = new GridPane();
        GridPane.setVgrow(listDishes, Priority.ALWAYS);
        GridPane.setHgrow(listDishes, Priority.ALWAYS);
        GridPane.setConstraints(listDishes, 0, 0);
        GridPane.setConstraints(layoutEDsBottom, 0, 1);
        layoutEDs.setPadding(new Insets(10, 10, 10, 10));
        layoutEDs.setVgap(10);
        layoutEDs.getChildren().addAll(listDishes, layoutEDsBottom);

        sceneEDs = new Scene(layoutEDs, stageWidth.getValue(), stageHeight.getValue());
    }

    private void makeSceneED(Stage primaryStage) {
        // Dish Name
        Label labelDishName = new Label("Dish Name : ");

        txtareaDishName.setPrefHeight(txtareaDishName.DEFAULT_PREF_ROW_COUNT);
        txtareaDishName.setPrefWidth(500);
        txtareaDishName.setPromptText("Enter dish name");

        // Dish Prep Time
        Label labelDishPrepTime = new Label("Dish Preparation Time : ");

        spinnerDishPrepTime.setPrefWidth(500);
        spinnerDishPrepTime.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        spinnerDishPrepTime.setEditable(true);

        // #
        GridPane layoutEDTop = new GridPane();
        GridPane.setConstraints(labelDishName, 0, 0);
        GridPane.setConstraints(txtareaDishName, 1, 0);
        GridPane.setConstraints(labelDishPrepTime, 0, 1);
        GridPane.setConstraints(spinnerDishPrepTime, 1, 1);
        GridPane.setHalignment(labelDishName, HPos.RIGHT);

        layoutEDTop.setVgap(10);
        layoutEDTop.setHgap(10);
        layoutEDTop.getChildren().addAll(labelDishName, txtareaDishName, labelDishPrepTime, spinnerDishPrepTime);
        layoutEDTop.setAlignment(Pos.CENTER);

        // Button
        Button btnED_DONE = new Button("Done");
        btnED_DONE.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnED_DONE.setOnAction(fn -> {
            if (!txtareaDishName.getText().isEmpty()) {

                if (flagAddDish) {
                    // create a new dish with the written dish & time
                    dishesToAddTemp.add(new Dish(txtareaDishName.getText(),
                            Integer.parseInt(spinnerDishPrepTime.getValue().toString())));
                    System.out.println(txtareaDishName.getText());
                    obsListDishes.add(txtareaDishName.getText());
                    System.out.println(obsListDishes);
                    
                    flagAddDish = false;
                } else {
                    // edit dish, update dish name & dish prep time
                    if (!CrabFoodOperator.getPartnerRestaurants().isEmpty()) {
                        for (Restaurant restaurant : CrabFoodOperator.getPartnerRestaurants()) {
                            if (restaurant.getName().equals(resToEdit.getValue())) {
                                if (!restaurant.getAllAvailableDishes().isEmpty()) {
                                    for (Dish dish : restaurant.getAllAvailableDishes()) {
                                        if (dish.getName().equals(dishToEdit.getValue())) {
                                            dish.setName(txtareaDishName.getText());
                                            dish.setFoodPrepareDuration(Integer.parseInt(spinnerDishPrepTime.getValue().toString()));
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // read the updated dishes to display in sceneEDs
                obsListDishes.clear();
                if (!CrabFoodOperator.getPartnerRestaurants().isEmpty()) {
                    for (Restaurant restaurant : CrabFoodOperator.getPartnerRestaurants()) {
                        if (restaurant.getName().equals(resToEdit.getValue())) {
                            if (!restaurant.getAllAvailableDishes().isEmpty()) {
                                for (Dish dish : restaurant.getAllAvailableDishes()) {
                                    obsListDishes.add(dish.getName());
                                }
                            }
                            break;
                        }
                    }
                }

                // reset input fields
                txtareaDishName.clear();
                spinnerDishPrepTime.getValueFactory().setValue(5);

                // clear stuff in edit
                dishToEdit.setValue("");

                primaryStage.setScene(sceneEDs);
            }
        });

        Button btnED_CANCEL = new Button("Cancel");
        btnED_CANCEL.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnED_CANCEL.setOnAction(fn -> {
            // reset input fields
            txtareaDishName.clear();
            spinnerDishPrepTime.getValueFactory().setValue(5);

            // clear stuff in edit
            dishToEdit.setValue("");

            if (flagAddDish) {
                flagAddDish = false;
            }

            primaryStage.setScene(sceneEDs);
        });

        // #
        HBox layoutEDBottom = new HBox(10, btnED_CANCEL, btnED_DONE);
        layoutEDBottom.setAlignment(Pos.CENTER);

        // ##
        GridPane layoutED = new GridPane();
        GridPane.setVgrow(layoutEDTop, Priority.ALWAYS);
        GridPane.setHgrow(layoutEDTop, Priority.ALWAYS);
        GridPane.setConstraints(layoutEDTop, 0, 0);
        GridPane.setConstraints(layoutEDBottom, 0, 1);
        layoutED.setPadding(new Insets(10, 10, 10, 10));
        layoutED.getChildren().addAll(layoutEDTop, layoutEDBottom);
        sceneED = new Scene(layoutED, stageWidth.getValue(), stageHeight.getValue());
    }

    private class Tile extends StackPane {

        Text text;
        Rectangle border;

        public Tile(String value) {
            border = new Rectangle(50, 50);
            border.setFill(null);
            border.setStroke(Color.BLACK);

            text = new Text();
            text.setText(value);
            text.setFont(Font.font(30));

            setAlignment(Pos.CENTER);
            getChildren().addAll(border, text);

            setOnMouseClicked(evt -> {
                if (border.getFill() == null) {
                    colorTileBlue();
                } else if (border.getFill() == Color.CORNFLOWERBLUE) {
                    border.setFill(null);
                }
            });
        }

        public boolean isBlue() {
            return border.getFill() == Color.CORNFLOWERBLUE;
        }

        public boolean isGrey() {
            return border.getFill() == Color.GREY;
        }

        public void colorTileNull() {
            border.setFill(null);
        }

        public void colorTileBlue() {
            border.setFill(Color.CORNFLOWERBLUE);
        }

        public void colorTileGrey() {
            border.setFill(Color.GREY);
        }

        public void colorTileLightGrey() {
            border.setFill(Color.LIGHTGRAY);
        }

        public String getText() {
            return text.toString();
        }
    }

}