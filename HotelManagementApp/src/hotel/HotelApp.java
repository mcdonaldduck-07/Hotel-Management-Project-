package hotel;

import hotel.ui.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HotelApp extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Hotel Management System");
        
        MainView root = new MainView();
        Scene scene = new Scene(root, 900, 600);
        
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
