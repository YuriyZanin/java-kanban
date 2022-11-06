package ru.yandex.practicum.kanban.web;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Status;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.service.InMemoryTaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.kanban.utils.JsonUtil.*;
import static ru.yandex.practicum.kanban.utils.TestData.*;


public class HttpTaskServerTest {
    static HttpTaskServer server;
    static HttpClient client;

    static Gson gson;

    @BeforeAll
    static void beforeAll() throws IOException {
        server = new HttpTaskServer();
        server.startServer();
        client = HttpClient.newHttpClient();
        gson = getGsonInstance();
    }

    @AfterAll
    static void afterAll() {
        server.stopServer();
    }

    @BeforeEach
    void setUp() {
        setUpTestData((InMemoryTaskManager) server.getManager());
    }

    @AfterEach
    void tearDown() {
        server.getManager().clearSimpleTasks();
        server.getManager().clearEpicTasks();
        server.getManager().clearSubTasks();
        ((InMemoryTaskManager) (server.getManager())).clearHistory();
    }

    @Test
    void shouldReturnPrioritizedTasks() {
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = sendRequest(request);
        List<Task> tasks = parseJsonArrayOfTasks(response.body());
        assertEquals(PRIORITIZED_TASKS, tasks);
    }

    @Test
    void shouldReturnHistory() {
        URI url = URI.create("http://localhost:8080/tasks/history/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = sendRequest(request);
        List<Task> tasks = parseJsonArrayOfTasks(response.body());
        assertEquals(List.of(SIMPLE_TASK_1, SIMPLE_TASK_2, EPIC_TASK_1, SUB_TASK_2), tasks);
    }

    @Test
    void shouldReturnSimpleTasks() {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = sendRequest(request);
        List<Task> simpleTasks = gson.fromJson(response.body(), SIMPLE_TASK_COLLECTION_TOKEN_TYPE.getType());
        assertEquals(200, response.statusCode());
        assertEquals(SIMPLE_TASKS, simpleTasks);
    }

    @Test
    void shouldCreateSimpleTask() {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        Task newTask = new Task(LAST_ID, "newTask", Status.NEW, "desc", null, Duration.ZERO);
        String json = gson.toJson(newTask);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = sendRequest(request);
        assertEquals(201, response.statusCode());

        url = URI.create("http://localhost:8080/tasks/task/?id=" + LAST_ID);
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = sendRequest(request);
        Task task = gson.fromJson(response.body(), Task.class);
        assertEquals(newTask, task);
    }

    @Test
    void shouldGetSimpleTaskById() {
        URI url = URI.create("http://localhost:8080/tasks/task/?id=" + SIMPLE_TASK_1_ID);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = sendRequest(request);
        Task task = gson.fromJson(response.body(), Task.class);
        assertEquals(200, response.statusCode());
        assertEquals(SIMPLE_TASK_1, task);
    }

    @Test
    void shouldDeleteSimpleTaskById() {
        URI url = URI.create("http://localhost:8080/tasks/task/?id=" + SIMPLE_TASK_1_ID);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = sendRequest(request);
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = sendRequest(request);
        assertEquals(404, response.statusCode());
    }

    @Test
    void shouldDeleteAllSimpleTasks() {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = sendRequest(request);
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = sendRequest(request);
        List<Task> tasks = gson.fromJson(response.body(), SIMPLE_TASK_COLLECTION_TOKEN_TYPE.getType());
        assertTrue(tasks.isEmpty());
    }

    @Test
    void shouldReturnEpics() {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = sendRequest(request);
        List<Epic> epics = gson.fromJson(response.body(), EPIC_TASK_COLLECTION_TOKEN_TYPE.getType());
        assertEquals(200, response.statusCode());
        assertEquals(EPIC_TASKS, epics);
    }

    @Test
    void shouldCreateEpic() {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Epic newEpic = new Epic(LAST_ID, "newTask", Status.NEW, "epic", null, Duration.ZERO);
        String json = gson.toJson(newEpic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = sendRequest(request);
        assertEquals(201, response.statusCode());

        url = URI.create("http://localhost:8080/tasks/epic/?id=" + LAST_ID);
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = sendRequest(request);
        Epic epic = gson.fromJson(response.body(), Epic.class);
        assertEquals(newEpic, epic);
    }

    @Test
    void shouldGetEpicById() {
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=" + EPIC_TASK_1_ID);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = sendRequest(request);
        Epic task = gson.fromJson(response.body(), Epic.class);
        assertEquals(200, response.statusCode());
        assertEquals(EPIC_TASK_1, task);
    }

    @Test
    void shouldDeleteEpicById() {
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=" + EPIC_TASK_1_ID);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = sendRequest(request);
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = sendRequest(request);
        assertEquals(404, response.statusCode());
    }

    @Test
    void shouldDeleteAllEpics() {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = sendRequest(request);
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = sendRequest(request);
        List<Epic> epics = gson.fromJson(response.body(), EPIC_TASK_COLLECTION_TOKEN_TYPE.getType());
        assertTrue(epics.isEmpty());
    }

    @Test
    void shouldReturnSubTasks() {
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = sendRequest(request);
        List<SubTask> subTasks = gson.fromJson(response.body(), SUB_TASK_COLLECTION_TOKEN_TYPE.getType());
        assertEquals(200, response.statusCode());
        assertEquals(SUB_TASKS, subTasks);
    }

    @Test
    void shouldCreateSubTask() {
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        SubTask newTask = new SubTask(LAST_ID, "newTask", Status.NEW, "epic", null, Duration.ZERO, EPIC_TASK_1_ID);
        String json = gson.toJson(newTask);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = sendRequest(request);
        assertEquals(201, response.statusCode());

        url = URI.create("http://localhost:8080/tasks/subtask/?id=" + LAST_ID);
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = sendRequest(request);
        SubTask subTask = gson.fromJson(response.body(), SubTask.class);
        assertEquals(newTask, subTask);
    }

    @Test
    void shouldGetSubTaskById() {
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=" + SUB_TASK_1_ID);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = sendRequest(request);
        SubTask task = gson.fromJson(response.body(), SubTask.class);
        assertEquals(200, response.statusCode());
        assertEquals(SUB_TASK_1, task);
    }

    @Test
    void shouldReturnSubTasksByEpicId() {
        URI url = URI.create("http://localhost:8080/tasks/subtask/epic/?id=" + EPIC_TASK_1_ID);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = sendRequest(request);
        List<Task> tasks = gson.fromJson(response.body(), SUB_TASK_COLLECTION_TOKEN_TYPE.getType());
        assertEquals(200, response.statusCode());
        assertEquals(SUB_TASKS, tasks);
    }

    @Test
    void shouldDeleteSubTaskById() {
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=" + SUB_TASK_1_ID);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = sendRequest(request);
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = sendRequest(request);
        assertEquals(404, response.statusCode());
    }

    @Test
    void shouldDeleteAllSubTask() {
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = sendRequest(request);
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = sendRequest(request);
        List<SubTask> subTasks = gson.fromJson(response.body(), SUB_TASK_COLLECTION_TOKEN_TYPE.getType());
        assertTrue(subTasks.isEmpty());
    }

    @Test
    void shouldReturnNotExistCode() {
        URI url = URI.create("http://localhost:8080/tasks/task/?id=" + NOT_EXIST_ID);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = sendRequest(request);
        assertEquals(404, response.statusCode());

        url = URI.create("http://localhost:8080/tasks/epic/?id=" + NOT_EXIST_ID);
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = sendRequest(request);
        assertEquals(404, response.statusCode());

        url = URI.create("http://localhost:8080/tasks/subtask/?id=" + NOT_EXIST_ID);
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = sendRequest(request);
        assertEquals(404, response.statusCode());
    }

    private HttpResponse<String> sendRequest(HttpRequest request) {
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }
}
