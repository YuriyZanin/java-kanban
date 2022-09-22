package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Status;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.utils.Managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> simpleTasks;
    private final HashMap<Integer, Epic> epicTasks;
    private final HashMap<Integer, SubTask> subTasks;
    private final HistoryManager historyManager;
    private int currentId;

    public InMemoryTaskManager() {
        this.simpleTasks = new HashMap<>();
        this.epicTasks = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
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
        subTasks.clear();
        for (Epic epicTask : getEpicTasks()) {
            epicTask.clearSubTasks();
            updateEpicStatus(epicTask);
        }
    }

    @Override
    public Task getSimpleTaskById(int id) {
        Task simpleTask = simpleTasks.get(id);
        historyManager.add(simpleTask);
        return simpleTask;
    }

    @Override
    public Epic getEpicTaskById(int id) {
        Epic epic = epicTasks.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = subTasks.get(id);
        historyManager.add(subTask);
        return subTask;
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
        historyManager.remove(id);
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
        List<SubTask> epicSubTasks = getEpicSubTasks(id);
        for (SubTask epicSubTask : epicSubTasks) {
            deleteSubTask(epicSubTask.getId());
        }
        epicTasks.remove(id);
        historyManager.remove(id);
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
        SubTask taskToRemove = subTasks.remove(id);
        Epic epicTask = epicTasks.get(taskToRemove.getParentId());
        epicTask.deleteSubTask(taskToRemove);
        updateEpicStatus(epicTask);
        historyManager.remove(id);
    }

    @Override
    public List<SubTask> getEpicSubTasks(int epicId) {
        List<SubTask> epicSubTasks = new ArrayList<>();
        for (SubTask subTask : subTasks.values()) {
            if (subTask.getParentId() == epicId)
                epicSubTasks.add(subTask);
        }
        return epicSubTasks;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateEpicStatus(Epic epicTask) {
        List<SubTask> epicSubTasks = getEpicSubTasks(epicTask.getId());
        if (epicSubTasks.isEmpty()) {
            epicTask.setStatus(Status.NEW);
        } else {
            boolean isExistNew = false;
            boolean isExistDone = false;
            for (SubTask subTask : epicSubTasks) {
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
