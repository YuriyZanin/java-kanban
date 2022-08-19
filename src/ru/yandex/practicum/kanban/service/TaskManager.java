package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.Status;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private int currentId;
    private final HashMap<Integer, Task> simpleTasks;
    private final HashMap<Integer, Epic> epicTasks;
    private final HashMap<Integer, SubTask> subTasks;

    public TaskManager() {
        this.simpleTasks = new HashMap<>();
        this.epicTasks = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.currentId = 1;
    }

    public List<Task> getSimpleTasks() {
        return List.copyOf(simpleTasks.values());
    }

    public List<Epic> getEpicTasks() {
        return List.copyOf(epicTasks.values());
    }

    public List<SubTask> getSubTasks() {
        return List.copyOf(subTasks.values());
    }

    public void clearSimpleTasks() {
        simpleTasks.clear();
    }

    public void clearEpicTasks() {
        clearSubTasks();
        epicTasks.clear();
    }

    public void clearSubTasks() {
        for (Epic epicTask : getEpicTasks()) {
            epicTask.getSubTaskIds().clear();
            updateEpicStatus(epicTask);
        }
        subTasks.clear();
    }

    public Task getSimpleTaskById(int id) {
        return simpleTasks.get(id);
    }

    public Epic getEpicTaskById(int id) {
        return epicTasks.get(id);
    }

    public SubTask getSubTaskById(int id) {
        return subTasks.get(id);
    }

    public void createSimpleTask(Task task) {
        task.setId(currentId++);
        simpleTasks.put(task.getId(), task);
    }

    public void updateSimpleTask(Task task) {
        simpleTasks.put(task.getId(), task);
    }

    public void deleteSimpleTask(int id) {
        simpleTasks.remove(id);
    }


    public void createEpicTask(Epic epic) {
        epic.setId(currentId++);
        epicTasks.put(epic.getId(), epic);
    }

    public void updateEpicTask(Epic epic) {
        epicTasks.put(epic.getId(), epic);
    }

    public void deleteEpicTask(int id) {
        Epic taskToRemove = getEpicTaskById(id);
        for (Integer subTaskId : taskToRemove.getSubTaskIds()) {
            subTasks.remove(subTaskId);
        }
        epicTasks.remove(id);
    }

    public void createSubTask(SubTask subTask) {
        Epic parent = getEpicTaskById(subTask.getParentId());
        subTask.setId(currentId++);
        parent.addSubTask(subTask);
        subTasks.put(subTask.getId(), subTask);
        updateEpicStatus(parent);
    }

    public void updateSubTask(SubTask subTask) {
        Epic parent = getEpicTaskById(subTask.getParentId());
        subTasks.put(subTask.getId(), subTask);
        updateEpicStatus(parent);
    }

    public void deleteSubTask(int id) {
        SubTask taskToRemove = getSubTaskById(id);
        Epic epicTask = getEpicTaskById(taskToRemove.getParentId());
        epicTask.deleteSubTask(taskToRemove);
        updateEpicStatus(epicTask);
        subTasks.remove(id);
    }

    public List<SubTask> getEpicSubTasks(int id) {
        List<SubTask> epicSubTasks = new ArrayList<>();
        Epic epicTask = getEpicTaskById(id);
        for (Integer subTaskId : epicTask.getSubTaskIds()) {
            epicSubTasks.add(getSubTaskById(subTaskId));
        }
        return epicSubTasks;
    }

    public void updateEpicStatus(Epic epicTask) {
        if (epicTask.getSubTaskIds().isEmpty()) {
            epicTask.setStatus(Status.NEW);
        } else {
            boolean isExistNew = false;
            boolean isExistDone = false;
            for (Integer subTaskId : epicTask.getSubTaskIds()) {
                SubTask subTask = getSubTaskById(subTaskId);
                if (subTask.getStatus() == Status.NEW) {
                    isExistNew = true;
                } else if (subTask.getStatus() == Status.DONE) {
                    isExistDone = true;
                } else {
                    epicTask.setStatus(Status.IN_PROGRESS);
                    return;
                }
            }
            if (isExistNew && isExistDone) {
                epicTask.setStatus(Status.IN_PROGRESS);
            } else if (!isExistNew) {
                epicTask.setStatus(Status.DONE);
            } else {
                epicTask.setStatus(Status.NEW);
            }
        }
    }
}
