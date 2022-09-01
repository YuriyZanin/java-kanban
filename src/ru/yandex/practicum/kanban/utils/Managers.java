package ru.yandex.practicum.kanban.utils;

import ru.yandex.practicum.kanban.service.InMemoryTaskManager;
import ru.yandex.practicum.kanban.service.TaskManager;

public class Managers {
    public static TaskManager getDefault(){
        return new InMemoryTaskManager();
    }
}
