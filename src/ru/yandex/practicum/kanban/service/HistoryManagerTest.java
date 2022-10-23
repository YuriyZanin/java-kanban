package ru.yandex.practicum.kanban.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.utils.Managers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.kanban.utils.TestData.*;

class HistoryManagerTest {

    private static HistoryManager manager;

    @BeforeEach
    void setUp() {
        manager = Managers.getDefaultHistory();
    }

    @Test
    void add() {
        assertTrue(manager.getHistory().isEmpty());

        manager.add(SIMPLE_TASK_1);
        manager.add(SIMPLE_TASK_2);
        assertEquals(SIMPLE_TASKS, manager.getHistory());

        manager.add(SIMPLE_TASK_1);
        assertEquals(List.of(SIMPLE_TASK_2, SIMPLE_TASK_1), manager.getHistory());
    }

    @Test
    void remove() {
        manager.add(EPIC_TASK_1);
        manager.add(EPIC_TASK_2);
        manager.add(SIMPLE_TASK_1);
        manager.add(SIMPLE_TASK_2);

        assertEquals(4, manager.getHistory().size());
        assertEquals(List.of(EPIC_TASK_1, EPIC_TASK_2, SIMPLE_TASK_1, SIMPLE_TASK_2), manager.getHistory());

        manager.remove(EPIC_TASK_1_ID);
        assertEquals(List.of(EPIC_TASK_2, SIMPLE_TASK_1, SIMPLE_TASK_2), manager.getHistory());

        manager.remove(SIMPLE_TASK_1_ID);
        assertEquals(List.of(EPIC_TASK_2, SIMPLE_TASK_2), manager.getHistory());

        manager.remove(SIMPLE_TASK_2_ID);
        assertEquals(List.of(EPIC_TASK_2), manager.getHistory());

        manager.remove(EPIC_TASK_2_ID);
        assertTrue(manager.getHistory().isEmpty());
    }
}