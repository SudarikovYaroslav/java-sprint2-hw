package util;

import service.HistoryManager;
import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;
import service.TaskManager;

public class Managers {
    /**
     * This is util method, which creates and returns TaskManger of right type.
     * For now there is only one implementation of TaskManager but in the nearest future there will be others.
     */
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
