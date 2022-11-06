package ru.yandex.practicum.kanban.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.kanban.utils.TestData;
import ru.yandex.practicum.kanban.web.KVServer;

import java.io.IOException;

public class HttpTaskManagerTest extends TaskManagerTest<HTTPTaskManager> {
    final static String KV_SERVER_URL = "http://localhost:8078";

    static KVServer kvServer;

    @BeforeAll
    static void beforeAll() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
    }

    @AfterAll
    static void afterAll() {
        kvServer.stop();
    }

    @BeforeEach
    void setUp() {
        manager = new HTTPTaskManager(KV_SERVER_URL);
        TestData.setUpTestData(manager);
    }

    @AfterEach
    void tearDown() {
        manager.clearSimpleTasks();
        manager.clearEpicTasks();
        manager.clearSubTasks();
        manager.clearHistory();
    }
}
