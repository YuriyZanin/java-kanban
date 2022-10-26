package ru.yandex.practicum.kanban.service;

import org.junit.jupiter.api.BeforeEach;

import static ru.yandex.practicum.kanban.utils.TestData.setUpTestData;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
        setUpTestData(manager);
    }
}