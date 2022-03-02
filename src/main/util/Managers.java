package main.util;

import main.managers.InMemoryTaskManager;
import main.managers.TaskManager;

public class Managers {
    /**
     * This is util method, which creates and returns TaskManger of right type.
     * For now there is only one implementation of TaskManager but in the nearest future there will be others.
     */
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }
}
