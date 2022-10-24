package ru.yandex.practicum.kanban.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.exeption.ManagerSaveException;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.kanban.model.Status.*;
import static ru.yandex.practicum.kanban.utils.TestData.*;

abstract class TaskManagerTest<T extends TaskManager> {

    public T manager;

    @Test
    void getSimpleTasks() {
        List<Task> simpleTasks = manager.getSimpleTasks();
        assertEquals(SIMPLE_TASKS.size(), simpleTasks.size());
    }

    @Test
    void getEpicTasks() {
        List<Epic> epicTasks = manager.getEpicTasks();
        assertEquals(EPIC_TASKS.size(), epicTasks.size());
    }

    @Test
    void getSubTasks() {
        List<SubTask> subTasks = manager.getSubTasks();
        assertEquals(SUB_TASKS.size(), subTasks.size());
    }

    @Test
    void clearSimpleTasks() {
        manager.clearSimpleTasks();
        assertTrue(manager.getSimpleTasks().isEmpty());
    }

    @Test
    void clearEpicTasks() {
        manager.clearEpicTasks();
        assertTrue(manager.getEpicTasks().isEmpty());
    }

    @Test
    void clearSubTasks() {
        manager.clearSubTasks();
        assertTrue(manager.getSubTasks().isEmpty());
    }

    @Test
    void getSimpleTaskById() {
        Task task = manager.getSimpleTaskById(SIMPLE_TASK_1_ID);
        assertNotNull(task);
        assertEquals(SIMPLE_TASK_1, task);

        task = manager.getSimpleTaskById(NOT_EXIST_ID);
        assertNull(task);
    }

    @Test
    void getEpicTaskById() {
        Epic epic = manager.getEpicTaskById(EPIC_TASK_1_ID);
        assertNotNull(epic);
        assertEquals(EPIC_TASK_1, epic);

        epic = manager.getEpicTaskById(NOT_EXIST_ID);
        assertNull(epic);
    }

    @Test
    void getSubTaskById() {
        SubTask subTask = manager.getSubTaskById(SUB_TASK_1_ID);
        assertNotNull(subTask);
        assertEquals(SUB_TASK_1, subTask);

        subTask = manager.getSubTaskById(NOT_EXIST_ID);
        assertNull(subTask);
    }

    @Test
    void createSimpleTask() {
        Task newTask = new Task("Task", "task from create method", null, Duration.ofMinutes(10));
        manager.createSimpleTask(newTask);
        assertNotNull(manager.getSimpleTaskById(newTask.getId()));
        assertEquals(manager.getSimpleTaskById(newTask.getId()), newTask);
        assertEquals(SIMPLE_TASKS.size() + 1, manager.getSimpleTasks().size());

        Task intersectedTask = new Task("IntersectedTask", "task with start time intersection", LocalDateTime.now(), Duration.ofMinutes(10));
        ManagerSaveException ex = assertThrows(ManagerSaveException.class, () ->
                manager.createSimpleTask(intersectedTask)
        );
        assertEquals("Время выполнения пересекается с другой задачей", ex.getMessage());
    }

    @Test
    void updateSimpleTask() {
        Task taskBeforeUpdate = manager.getSimpleTaskById(SIMPLE_TASK_1_ID);
        manager.updateSimpleTask(new Task(taskBeforeUpdate.getId(), "updated Name", taskBeforeUpdate.getStatus(), "updated desc", taskBeforeUpdate.getStartTime(), taskBeforeUpdate.getDuration()));
        Task updatedTask = manager.getSimpleTaskById(taskBeforeUpdate.getId());
        assertNotEquals(taskBeforeUpdate, updatedTask);
        assertEquals(SIMPLE_TASKS.size(), manager.getSimpleTasks().size());

        updatedTask.setStartTime(LocalDateTime.now().plusMinutes(20));
        ManagerSaveException ex = assertThrows(ManagerSaveException.class, () -> manager.updateSimpleTask(updatedTask));
        assertEquals("Время выполнения пересекается с другой задачей", ex.getMessage());
    }

    @Test
    void deleteSimpleTask() {
        manager.deleteSimpleTask(SIMPLE_TASK_1_ID);
        assertNull(manager.getSimpleTaskById(SIMPLE_TASK_1_ID));
        assertEquals(SIMPLE_TASKS.size() - 1, manager.getSimpleTasks().size());

        manager.deleteSimpleTask(NOT_EXIST_ID);
        assertEquals(SIMPLE_TASKS.size() - 1, manager.getSimpleTasks().size());
    }

    @Test
    void createEpicTask() {
        Epic newEpic = new Epic("Epic", "epic from create method", LocalDateTime.now(), Duration.ofMinutes(10));
        manager.createEpicTask(newEpic);
        assertNotNull(manager.getEpicTaskById(newEpic.getId()));
        assertEquals(newEpic, manager.getEpicTaskById(newEpic.getId()));
        assertEquals(EPIC_TASKS.size() + 1, manager.getEpicTasks().size());
    }

