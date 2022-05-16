package service.managers;

import com.google.gson.Gson;
import http.KVTaskClient;

import java.io.IOException;

public class HttpTaskManager extends FileBackedTaskManager {
    private String api_key;

    private final KVTaskClient kvTaskClient;
    private final Gson gson;

    public HttpTaskManager(HistoryManager historyManager, String kvServerUrl) {
        super(historyManager, kvServerUrl);
        kvTaskClient = new KVTaskClient(kvServerUrl);
        api_key = kvTaskClient.getAPI_KEY();
        gson = new Gson();
    }

    /* Сергей, что-то я не очень понял )) :

     ? "по тз все операции с задачами должны проходить через kv server, а не через  файл
        сейчас происходит только сохранение на сервер, но эти данные от туда не используются,
        а продолжают браться из файлов" ?

     Так ведь у меня HttpTaskManager и сохраняется и грузится с KVServer:
     HttpTaskHandler получает http запросы, мапит их сюда, менеджер их обрабатывает и сохраняет состояние вызовом
     save() (в FileBackedTaskManager я сохраняю состояние во всех методах, которые его меняют). Но в
     FileBackedTaskManager save() пишет всё в файл, а тут  HttpTaskManager в конструкторе получает url к kvServer
     (String kvServerUrl), а метод save() я переопределил, чтобы он через KVTaskClient записывал текущее состояние
     менеджера на KVServer. Никаких файлов для сохранения save() тут не использует.

     Загрузку ты мне ещё на моменте написания FileBackTaskManager рекомендовал писать в классе Util.Managers
     так как она менеджер возвращает. Поэтому и нынешнюю загрузку HttpTaskManager c KVServer я тоже там написал
     в методе HttpTaskManager loadHttpTaskManagerFromKVServer(String apiKey). Там тоже никаких файлов не использую.
     Аналогично сохранению KVTaskClient выполняет загрузку менеджера с KVServer по уникальному ключу, который при
     сохранении  выдаётся пользователю в момент выполнения запроса "/register"

     Т.е с помощью Managers.getDefault() мы можем получить новенький, пустой HttpTaskManager, а выполнив
     Managers.loadHttpTaskManagerFromKVServer(String apiKey) мы как раз и получаем менеджер с загруженным из
     KVServer состоянием. Для дальнейших запросов таски уже хранятся в оперативке с менеджером, а если что-то будет
     меняться, всё изменения и в оперативной памяти произойдут, как это было в FileBackedTaskManager и запишутся
     на сервере.

     Или я тут совсем всё напутал ))) ?
     */
    @Override
    public void save() {
        try {
            kvTaskClient.put(kvTaskClient.getAPI_KEY(), gson.toJson(this));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getApiKey() {
        return api_key;
    }
}
