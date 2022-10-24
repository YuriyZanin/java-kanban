package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Status;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.utils.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    public static final Comparator<Task> TASK_START_TIME_COMPARATOR = (o1, o2) -> {
        if (o1.getStartTime() == null && o2.getStartTime() == null)
            return o1.getId().compareTo(o2.getId());
        if (o1.getStartTime() == null) {
            return 1;
        }
        if (o2.getStartTime() == null) {
            return -1;
        }
        return o1.getStartTime().compareTo(o2.getStartTime());
    };
    private final HashMap<Integer, Task> simpleTasks;
    private final HashMap<Integer, Epic> epicTasks;
    private final HashMap<Integer, SubTask> subTasks;
    private final Set<Task> sortedTasks;
    private final HistoryManager historyManager;
    private int currentId;

    public InMemoryTaskManager() {
        this.simpleTasks = new HashMap<>();
        this.epicTasks = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
        this.sortedTasks = new TreeSet<>(TASK_START_TIME_COMPARATOR);
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
        simpleTasks.values().forEach(sortedTasks::remove);
        simpleTasks.clear();
    }

    @Override
    public void clearEpicTasks() {
        clearSubTasks();
        epicTasks.clear();
    }

    @Override
    public void clearSubTasks() {
        subTasks.values().forEach(sortedTasks::remove);
        subTasks.clear();
        for (Epic epicTask : getEpicTasks()) {
            epicTask.clearSubTasks();
            updateEpic(epicTask);
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
        if (task.getId() == null)
            task.setId(currentId++);
        simpleTasks.put(task.getId(), task);
        sortedTasks.add(task);
    }

    @Override
    public void updateSimpleTask(Task task) {
        Task oldTask = simpleTasks.put(task.getId(), task);
        if (oldTask != null) {
            sortedTasks.remove(oldTask);
        }
        sortedTasks.add(task);
    }

    @Override
    public void deleteSimpleTask(int id) {
        Task removedTask = simpleTasks.remove(id);
        if (removedTask != null) {
            sortedTasks.remove(removedTask);
        }
        historyManager.remove(id);
    }

    @Override
    public void createEpicTask(Epic epic) {
        if (epic.getId() == null)
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
        if (subTask.getId() == null)
            subTask.setId(currentId++);
        parent.addSubTask(subTask);
        subTasks.put(subTask.getId(), subTask);
        sortedTasks.add(subTask);
        updateEpic(parent);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        Epic parent = epicTasks.get(subTask.getParentId());
        SubTask oldTask = subTasks.put(subTask.getId(), subTask);
        if (oldTask != null) {
            sortedTasks.remove(oldTask);
        }
        sortedTasks.add(subTask);
        updateEpic(parent);
    }

    @Override
    public void deleteSubTask(int id) {
        SubTask taskToRemove = subTasks.remove(id);
        if (taskToRemove != null) {
            sortedTasks.remove(taskToRemove);
            Epic epicTask = epicTasks.get(taskToRemove.getParentId());
            epicTask.deleteSubTask(taskToRemove);
            updateEpic(epicTask);
            historyManager.remove(id);
        }
    }

    @Override
    public List<SubTask> getEpicSubTasks(int epicId) {
        List<SubTask> epicSubTasks = new ArrayList<>();
        Epic epicTask = epicTasks.get(epicId);
        if (epicTask != null) {
            for (Integer subTaskId : epicTask.getSubTaskIds()) {
                epicSubTasks.add(subTasks.get(subTaskId));
            }
        }
        return epicSubTasks;
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return sortedTasks;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public void clearHistory() {
        for (Task task : getHistory()) {
            historyManager.remove(task.getId());
        }
    }

    private void updateEpic(Epic epicTask) {
        List<SubTask> epicSubTasks = getEpicSubTasks(epicTask.getId());
        if (epicSubTasks.isEmpty()) {
            epicTask.setStatus(Status.NEW);
            epicTask.setDuration(Duration.ZERO);
            epicTask.setStartTime(null);
            epicTask.setEndTime(null);
        } else {
            boolean isExistNew = false;
            boolean isExistDone = false;
            boolean isExistInProgress = false;
            Duration duration = Duration.ZERO;
            LocalDateTime startTime = epicSubTasks.get(0).getStartTime();
            for (SubTask subTask : epicSubTasks) {
                if (subTask.getDuration() != null) {
                    duration = duration.plus(subTask.getDuration());
                }
                if (subTask.getStartTime() != null && subTask.getStartTime().isBefore(startTime)) {
                    startTime = subTask.getStartTime();
                }

                if (subTask.getStatus() == Status.NEW) {
                    isExistNew = true;
                } else if (subTask.getStatus() == Status.DONE) {
                    isExistDone = true;
                } else {
                    isExistInProgress = true;
                }
            }
            epicTask.setDuration(duration);
            if (startTime != null) {
                epicTask.setStartTime(startTime);
                epicTask.setEndTime(startTime.plus(duration));
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

    public void setCurrentId(int currentId) {
        this.currentId = currentId;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }
}
