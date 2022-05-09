package main.service;

import java.nio.file.Path;

public class HTTPTaskManager extends FileBackedTaskManager {

    // Конструктор HTTPTaskManager должен будет вместо имени файла принимать URL к серверу KVServer
    public HTTPTaskManager(HistoryManager historyManager, String url) {
        super(historyManager, url);
    }

}
