package util;

import service.*;

public class Managers {

    private static final InMemoryHistoryManager historyManger = new InMemoryHistoryManager();
    private static final InMemoryTaskManager taskManager = new InMemoryTaskManager(historyManger);

    public static TaskManager getDefault() {
        return taskManager;
    }

    public static HistoryManager getDefaultHistory() {
        return historyManger;
    }
}
