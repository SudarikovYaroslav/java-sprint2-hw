package service;

public class IdGenerator {
    private static long id = 1;
    private static IdGenerator instance = null;

    public static void setStartIdValue(long value) {
        id = value;
    }

    public static Long peekCurrentIdValue() {
        return id;
    }

    public static long generate() {
        return id++;
    }
}
