package hotel.models;

import javafx.beans.property.*;

public class Room {
    private final IntegerProperty number;
    private final ObjectProperty<RoomType> type;
    private final ObjectProperty<RoomStatus> status;
    private final StringProperty currentGuest;
    private int accruedCharges;
    private int cleaningCharges;
    private int foodCharges;

    public Room(int number, RoomType type) {
        this.number = new SimpleIntegerProperty(number);
        this.type = new SimpleObjectProperty<>(type);
        this.status = new SimpleObjectProperty<>(RoomStatus.AVAILABLE);
        this.currentGuest = new SimpleStringProperty("");
    }

    public int getNumber() { return number.get(); }
    public IntegerProperty numberProperty() { return number; }

    public RoomType getType() { return type.get(); }
    public ObjectProperty<RoomType> typeProperty() { return type; }

    public RoomStatus getStatus() { return status.get(); }
    public ObjectProperty<RoomStatus> statusProperty() { return status; }
    public void setStatus(RoomStatus status) { this.status.set(status); }
    
    public String getCurrentGuest() { return currentGuest.get(); }
    public StringProperty currentGuestProperty() { return currentGuest; }
    public void setCurrentGuest(String guest) { this.currentGuest.set(guest == null ? "" : guest); }
    
    public void addCleaningCharge(int amount) { this.cleaningCharges += amount; this.accruedCharges += amount; }
    public void addFoodCharge(int amount) { this.foodCharges += amount; this.accruedCharges += amount; }
    public void addRoomCharge(int amount) { this.accruedCharges += amount; }
    
    public int getTotalCharges() { return accruedCharges; }
    public int getCleaningCharges() { return cleaningCharges; }
    public int getFoodCharges() { return foodCharges; }
    
    public void resetCharges() { 
        this.accruedCharges = 0; 
        this.cleaningCharges = 0;
        this.foodCharges = 0;
        this.setCurrentGuest("");
    }
}
