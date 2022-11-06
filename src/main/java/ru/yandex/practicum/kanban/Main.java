package ru.yandex.practicum.kanban;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.service.HTTPTaskManager;
import ru.yandex.practicum.kanban.service.TaskManager;
import ru.yandex.practicum.kanban.utils.Managers;
import ru.yandex.practicum.kanban.web.KVServer;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws IOException {
        KVServer kvServer = new KVServer();
        kvServer.start();
        TaskManager manager = Managers.getDefault();
        manager.createSimpleTask(new Task("test", "desc", LocalDateTime.now()));
        manager.createSimpleTask(new Task("test2", "desc", LocalDateTime.now().plusMinutes(20)));
        manager.createEpicTask(new Epic("epic", "des", LocalDateTime.now().plusMinutes(40), Duration.ZERO));
        manager.createSubTask(new SubTask("sub", "sec", LocalDateTime.now().plusMinutes(60), Duration.ZERO, 3));
        manager.getSubTaskById(4);
        manager.getSimpleTaskById(1);
        manager.getEpicTaskById(3);
        HTTPTaskManager manager1 = new HTTPTaskManager("http://localhost:8078");
        System.out.println(manager1.getSimpleTasks());
        System.out.println(manager1.getEpicTasks());
        System.out.println(manager1.getSubTasks());
        System.out.println(manager1.getHistory());
        kvServer.stop();
    }
}
