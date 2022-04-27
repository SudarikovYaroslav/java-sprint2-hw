package main.service;

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

    public void setStartIdValue(long value) {
        id = value;
    }

    public Long peekCurrentIdValue() {
        return id;
    }

    public long generate() {
        return id++;
    }
}
