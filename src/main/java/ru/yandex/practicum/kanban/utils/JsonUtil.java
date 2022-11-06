package ru.yandex.practicum.kanban.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class JsonUtil {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();
    public static TypeToken<Collection<Task>> SIMPLE_TASK_COLLECTION_TOKEN_TYPE = new TypeToken<>() {
    };
    public static TypeToken<Collection<Epic>> EPIC_TASK_COLLECTION_TOKEN_TYPE = new TypeToken<>() {
    };
    public static TypeToken<Collection<SubTask>> SUB_TASK_COLLECTION_TOKEN_TYPE = new TypeToken<>() {
    };
    public static TypeToken<Collection<Integer>> INTEGER_COLLECTION_TOKEN_TYPE = new TypeToken<>() {
    };

    public static Gson getGsonInstance() {
        return GSON;
    }

    public static List<Task> parseJsonArrayOfTasks(String json) {
        JsonElement jsonElement = JsonParser.parseString(json);

        List<Task> tasks = new LinkedList<>();
        for (JsonElement element : jsonElement.getAsJsonArray()) {
            JsonObject object = element.getAsJsonObject();
            if (object.keySet().contains("parentId")) {
                tasks.add(GSON.fromJson(object, SubTask.class));
            } else if (object.keySet().contains("subTaskIds")) {
                tasks.add(GSON.fromJson(object, Epic.class));
            } else {
                tasks.add(GSON.fromJson(object, Task.class));
            }
        }
        return tasks;
    }
}
