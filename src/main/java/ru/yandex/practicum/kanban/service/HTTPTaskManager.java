package ru.yandex.practicum.kanban.service;

import com.google.gson.Gson;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.web.KVTaskClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.yandex.practicum.kanban.utils.JsonUtil.*;

public class HTTPTaskManager extends FileBackedTaskManager {

    private final KVTaskClient client;
    private final Gson gson;

    public HTTPTaskManager(String url) {
        super(null);
        gson = getGsonInstance();
        client = new KVTaskClient(url);
        loadFromServer();
    }

    @Override
    protected void save() {
        client.put("tasks", gson.toJson(getSimpleTasks()));
        client.put("epics", gson.toJson(getEpicTasks()));
        client.put("subtasks", gson.toJson(getSubTasks()));
        client.put("history", gson.toJson(getHistory().stream().map(Task::getId).collect(Collectors.toList())));
    }

    private void loadFromServer() {
        String tasks = client.load("tasks");
        String epics = client.load("epics");
        String subTasks = client.load("subtasks");
        String history = client.load("history");

        Map<Integer, Task> loadedTasks = new HashMap<>();

        if (!tasks.isEmpty()) {
            List<Task> fromJson = gson.fromJson(tasks, SIMPLE_TASK_COLLECTION_TOKEN_TYPE.getType());
            fromJson.forEach(this::createSimpleTask);
            fromJson.forEach(task -> loadedTasks.put(task.getId(), task));
        }

        if (!epics.isEmpty()) {
            List<Epic> fromJson = gson.fromJson(epics, EPIC_TASK_COLLECTION_TOKEN_TYPE.getType());
            fromJson.forEach(this::createEpicTask);
            fromJson.forEach(task -> loadedTasks.put(task.getId(), task));
        }

        if (!subTasks.isEmpty()) {
            List<SubTask> fromJson = gson.fromJson(subTasks, SUB_TASK_COLLECTION_TOKEN_TYPE.getType());
            fromJson.forEach(this::createSubTask);
            fromJson.forEach(task -> loadedTasks.put(task.getId(), task));
        }

        if (!history.isEmpty()) {
            List<Integer> taskIds = gson.fromJson(history, INTEGER_COLLECTION_TOKEN_TYPE.getType());
            taskIds.forEach(id -> getHistoryManager().add(loadedTasks.get(id)));
        }
    }
}
