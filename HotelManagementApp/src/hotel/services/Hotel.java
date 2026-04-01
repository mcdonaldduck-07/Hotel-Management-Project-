package hotel.services;

import hotel.models.Room;
import hotel.models.RoomStatus;
import hotel.models.RoomType;
import hotel.tasks.PaymentTask;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextArea;

public class Hotel {
    private final ObservableList<Room> allRooms;
    private final ObservableList<String> activeGuests;
    private TextArea guiLog;

    public Hotel(int numRooms, TextArea guiLog) {
        this.guiLog = guiLog;
        this.allRooms = FXCollections.observableArrayList();
        this.activeGuests = FXCollections.observableArrayList();

        for (int i = 1; i <= numRooms; i++) {
            RoomType type = RoomType.SINGLE;
            if (i % 3 == 0) type = RoomType.SUITE;
            else if (i % 2 == 0) type = RoomType.DOUBLE;
            
            allRooms.add(new Room(100 + i, type)); 
        }
    }
    
    public ObservableList<Room> getAllRooms() {
        return allRooms;
    }
    
    public ObservableList<String> getActiveGuests() {
        return activeGuests;
    }

    public void addRoom(Room room) {
        Platform.runLater(() -> allRooms.add(room));
    }

    public synchronized void addCleaningCharge(String guest, int amount) {
        for (Room r : allRooms) {
            if (guest.equals(r.getCurrentGuest())) {
                r.addCleaningCharge(amount);
                return;
            }
        }
    }
    
    public synchronized void addFoodCharge(String guest, int amount) {
        for (Room r : allRooms) {
            if (guest.equals(r.getCurrentGuest())) {
                r.addFoodCharge(amount);
                return;
            }
        }
    }

    public void logMsg(String msg) {
        Logger.log(msg);
        Platform.runLater(() -> guiLog.appendText(msg + "\n"));
    }

    public synchronized void bookRoom(String guest, RoomType requestedType) {
        try {
            Room availableRoom = null;
            
            while (availableRoom == null) {
                for (Room r : allRooms) {
                    if (r.getStatus() == RoomStatus.AVAILABLE && r.getType() == requestedType) {
                        availableRoom = r;
                        break;
                    }
                }
                
                if (availableRoom == null) {
                    for (Room r : allRooms) {
                        if (r.getStatus() == RoomStatus.AVAILABLE) {
                            logMsg(guest + " -> " + requestedType + " unavailable, offering " + r.getType() + " instead.");
                            availableRoom = r;
                            break;
                        }
                    }
                }

                if (availableRoom == null) {
                    logMsg(guest + " is waiting... Hotel is completely full!");
                    wait(); // Wait until a room opens up
                }
            }

            logMsg(guest + " is booking Room " + availableRoom.getNumber() + " (" + availableRoom.getType() + " - Base: $" + availableRoom.getType().getPrice() + ")");
            Thread.sleep(800); 
            
            final Room roomFinal = availableRoom;
            Platform.runLater(() -> {
                roomFinal.setStatus(RoomStatus.OCCUPIED);
                roomFinal.setCurrentGuest(guest);
                if (!activeGuests.contains(guest)) {
                    activeGuests.add(guest);
                }
            });
            roomFinal.addRoomCharge(roomFinal.getType().getPrice());
            
            logMsg(guest + " BOOKED SUCCESSFULLY. (Room " + roomFinal.getNumber() + ")");
        } catch (InterruptedException e) {}
    }

    public synchronized void releaseRoom(String guest) {
        try {
            Room bookedRoom = null;
            for (Room r : allRooms) {
                if (guest.equals(r.getCurrentGuest())) {
                    bookedRoom = r;
                    break;
                }
            }

            if (bookedRoom == null) {
                logMsg("Error: " + guest + " cannot checkout because they are not currently booked.");
                return;
            }

            logMsg(guest + " is checking out of Room " + bookedRoom.getNumber() + "...");
            Thread.sleep(500);
            
            int roomCost = bookedRoom.getType().getPrice();
            int cleanCost = bookedRoom.getCleaningCharges();
            int foodCost = bookedRoom.getFoodCharges();
            int finalBill = bookedRoom.getTotalCharges();
            
            final Room roomFinal = bookedRoom;
            Platform.runLater(() -> {
                roomFinal.resetCharges();
                roomFinal.setStatus(RoomStatus.AVAILABLE);
                activeGuests.remove(guest);
            });
            
            logMsg("RECEIPT FOR " + guest + ":");
            logMsg("  > Room Rate: $" + roomCost);
            logMsg("  > Cleaning Services: $" + cleanCost);
            logMsg("  > Room Service / Food: $" + foodCost);
            logMsg("  > TOTAL AMOUNT DUE = $" + finalBill);
            
            logMsg(guest + " CHECKED OUT. Room " + roomFinal.getNumber() + " is now available.");
            notifyAll(); 

          
            new Thread(new PaymentTask(guest, finalBill, guiLog, this)).start();
        } catch (InterruptedException e) {}
    }
}
