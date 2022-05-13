package main.util;

import com.google.gson.*;
import main.model.Status;
import main.model.tasks.Epic;
import main.model.tasks.SubTask;

import java.lang.reflect.Type;

public class SubTaskSerializer implements JsonSerializer<SubTask>, JsonDeserializer<SubTask> {
    @Override
    public SubTask deserialize(JsonElement jsonElement, Type type,
                               JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();
        long id = jsonObject.get("id").getAsLong();

        JsonObject jsonEpic = jsonObject.get("epic").getAsJsonObject();
        String epicName = jsonEpic.get("name").getAsString();
        String epicDescription = jsonEpic.get("description").getAsString();
        long epicId = jsonEpic.get("id").getAsLong();
        Epic epic = new Epic(epicName, epicDescription, epicId);

        String status = jsonObject.get("status").getAsString();

        SubTask result = new SubTask(name, description, id);
        result.setEpic(epic);

        switch (status) {
            case "NEW" :
               result.setStatus(Status.NEW);
               break;
            case "DONE" :
                result.setStatus(Status.DONE);
                break;
            default :
                result.setStatus(Status.IN_PROGRESS);
        }
        return result;
    }

    @Override
    public JsonElement serialize(SubTask subTask, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.add("name", new JsonPrimitive(subTask.getName()));
        result.add("description", new JsonPrimitive(subTask.getDescription()));
        result.add("id", new JsonPrimitive(subTask.getId()));
        result.add("status", new JsonPrimitive(subTask.getStatus().toString()));

        if (subTask.getDuration() != null) {
            result.add("duration", new JsonPrimitive(subTask.getDuration().toString()));
        } else {
            result.add("duration", null);
        }

        if (subTask.getStartTime() != null) {
            result.add("startTime", new JsonPrimitive(subTask.getStartTime().toString()));
        } else {
            result.add("startTime", null);
        }

        if (subTask.getEpic() != null) {
            JsonObject epic = new JsonObject();
            epic.add("name", new JsonPrimitive(subTask.getEpic().getName()));
            epic.add("description", new JsonPrimitive(subTask.getEpic().getDescription()));
            epic.add("id", new JsonPrimitive(subTask.getEpic().getId()));

            result.add("epic", epic);
        } else {
            result.add("epic", null);
        }
        return result;
    }
}
