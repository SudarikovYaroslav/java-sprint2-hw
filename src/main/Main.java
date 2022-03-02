package main;

import main.managers.InMemoryTaskManager;
import main.test.ConsoleTest;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        ConsoleTest consoleTest = new ConsoleTest(inMemoryTaskManager);
        consoleTest.runConsoleTest();
    }
}
