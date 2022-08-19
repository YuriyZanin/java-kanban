package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.Task;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private int currentId;
    private HashMap<Integer, Task> simpleTaskMap;
    private HashMap<Integer, Epic> epicTaskMap;
    private HashMap<Integer, SubTask> subTaskMap;

    public TaskManager() {
        this.simpleTaskMap = new HashMap<>();
        this.epicTaskMap = new HashMap<>();
        this.subTaskMap = new HashMap<>();
        this.currentId = 1;
    }

    public Collection<Task> getSimpleTasks() {
        return simpleTaskMap.values();
    }

    public Collection<Epic> getEpicTasks() {
        return epicTaskMap.values();
    }

    public Collection<SubTask> getSubTasks() {
        return subTaskMap.values();
    }

    public void clearSimpleTasks() {
        simpleTaskMap.clear();
    }

    public void clearEpicTasks() {
        clearSubTasks();
        epicTaskMap.clear();
    }

    public void clearSubTasks() {
        for (SubTask subTask : List.copyOf(getSubTasks())) {
            deleteSubTask(subTask.getId());
        }
    }

    public Task getSimpleTaskById(int id) {
        return simpleTaskMap.get(id);
    }

    public Epic getEpicTaskById(int id) {
        return epicTaskMap.get(id);
    }

    public SubTask getSubTaskById(int id) {
        return subTaskMap.get(id);
    }

    public void createOrUpdateSimpleTask(Task task) {
        if (task.getId() == null) {
            task.setId(currentId++);
        }
        simpleTaskMap.put(task.getId(), task);
    }

    public void createOrUpdateEpicTask(Epic epic) {
        if (epic.getId() == null) {
            epic.setId(currentId++);
        }
        epicTaskMap.put(epic.getId(), epic);
    }

    public void createOrUpdateSubTask(SubTask subTask) {
        if (subTask.getId() == null) {
            subTask.setId(currentId++);
            subTask.getParent().getSubTasks().add(subTask);
        }
        subTask.getParent().updateStatus();
        subTaskMap.put(subTask.getId(), subTask);
    }

    public void deleteSimpleTask(int id) {
        simpleTaskMap.remove(id);
    }

    public void deleteEpicTask(int id) {
        for (SubTask subTask : List.copyOf(getEpicSubTasks(id))) {
            deleteSubTask(subTask.getId());
        }
        epicTaskMap.remove(id);
    }

    public void deleteSubTask(int id) {
        SubTask taskToRemove = getSubTaskById(id);
        taskToRemove.getParent().deleteSubTask(taskToRemove);
        taskToRemove.getParent().updateStatus();
        subTaskMap.remove(id);
    }

    public Collection<SubTask> getEpicSubTasks(int id) {
        return epicTaskMap.get(id).getSubTasks();
    }
}
