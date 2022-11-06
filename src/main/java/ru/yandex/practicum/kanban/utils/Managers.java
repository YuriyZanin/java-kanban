package ru.yandex.practicum.kanban.utils;

import ru.yandex.practicum.kanban.service.*;

public class Managers {
    final static String HOME = System.getProperty("user.home");
    final static String FILE_NAME = "test.csv";

    final static String URL = "http://localhost:8078";

    public static TaskManager getDefault() {
        return new HTTPTaskManager(URL);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getFileBackedTaskManager() {
        return new FileBackedTaskManager(HOME, FILE_NAME);
    }
}
