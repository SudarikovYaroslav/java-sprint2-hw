package main.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeParametersManager {

    public static final String DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm";

    public LocalDateTime convertStringToLocalDateTime(String startTime) {
        if (startTime.equals("null")) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
        return LocalDateTime.parse(startTime, formatter);
    }

    public Duration convertStringToDuration(String duration) {
        if (duration.equals("null")) return null;
        Duration resultDuration = Duration.ZERO;
        long millis = Long.parseLong(duration.substring(1));
        return resultDuration.plusMillis(millis);
    }

    public String convertStartTimeToString(LocalDateTime startTime) {
        if (startTime == null) return "null";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
        return startTime.format(formatter);
    }

    public String convertDurationToString(Duration duration) {
        if (duration == null) return "null";
        return "D" + duration.toMillis();
    }

}
