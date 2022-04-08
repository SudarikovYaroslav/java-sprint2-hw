package util;

import model.Status;
import model.ManagerLoadException;

public class Util {
    public static Status getStatusFromString(String data) {
        Status status;

        switch (data) {
            case "NEW":
                status = Status.NEW;
                break;
            case "IN_PROGRESS":
                status = Status.IN_PROGRESS;
                break;
            case "DONE":
                status = Status.DONE;
            default:
                status = Status.IN_PROGRESS;
        }
        return status;
    }

    public static long getIdFromString(String data, String expMessage) {
        long id = 0;
        try {
            id = Long.parseLong(data);
        } catch (NumberFormatException e) {
            try {
                throw new ManagerLoadException(expMessage);
            } catch (ManagerLoadException exception) {
                exception.printStackTrace();
            }
        }
        return id;
    }
}
