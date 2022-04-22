import tests.myOldFashionedTests.FileBackedTest;
import tests.myOldFashionedTests.Test;

public class Main {
    public static void main(String[] args) {
        Test test = new FileBackedTest();
        test.run();
    }
}
