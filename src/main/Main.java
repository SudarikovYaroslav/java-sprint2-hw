package main;

import main.test.ConsoleTest;

public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();
        ConsoleTest consoleTest = new ConsoleTest(manager);
        consoleTest.runConsoleTest();
    }
}
