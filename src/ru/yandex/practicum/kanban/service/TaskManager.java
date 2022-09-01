package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getSimpleTasks();

    List<Epic> getEpicTasks();

    List<SubTask> getSubTasks();

    void clearSimpleTasks();

    void clearEpicTasks();

    void clearSubTasks();

    Task getSimpleTaskById(int id);

    Epic getEpicTaskById(int id);

    SubTask getSubTaskById(int id);

    void createSimpleTask(Task task);

    void updateSimpleTask(Task task);

    void deleteSimpleTask(int id);

    void createEpicTask(Epic epic);

    void updateEpicTask(Epic epic);

    void deleteEpicTask(int id);

    void createSubTask(SubTask subTask);

    void updateSubTask(SubTask subTask);

    void deleteSubTask(int id);

    List<SubTask> getEpicSubTasks(int epicId);
}
