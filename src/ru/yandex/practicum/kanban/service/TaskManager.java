package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.Status;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.Task;

import java.util.ArrayList;
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
        Epic parent = getEpicTaskById(subTask.getParentId());
        if (subTask.getId() == null) {
            subTask.setId(currentId++);
            parent.addSubTask(subTask);
        }
        subTaskMap.put(subTask.getId(), subTask);
        updateStatus(parent);
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
        Epic epicTask = getEpicTaskById(taskToRemove.getParentId());
        epicTask.deleteSubTask(taskToRemove);
        updateStatus(epicTask);
        subTaskMap.remove(id);
    }

    public List<SubTask> getEpicSubTasks(int id) {
        List<SubTask> subTasks = new ArrayList<>();
        Epic epicTask = getEpicTaskById(id);
        for (Integer subTaskId : epicTask.getSubTaskIds()) {
            subTasks.add(getSubTaskById(subTaskId));
        }
        return subTasks;
    }

    public void updateStatus(Epic epicTask) {
        if (epicTask.getSubTaskIds().isEmpty()) {
            epicTask.setStatus(Status.NEW);
        } else {
            boolean isExistNew = false;
            boolean isExistDone = false;
            boolean isExistInProgress = false;
            for (Integer subTaskId : epicTask.getSubTaskIds()) {
                SubTask subTask = getSubTaskById(subTaskId);
                if (subTask.getStatus() == Status.NEW) {
                    isExistNew = true;
                } else if (subTask.getStatus() == Status.DONE) {
                    isExistDone = true;
                } else {
                    isExistInProgress = true;
                }
            }
            if (isExistInProgress || (isExistNew && isExistDone)) {
                epicTask.setStatus(Status.IN_PROGRESS);
            } else if (!isExistNew) {
                epicTask.setStatus(Status.DONE);
            } else {
                epicTask.setStatus(Status.NEW);
            }
        }
    }
}
