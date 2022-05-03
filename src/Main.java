import main.model.exceptions.TaskCreateException;
import main.model.exceptions.TaskLoadException;
import main.model.exceptions.TaskSaveException;
import main.service.FileBackedTaskManager;
import main.util.Managers;
import main.util.Util;

public class Main {
    public static void main(String[] args) throws TaskSaveException, TaskCreateException, TaskLoadException {
        FileBackedTaskManager reloadedTaskManager = Managers.loadFromFile(Util.getBackedPath());

        System.out.println(reloadedTaskManager.getTaskById(1));
        System.out.println(reloadedTaskManager.getEpicById(2));
        System.out.println(reloadedTaskManager.getSubTaskById(3));
    }
}
