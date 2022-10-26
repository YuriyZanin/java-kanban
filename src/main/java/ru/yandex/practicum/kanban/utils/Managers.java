package ru.yandex.practicum.kanban.utils;

import ru.yandex.practicum.kanban.service.HistoryManager;
import ru.yandex.practicum.kanban.service.InMemoryHistoryManager;
import ru.yandex.practicum.kanban.service.InMemoryTaskManager;
import ru.yandex.practicum.kanban.service.TaskManager;

import java.time.format.DateTimeFormatter;

public class Managers {
    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
