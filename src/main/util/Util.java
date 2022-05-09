package main.util;

import main.model.Status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Util {

    private Util(){}

    public static Status getStatusFromString(String data) {
        switch (data) {
            case "NEW":
                return Status.NEW;
            case "DONE":
                return Status.DONE;
            default: return Status.IN_PROGRESS;
        }
    }

    public static long getIdFromString(String data, String expMessage) {
        long id = 0;
        try {
            id = Long.parseLong(data);
        } catch (NumberFormatException e) {
            print(e.getMessage() + " " + expMessage);
        }
        return id;
    }

    public static String getBackedPath() {
        Path uniPath = Paths.get("").toAbsolutePath();
        Path fileBacked = Paths.get(uniPath + "\\FileBacked.txt");

        try {
            if (!Files.exists(fileBacked)) Files.createFile(fileBacked);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileBacked.toString();
    }

    private static void print(String message) {
        System.out.println(message);
    }
}
