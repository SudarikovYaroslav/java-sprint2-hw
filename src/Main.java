import com.google.gson.Gson;
import main.model.exceptions.TaskCreateException;
import main.model.exceptions.TaskLoadException;
import main.model.exceptions.TaskSaveException;

public class Main {
    public static void main(String[] args) throws TaskSaveException, TaskCreateException, TaskLoadException {
        Gson gson = new Gson();
    }
}
