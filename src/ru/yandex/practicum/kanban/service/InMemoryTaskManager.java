package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.model.Status;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager{
    private int currentId;
    private final HashMap<Integer, Task> simpleTasks;
    private final HashMap<Integer, Epic> epicTasks;
    private final HashMap<Integer, SubTask> subTasks;

    public InMemoryTaskManager() {
        this.simpleTasks = new HashMap<>();
        this.epicTasks = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.currentId = 1;
    }

    @Override
    public List<Task> getSimpleTasks() {
        return List.copyOf(simpleTasks.values());
    }

    @Override
    public List<Epic> getEpicTasks() {
        return List.copyOf(epicTasks.values());
    }

    @Override
    public List<SubTask> getSubTasks() {
        return List.copyOf(subTasks.values());
    }

    @Override
    public void clearSimpleTasks() {
        simpleTasks.clear();
    }

    @Override
    public void clearEpicTasks() {
        clearSubTasks();
        epicTasks.clear();
    }

    @Override
    public void clearSubTasks() {
        for (Epic epicTask : getEpicTasks()) {
            epicTask.getSubTaskIds().clear();
            updateEpicStatus(epicTask);
        }
        subTasks.clear();
    }

    @Override
    public Task getSimpleTaskById(int id) {
        return simpleTasks.get(id);
    }

    @Override
    public Epic getEpicTaskById(int id) {
        return epicTasks.get(id);
    }

    @Override
    public SubTask getSubTaskById(int id) {
        return subTasks.get(id);
    }

    @Override
    public void createSimpleTask(Task task) {
        task.setId(currentId++);
        simpleTasks.put(task.getId(), task);
    }

    @Override
    public void updateSimpleTask(Task task) {
        simpleTasks.put(task.getId(), task);
    }

    @Override
    public void deleteSimpleTask(int id) {
        simpleTasks.remove(id);
    }

    @Override
    public void createEpicTask(Epic epic) {
        epic.setId(currentId++);
        epicTasks.put(epic.getId(), epic);
    }

    @Override
    public void updateEpicTask(Epic epic) {
        epicTasks.put(epic.getId(), epic);
    }

    @Override
    public void deleteEpicTask(int id) {
        Epic taskToRemove = epicTasks.get(id);
        for (Integer subTaskId : taskToRemove.getSubTaskIds()) {
            subTasks.remove(subTaskId);
        }
        epicTasks.remove(id);
    }

    @Override
    public void createSubTask(SubTask subTask) {
        Epic parent = epicTasks.get(subTask.getParentId());
        subTask.setId(currentId++);
        parent.addSubTask(subTask);
        subTasks.put(subTask.getId(), subTask);
        updateEpicStatus(parent);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        Epic parent = epicTasks.get(subTask.getParentId());
        subTasks.put(subTask.getId(), subTask);
        updateEpicStatus(parent);
    }

    @Override
    public void deleteSubTask(int id) {
        SubTask taskToRemove = subTasks.get(id);
        Epic epicTask = epicTasks.get(taskToRemove.getParentId());
        epicTask.deleteSubTask(taskToRemove);
        updateEpicStatus(epicTask);
        subTasks.remove(id);
    }

    @Override
    public List<SubTask> getEpicSubTasks(int epicId) {
        List<SubTask> epicSubTasks = new ArrayList<>();
        Epic epicTask = epicTasks.get(epicId);
        for (Integer subTaskId : epicTask.getSubTaskIds()) {
            epicSubTasks.add(subTasks.get(subTaskId));
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
                SubTask subTask = subTasks.get(subTaskId);
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