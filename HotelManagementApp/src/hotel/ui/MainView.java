package hotel.ui;

import hotel.models.Room;
import hotel.models.RoomStatus;
import hotel.models.RoomType;
import hotel.services.Hotel;
import hotel.tasks.CleaningTask;
import hotel.tasks.FoodDeliveryTask;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class MainView extends BorderPane {
    private Hotel hotel;
    private TextArea logArea;
    private int guestId = 1;

    private ComboBox<RoomType> cbRoomType;
    private ComboBox<String> cbActiveGuests;

    public MainView() {
        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-size: 14px;");

        hotel = new Hotel(3, logArea);
        buildUI();
    }

    private void buildUI() {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> System.exit(0));
        fileMenu.getItems().addAll(exitItem);
        menuBar.getMenus().add(fileMenu);
        this.setTop(menuBar);

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab operationsTab = new Tab("Operations", buildOperationsTab());
        Tab roomManagementTab = new Tab("Room Management", buildRoomManagementTab());

        tabPane.getTabs().addAll(operationsTab, roomManagementTab);
        this.setCenter(tabPane);

        hotel.logMsg("System Initialized.\nTotal Rooms Available: 3\n");
        hotel.logMsg("Types available: 1 SINGLE, 1 DOUBLE, 1 SUITE\n");
        hotel.logMsg("Prices: SINGLE ($100), DOUBLE ($200), SUITE ($500)\n\n");
    }

    private BorderPane buildOperationsTab() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(10));

        Label title = new Label("Hotel Operations Dashboard");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
        BorderPane.setAlignment(title, Pos.CENTER);
        BorderPane.setMargin(title, new Insets(0, 0, 10, 0));
        pane.setTop(title);

        pane.setCenter(logArea);

        VBox sidebar = new VBox(12);
        sidebar.setPadding(new Insets(15));
        sidebar.setPrefWidth(240);

        Label lblRoom = new Label("Select Room Type:");
        cbRoomType = new ComboBox<>();
        cbRoomType.getItems().addAll(RoomType.values());
        cbRoomType.setValue(RoomType.DOUBLE);
        cbRoomType.setMaxWidth(Double.MAX_VALUE);

        Button btnBook = new Button("Book Room");
        btnBook.setMaxWidth(Double.MAX_VALUE);
        btnBook.setOnAction(e -> handleBooking());

        Label lblGuest = new Label("Select Guest for Service:");
        cbActiveGuests = new ComboBox<>();
        cbActiveGuests.setItems(hotel.getActiveGuests());
        cbActiveGuests.setMaxWidth(Double.MAX_VALUE);

        Button btnRelease = new Button("Checkout Guest");
        btnRelease.setMaxWidth(Double.MAX_VALUE);
        btnRelease.setOnAction(e -> handleRelease());

        Button btnClean = new Button("Request Cleaning ($50)");
        btnClean.setMaxWidth(Double.MAX_VALUE);
        btnClean.setOnAction(e -> handleCleaning());

        Button btnFood = new Button("Order Food ($30)");
        btnFood.setMaxWidth(Double.MAX_VALUE);
        btnFood.setOnAction(e -> handleFood());

        sidebar.getChildren().addAll(
            lblRoom, cbRoomType, btnBook,
            new Separator(),
            lblGuest, cbActiveGuests,
            btnClean, btnFood,
            new Separator(),
            btnRelease
        );

        pane.setRight(sidebar);
        return pane;
    }

    private BorderPane buildRoomManagementTab() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(10));

        TableView<Room> table = new TableView<>();
        table.setItems(hotel.getAllRooms());

        TableColumn<Room, Integer> colNumber = new TableColumn<>("Room Number");
        colNumber.setCellValueFactory(data -> data.getValue().numberProperty().asObject());

        TableColumn<Room, RoomType> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(data -> data.getValue().typeProperty());

        TableColumn<Room, RoomStatus> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(data -> data.getValue().statusProperty());

        TableColumn<Room, String> colGuest = new TableColumn<>("Current Guest");
        colGuest.setCellValueFactory(data -> data.getValue().currentGuestProperty());

        TableColumn<Room, String> colCharges = new TableColumn<>("Total Charges ($)");
        colCharges.setCellValueFactory(data -> {
            int total = data.getValue().getTotalCharges();
            return new SimpleStringProperty(String.valueOf(total));
        });

        table.getColumns().addAll(colNumber, colType, colStatus, colGuest, colCharges);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        pane.setCenter(table);

        HBox bottomForm = new HBox(10);
        bottomForm.setPadding(new Insets(10, 0, 0, 0));
        bottomForm.setAlignment(Pos.CENTER_LEFT);

        TextField txtRoomNumber = new TextField();
        txtRoomNumber.setPromptText("Room #");

        ComboBox<RoomType> cbNewRoomType = new ComboBox<>();
        cbNewRoomType.getItems().addAll(RoomType.values());
        cbNewRoomType.setValue(RoomType.SINGLE);

        Button btnAddRoom = new Button("Add Room");
        btnAddRoom.setOnAction(e -> {
            try {
                int number = Integer.parseInt(txtRoomNumber.getText().trim());
                RoomType type = cbNewRoomType.getValue();
                boolean exists = hotel.getAllRooms().stream().anyMatch(r -> r.getNumber() == number);
                if (exists) {
                    hotel.logMsg("Error: Room " + number + " already exists!");
                } else {
                    hotel.addRoom(new Room(number, type));
                    txtRoomNumber.clear();
                    hotel.logMsg("Added new " + type + " room: " + number);
                }
            } catch (NumberFormatException ex) {
                hotel.logMsg("Error: Invalid room number format.");
            }
        });

        bottomForm.getChildren().addAll(new Label("Add New Room:"), txtRoomNumber, cbNewRoomType, btnAddRoom);
        pane.setBottom(bottomForm);

        return pane;
    }

    private void handleBooking() {
        String name = "Guest-" + guestId++;
        RoomType selectedType = cbRoomType.getValue();
        new Thread(() -> hotel.bookRoom(name, selectedType)).start();
    }

    private void handleRelease() {
        String targetGuest = cbActiveGuests.getValue();
        if (targetGuest == null || targetGuest.isEmpty()) {
            hotel.logMsg("Please select a guest to checkout.");
            return;
        }
        new Thread(() -> hotel.releaseRoom(targetGuest)).start();
    }

    private void handleCleaning() {
        String targetGuest = cbActiveGuests.getValue();
        if (targetGuest == null || targetGuest.isEmpty()) {
            hotel.logMsg("Please select a guest to receive Room Cleaning.");
            return;
        }
        new Thread(new CleaningTask(targetGuest, logArea, hotel)).start();
    }

    private void handleFood() {
        String targetGuest = cbActiveGuests.getValue();
        if (targetGuest == null || targetGuest.isEmpty()) {
            hotel.logMsg("Please select a guest to receive Food Delivery.");
            return;
        }
        new Thread(new FoodDeliveryTask(targetGuest, "Pizza & Soda", logArea, hotel)).start();
    }
}
