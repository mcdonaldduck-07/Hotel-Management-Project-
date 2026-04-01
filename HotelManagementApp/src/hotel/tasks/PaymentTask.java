package hotel.tasks;

import hotel.services.Hotel;
import javafx.scene.control.TextArea;

public class PaymentTask extends AbstractTask {
    private String guestName;
    private int amount;
    public PaymentTask(String guestName, int amount, TextArea guiLog, Hotel hotel) {
        super(Integer.valueOf(2500), "Payment Processing for " + guestName, guiLog, hotel);
        this.guestName = guestName;
        this.amount = amount;
    }
    @Override public void executeTaskDetails() { 
        logMessage("   -> Processing credit card payment of $" + amount + " for " + guestName + "... "); 
    }
}
