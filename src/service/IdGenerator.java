package service;

public class IdGenerator {
    private static long id = 1;
    private static IdGenerator instance = null;

    private IdGenerator() {
    }

    public static IdGenerator getInstance() {
        if (instance == null) {
            synchronized (IdGenerator.class) {
                instance = new IdGenerator();
            }
        }
        return instance;
    }

    public static void setStartIdValue(long value) {
        id = value;
    }

    public static Long peekCurrentIdValue() {
        return id;
    }

    public long generate() {
        return id++;
    }
}
