package ru.yandex.practicum.kanban.utils;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Status;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.service.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class TestData {
    public static final int SIMPLE_TASK_1_ID = 1;
    public static final int SIMPLE_TASK_2_ID = 2;
    public static final int EPIC_TASK_1_ID = 3;
    public static final int EPIC_TASK_2_ID = 4;
    public static final int SUB_TASK_1_ID = 5;
    public static final int SUB_TASK_2_ID = 6;
    public static final int SUB_TASK_3_ID = 7;

    public static final int LAST_ID = 8;

    public static final int NOT_EXIST_ID = 333;

    public static final Task SIMPLE_TASK_1 = new Task(SIMPLE_TASK_1_ID, "Simple1", Status.NEW, "simple task 1", LocalDateTime.now(), Duration.ofMinutes(10));
    public static final Task SIMPLE_TASK_2 = new Task(SIMPLE_TASK_2_ID, "Simple2", Status.NEW, "simple task 2", null, Duration.ofMinutes(10));
    public static final List<Task> SIMPLE_TASKS = List.of(SIMPLE_TASK_1, SIMPLE_TASK_2);
    public static final Epic EPIC_TASK_1 = new Epic(EPIC_TASK_1_ID, "Epic1", Status.NEW, "epic task 1", null, Duration.ofMinutes(12));
    public static final SubTask SUB_TASK_1 = new SubTask(SUB_TASK_1_ID, "SubTask1", Status.NEW, "sub task 1", LocalDateTime.now(), Duration.ofMinutes(15), EPIC_TASK_1.getId());
    public static final SubTask SUB_TASK_2 = new SubTask(SUB_TASK_2_ID, "SubTask2", Status.NEW, "sub task 2", LocalDateTime.now(), Duration.ofMinutes(20), EPIC_TASK_1.getId());
    public static final SubTask SUB_TASK_3 = new SubTask(SUB_TASK_3_ID, "SubTask3", Status.NEW, "sub task 3", LocalDateTime.now(), Duration.ofMinutes(15), EPIC_TASK_1.getId());
    public static final List<SubTask> SUB_TASKS = List.of(SUB_TASK_1, SUB_TASK_2, SUB_TASK_3);
    public static final Epic EPIC_TASK_2 = new Epic(EPIC_TASK_2_ID, "Epic1", Status.NEW, "epic task 1", null, Duration.ofMinutes(10));
    public static final List<Epic> EPIC_TASKS = List.of(EPIC_TASK_1, EPIC_TASK_2);

    private TestData() {
    }

    public static <T extends InMemoryTaskManager> void setUpTestData(T manager) {
        EPIC_TASK_1.clearSubTasks();
        EPIC_TASK_2.clearSubTasks();
        manager.setCurrentId(LAST_ID);

        manager.createSimpleTask(SIMPLE_TASK_1);
        manager.createSimpleTask(SIMPLE_TASK_2);

        manager.createEpicTask(EPIC_TASK_1);
        manager.createSubTask(SUB_TASK_1);
        manager.createSubTask(SUB_TASK_2);
        manager.createSubTask(SUB_TASK_3);

        manager.createEpicTask(EPIC_TASK_2);

        manager.getSimpleTaskById(SIMPLE_TASK_1_ID);
        manager.getSimpleTaskById(SIMPLE_TASK_2_ID);
        manager.getEpicTaskById(EPIC_TASK_1_ID);
        manager.getSubTaskById(SIMPLE_TASK_2_ID);
    }
}
