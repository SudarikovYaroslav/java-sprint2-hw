package main.util;

import main.model.Status;
import main.model.exceptions.TaskLoadException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Util {

    public static Status getStatusFromString(String data) {
        Status status = Status.IN_PROGRESS; // default value

        switch (data) {
            case "NEW":
                status = Status.NEW;
                break;
            case "IN_PROGRESS":
                break;
            case "DONE":
                status = Status.DONE;
        }

        return status;
    }

    /* По поводу "действительно ли нужно сообщение об ошибке передавать извне в этот метод?
                  проще его уже тут генерировать"
    Этот метод я использую в разных методах при загрузке из файла состояния программы.
    Передавая сообщение об ошибке аргументом, в expMessage я точно могу указать место внутри упавшего метода, откуда
    бросился exception в процессе  работы метода loadFromFile(Path tasksFilePath) и методов по сборке тасков из строки.
    Например только от SubTask сюда приходит 3 разных варианта сообщения, т.к.
    Чтобы сохранить возможность точно указывать на место в процессе загрузки, откуда падает исключение,
    придётся передавать сюда несколько аргументов, и писать сложную логику, чтобы понять,
    из какого конкретно места загрузки выбросился exception. А так просто откуда надо пишешь нужное сообщение и всё.
    */
    public static long getIdFromString(String data, String expMessage) {
        long id = 0;
        try {
            id = Long.parseLong(data);
        } catch (NumberFormatException e) {
            try {
                throw new TaskLoadException(expMessage);
            } catch (TaskLoadException exception) {
                exception.printStackTrace();
            }
        }
        return id;
    }

    public static Path getBackedPath() {
        Path uniPath = Paths.get("").toAbsolutePath();
        Path fileBacked = Paths.get(uniPath + "\\FileBacked.txt");

        try {
            if (!Files.exists(fileBacked)) Files.createFile(fileBacked);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileBacked;
    }
}
