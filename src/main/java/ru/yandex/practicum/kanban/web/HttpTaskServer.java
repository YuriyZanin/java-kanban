package ru.yandex.practicum.kanban.web;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.service.TaskManager;
import ru.yandex.practicum.kanban.utils.JsonUtil;
import ru.yandex.practicum.kanban.utils.Managers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class HttpTaskServer {
    private final HttpServer server;
    private final TaskManager manager;

    private final Gson gson;

    public HttpTaskServer() throws IOException {
        manager = Managers.getFileBackedTaskManager();
        gson = JsonUtil.getGsonInstance();
        server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/tasks", this::handleRequest);
    }

    private void handleRequest(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath().endsWith("/")
                ? exchange.getRequestURI().getPath()
                : exchange.getRequestURI().getPath() + "/";

        Map<String, String> query = queryToMap(exchange.getRequestURI().getQuery());

        int respCode = 200;
        String response;

        switch (method) {
            case "GET":
                exchange.getResponseHeaders().add("Content-Type", "application/json");

                if (path.endsWith("/tasks/")) {
                    response = gson.toJson(manager.getPrioritizedTasks());
                } else if (path.endsWith("/tasks/history/")) {
                    response = gson.toJson(manager.getHistory());
                } else if (path.endsWith("/tasks/task/")) {
                    response = query == null ? gson.toJson(manager.getSimpleTasks())
                            : gson.toJson(manager.getSimpleTaskById(Integer.parseInt(query.get("id"))));
                } else if (path.endsWith("/tasks/subtask/")) {
                    response = query == null ? gson.toJson(manager.getSubTasks())
                            : gson.toJson(manager.getSubTaskById(Integer.parseInt(query.get("id"))));
                } else if (path.endsWith("/tasks/epic/")) {
                    response = query == null ? gson.toJson(manager.getEpicTasks())
                            : gson.toJson(manager.getEpicTaskById(Integer.parseInt(query.get("id"))));
                } else if (path.endsWith("/subtask/epic/")) {
                    response = gson.toJson(manager.getEpicSubTasks(Integer.parseInt(query.get("id"))));
                } else {
                    response = "Неверный запрос";
                    respCode = 400;
                }

                try (OutputStream outputStream = exchange.getResponseBody()) {
                    if (response.equals("null")) {
                        respCode = 404;
                        exchange.sendResponseHeaders(respCode, 0);
                    } else {
                        exchange.sendResponseHeaders(respCode, response.getBytes().length);
                        outputStream.write(response.getBytes());
                    }
                }
                break;

            case "POST":
                byte[] bytes = exchange.getRequestBody().readAllBytes();
                respCode = 201;

                try {
                    if (path.endsWith("/tasks/task/")) {
                        Task task = gson.fromJson(new String(bytes), Task.class);
                        if (task.getId() == null || manager.getSimpleTaskById(task.getId()) == null) {
                            manager.createSimpleTask(task);
                        } else {
                            manager.updateSimpleTask(task);
                        }
                    } else if (path.endsWith("/tasks/epic/")) {
                        Epic epic = gson.fromJson(new String(bytes), Epic.class);
                        if (epic.getId() == null || manager.getEpicTaskById(epic.getId()) == null) {
                            manager.createEpicTask(epic);
                        } else {
                            manager.updateEpicTask(epic);
                        }
                    } else if (path.endsWith("/tasks/subtask/")) {
                        SubTask subTask = gson.fromJson(new String(bytes), SubTask.class);
                        if (subTask.getId() == null || manager.getSubTaskById(subTask.getId()) == null) {
                            manager.createSubTask(subTask);
                        } else {
                            manager.updateSubTask(subTask);
                        }
                    } else {
                        respCode = 400;
                    }
                    exchange.sendResponseHeaders(respCode, 0);
                    exchange.close();
                } catch (RuntimeException e) {
                    String message = e.getMessage() == null ? "Ошика при выполнении запроса" : e.getMessage();
                    exchange.sendResponseHeaders(400, message.getBytes().length);
                    exchange.getResponseBody().write(message.getBytes());
                    exchange.close();
                }
                break;

            case "DELETE":
                if (path.endsWith("/tasks/task/")) {
                    if (query == null) {
                        manager.clearSimpleTasks();
                    } else {
                        manager.deleteSimpleTask(Integer.parseInt(query.get("id")));
                    }
                } else if (path.endsWith("/tasks/epic/")) {
                    if (query == null) {
                        manager.clearEpicTasks();
                    } else {
                        manager.deleteEpicTask(Integer.parseInt(query.get("id")));
                    }
                } else if (path.endsWith("/tasks/subtask/")) {
                    if (query == null) {
                        manager.clearSubTasks();
                    } else {
                        manager.deleteSubTask(Integer.parseInt(query.get("id")));
                    }
                } else {
                    respCode = 400;
                }

                exchange.sendResponseHeaders(respCode, 0);
                exchange.getResponseBody().close();
                break;

            default:
                exchange.sendResponseHeaders(400, 0);
                try (OutputStream outputStream = exchange.getResponseBody()) {
                    outputStream.write("Неверный запрос".getBytes());
                }
        }
    }

    // https://stackoverflow.com/questions/11640025/how-to-obtain-the-query-string-in-a-get-with-java-httpserver-httpexchange
    private Map<String, String> queryToMap(String query) {
        if (query == null) {
            return null;
        }
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }

    public TaskManager getManager() {
        return manager;
    }

    public void startServer() {
        this.server.start();
    }

    public void stopServer() {
        this.server.stop(1);
    }
}
