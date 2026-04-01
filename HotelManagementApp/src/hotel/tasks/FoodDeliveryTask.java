package hotel.tasks;

import hotel.services.Hotel;
import javafx.scene.control.TextArea;

public class FoodDeliveryTask extends AbstractTask {
    private String guestName;
    private String foodItem;
    public FoodDeliveryTask(String guestName, String foodItem, TextArea guiLog, Hotel hotel) {
        super(Integer.valueOf(1500), "Food Delivery to " + guestName, guiLog, hotel);
        this.guestName = guestName;
        this.foodItem = foodItem;
    }
    @Override public void executeTaskDetails() { 
        logMessage("   -> Chef is preparing " + foodItem + " for " + guestName + " ($30 added)"); 
        hotel.addFoodCharge(guestName, 30);
    }
}
