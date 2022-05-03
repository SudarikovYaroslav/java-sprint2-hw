package main.util;

import main.model.Status;
import main.model.exceptions.TaskLoadException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Util {

    public static Status getStatusFromString(String data) {
        Status status = Status.IN_PROGRESS; // default value

        switch (data) {
            case "NEW":
                status = Status.NEW;
                break;
            case "IN_PROGRESS":
                break;
            case "DONE":
                status = Status.DONE;
        }

        return status;
    }

    public static long getIdFromString(String data, String expMessage) {
        long id = 0;
        try {
            id = Long.parseLong(data);
        } catch (NumberFormatException e) {
            Util.print(e.getMessage() + " " + expMessage);
        }
        return id;
    }

    public static Path getBackedPath() {
        Path uniPath = Paths.get("").toAbsolutePath();
        Path fileBacked = Paths.get(uniPath + "\\FileBacked.txt");

        try {
            if (!Files.exists(fileBacked)) Files.createFile(fileBacked);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileBacked;
    }

    private static void print(String message) {
        System.out.println(message);
    }
}
