package hotel.tasks;

import hotel.services.Hotel;
import javafx.scene.control.TextArea;

public class CleaningTask extends AbstractTask {
    private String guestName;
    public CleaningTask(String guestName, TextArea guiLog, Hotel hotel) {
        super(Integer.valueOf(2000), "Cleaning for " + guestName, guiLog, hotel);
        this.guestName = guestName;
    }
    @Override public void executeTaskDetails() { 
        logMessage("   -> Housekeeper is cleaning room for " + guestName + " ($50 added)"); 
        hotel.addCleaningCharge(guestName, 50);
    }
}
