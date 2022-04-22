import tests.consoleLogTests.FileBackedTest;
import tests.consoleLogTests.Test;

public class Main {
    public static void main(String[] args) {
        Test test = new FileBackedTest();
        test.run();
    }
}
