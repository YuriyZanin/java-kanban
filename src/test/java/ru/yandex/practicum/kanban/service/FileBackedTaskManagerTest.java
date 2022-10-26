package ru.yandex.practicum.kanban.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.exeption.ManagerLoadException;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.kanban.utils.TestData.setUpTestData;


class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    final static String HOME = System.getProperty("user.home");
    final static String FILE_NAME = "test.csv";

    @BeforeEach
    void setUp() {
        manager = new FileBackedTaskManager(HOME, FILE_NAME);
        setUpTestData(manager);
    }

    @Test
    void loadFromFile() {
        File file = Paths.get(HOME, FILE_NAME).toFile();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(manager.getSimpleTasks(), loadedManager.getSimpleTasks());
        assertEquals(manager.getEpicTasks(), loadedManager.getEpicTasks());
        assertEquals(manager.getSubTasks(), loadedManager.getSubTasks());
        assertEquals(manager.getPrioritizedTasks(), loadedManager.getPrioritizedTasks());
        assertEquals(manager.getHistory(), loadedManager.getHistory());

        manager.clearHistory();

        loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertTrue(loadedManager.getHistory().isEmpty());

        manager.clearSimpleTasks();
        manager.clearSubTasks();
        manager.clearEpicTasks();

        loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertTrue(loadedManager.getSimpleTasks().isEmpty());
        assertTrue(loadedManager.getEpicTasks().isEmpty());
        assertTrue(loadedManager.getSubTasks().isEmpty());

        assertThrows(ManagerLoadException.class, () ->
                FileBackedTaskManager.loadFromFile(Paths.get(HOME, "not_exist_file.csv").toFile())
        );
    }
}