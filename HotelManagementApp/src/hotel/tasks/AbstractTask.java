package hotel.tasks;

import hotel.services.Hotel;
import hotel.services.Loggable;
import hotel.services.Logger;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

public abstract class AbstractTask implements Runnable, Loggable {
    protected Integer durationMs;
    protected String name;
    protected TextArea guiLog;
    protected Hotel hotel;

    public AbstractTask(Integer durationMs, String name, TextArea guiLog, Hotel hotel) {
        this.durationMs = durationMs;
        this.name = name;
        this.guiLog = guiLog;
        this.hotel = hotel;
    }

    @Override
    public void logMessage(String msg) {
        Logger.log(msg);
        Platform.runLater(() -> guiLog.appendText(msg + "\n"));
    }

    public abstract void executeTaskDetails();

    @Override
    public void run() {
        logMessage("START: " + name);
        executeTaskDetails();
        try { Thread.sleep(durationMs); } catch (InterruptedException e) {}
        logMessage("DONE: " + name);
    }
}