    @Test
    void updateEpicTask() {
        Epic epicBeforeUpdate = manager.getEpicTaskById(EPIC_TASK_2_ID);
        manager.updateEpicTask(new Epic(epicBeforeUpdate.getId(), "Epic", NEW, "updated epic task", epicBeforeUpdate.getStartTime(), epicBeforeUpdate.getDuration()));
        Epic updatedEpic = manager.getEpicTaskById(epicBeforeUpdate.getId());
        assertNotEquals(epicBeforeUpdate, updatedEpic);
        assertEquals(EPIC_TASKS.size(), manager.getEpicTasks().size());
    }

    @Test
    void deleteEpicTask() {
        manager.deleteEpicTask(EPIC_TASK_1_ID);
        assertNull(manager.getEpicTaskById(EPIC_TASK_1_ID));
        assertEquals(EPIC_TASKS.size() - 1, manager.getEpicTasks().size());

        manager.deleteEpicTask(NOT_EXIST_ID);
        assertEquals(EPIC_TASKS.size() - 1, manager.getEpicTasks().size());
    }

    @Test
    void createSubTask() {
        SubTask newSubTask = new SubTask("Sub task", "sub task from create method", null, Duration.ofMinutes(10), EPIC_TASK_1_ID);
        manager.createSubTask(newSubTask);
        assertNotNull(manager.getSubTaskById(newSubTask.getId()));
        assertEquals(newSubTask, manager.getSubTaskById(newSubTask.getId()));
        assertEquals(SUB_TASKS.size() + 1, manager.getSubTasks().size());

        SubTask intersectedTask = new SubTask("IntersectedTask", "sub task with start time intersection", LocalDateTime.now().minusMinutes(30), Duration.ofMinutes(90), EPIC_TASK_1_ID);
        ManagerSaveException ex = assertThrows(ManagerSaveException.class, () -> manager.createSubTask(intersectedTask));
        assertEquals("Время выполнения пересекается с другой задачей", ex.getMessage());
    }

    @Test
    void updateSubTask() {
        SubTask subTaskBeforeUpdate = manager.getSubTaskById(SUB_TASK_1_ID);
        manager.updateSubTask(new SubTask(subTaskBeforeUpdate.getId(), "sub", NEW, "updated sub task", subTaskBeforeUpdate.getStartTime(), subTaskBeforeUpdate.getDuration(), EPIC_TASK_1_ID));
        SubTask updatedTask = manager.getSubTaskById(subTaskBeforeUpdate.getId());
        assertNotEquals(subTaskBeforeUpdate, updatedTask);
        assertEquals(SUB_TASKS.size(), manager.getSubTasks().size());

        updatedTask.setStartTime(LocalDateTime.now());
        ManagerSaveException ex = assertThrows(ManagerSaveException.class, () -> manager.updateSubTask(updatedTask));
        assertEquals("Время выполнения пересекается с другой задачей", ex.getMessage());
    }

    @Test
    void deleteSubTask() {
        manager.deleteSubTask(SUB_TASK_1_ID);
        assertNull(manager.getSubTaskById(SUB_TASK_1_ID));
        assertEquals(SUB_TASKS.size() - 1, manager.getSubTasks().size());

        manager.deleteSubTask(NOT_EXIST_ID);
        assertEquals(SUB_TASKS.size() - 1, manager.getSubTasks().size());
    }

    @Test
    void getEpicSubTasks() {
        List<SubTask> epicSubTasks = manager.getEpicSubTasks(EPIC_TASK_1_ID);
        assertEquals(epicSubTasks, SUB_TASKS);

        assertTrue(manager.getEpicSubTasks(NOT_EXIST_ID).isEmpty());
    }

    @Test
    void getPrioritizedTasks() {
        int i = 0;
        for (Task task : manager.getPrioritizedTasks()) {
            assertEquals(PRIORITIZED_TASKS.get(i++), task);
        }
    }

    @Test
    void checkEpicStatusCalculating() {
        Epic newEpic = new Epic("new Epic", "epic for testing", null, Duration.ofMinutes(10));
        manager.createEpicTask(newEpic);
        manager.createSubTask(new SubTask("new Sub 1", "sub 1", null, Duration.ofMinutes(10), newEpic.getId()));
        manager.createSubTask(new SubTask("new Sub 2", "sub 2", null, Duration.ofMinutes(10), newEpic.getId()));
        manager.createSubTask(new SubTask("new Sub 3", "sub 3", null, Duration.ofMinutes(10), newEpic.getId()));

        List<SubTask> subTasks = manager.getEpicSubTasks(newEpic.getId());
        assertTrue(subTasks.stream().allMatch(subTask -> subTask.getStatus() == NEW));
        assertEquals(newEpic.getStatus(), NEW);

        subTasks.get(0).setStatus(DONE);
        manager.updateSubTask(subTasks.get(0));
        assertEquals(newEpic.getStatus(), IN_PROGRESS);

        subTasks.forEach(subTask -> {
            subTask.setStatus(DONE);
            manager.updateSubTask(subTask);
        });
        assertEquals(newEpic.getStatus(), DONE);

        subTasks.forEach(subTask -> {
            subTask.setStatus((IN_PROGRESS));
            manager.updateSubTask(subTask);
        });
        assertEquals(newEpic.getStatus(), IN_PROGRESS);

        manager.clearSubTasks();
        assertEquals(newEpic.getStatus(), NEW);
    }
}