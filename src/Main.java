import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.model.serializators.SubTaskSerializer;
import main.model.tasks.Epic;
import main.model.tasks.SubTask;
import main.service.TaskForTestsGenerator;

public class Main {
    public static void main(String[] args) {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(SubTask.class, new SubTaskSerializer())
                .serializeNulls()
                .create();
        Epic testEpic = TaskForTestsGenerator.testEpicTemplateGen();
        SubTask testSubTask1 = TaskForTestsGenerator.testSubTaskTemplateGen();
        SubTask testSubTask2 = TaskForTestsGenerator.testSubTaskTemplateGen();
        testEpic.addSubTask(testSubTask1);
        testEpic.addSubTask(testSubTask2);
        testSubTask1.setEpic(testEpic);

        String epicJson = gson.toJson(testEpic);
        String subTaskJson = gson.toJson(testSubTask1);

        System.out.println("Source Epic: " + testEpic.toString());
        System.out.println("Source SubTask: " + testSubTask1.toString());

        System.out.println("=======================");

        System.out.println("Epic: " + epicJson);
        System.out.println("SubTask: " + subTaskJson);

        System.out.println("=======================");

        SubTask loadedSubTask = gson.fromJson(subTaskJson, SubTask.class);
        Epic loadedEpic = gson.fromJson(epicJson, Epic.class);

        System.out.println("Loaded epic: " + loadedEpic);
        System.out.println("Loaded subTask: " + loadedSubTask.toString());

        System.out.println("Equals Epic test: " + testEpic.equals(loadedEpic));
        System.out.println("Equals SubTask test: " + testSubTask1.equals(loadedSubTask));

    }


}
