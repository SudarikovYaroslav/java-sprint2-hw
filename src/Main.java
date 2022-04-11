import tests.FileBackedTest;
import tests.Test;

public class Main {
    public static void main(String[] args) {
        Test test = new FileBackedTest();
        test.run();
    }
}
