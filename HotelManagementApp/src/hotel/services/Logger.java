package hotel.services;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final String LOG_FILE = "hotel_log.txt";
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static synchronized void log(String message) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true)) {
            fw.write("[" + dtf.format(LocalDateTime.now()) + "] " + message + "\n");
        } catch (IOException e) {}
    }
}
