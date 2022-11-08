package ru.yandex.practicum.kanban.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.kanban.utils.TestData;
import ru.yandex.practicum.kanban.web.KVServer;

import java.io.IOException;

public class HttpTaskManagerTest extends TaskManagerTest<HTTPTaskManager> {
    final String KV_SERVER_URL = "http://localhost:8078";

    KVServer kvServer;

    @BeforeEach
    void setUp() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        manager = new HTTPTaskManager(KV_SERVER_URL);
        TestData.setUpTestData(manager);
    }

    @AfterEach
    void tearDown() {
        kvServer.stop();
    }
}
