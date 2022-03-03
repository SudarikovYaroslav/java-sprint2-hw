package model;

public class IdGenerator {
    private static long id = 1;

    public long generate() {
        return id++;
    }
}
