package ru.yandex.practicum.kanban.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.kanban.utils.TestData.*;

class EpicTest {

    @BeforeEach
    void setUp() {
        EPIC_TASK_1.addSubTask(SUB_TASK_1);
        EPIC_TASK_1.addSubTask(SUB_TASK_2);
        EPIC_TASK_1.addSubTask(SUB_TASK_3);
    }

    @AfterEach
    void tearDown() {
        EPIC_TASK_1.clearSubTasks();
    }

    @Test
    void addSubTask() {
        EPIC_TASK_1.addSubTask(new SubTask(5, "Sub task 5", Status.NEW, "sub task from add method", EPIC_TASK_1_ID));
        assertEquals(4, EPIC_TASK_1.getSubTaskIds().size());
    }

    @Test
    void deleteSubTask() {
        EPIC_TASK_1.deleteSubTask(SUB_TASK_3);
        assertEquals(2, EPIC_TASK_1.getSubTaskIds().size());
    }

    @Test
    void clearSubTasks() {
        EPIC_TASK_1.clearSubTasks();
        assertTrue(EPIC_TASK_1.getSubTaskIds().isEmpty());
    }

    @Test
    void getSubTaskIds() {
        assertEquals(List.of(SUB_TASK_1_ID, SUB_TASK_2_ID, SUB_TASK_3_ID), EPIC_TASK_1.getSubTaskIds());
    }
}