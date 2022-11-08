package ru.yandex.practicum.kanban.web;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final HttpClient client;
    private final String serverUrl;
    private final String token;

    public KVTaskClient(String serverUrl) {
        this.serverUrl = serverUrl;
        this.client = HttpClient.newHttpClient();
        this.token = register();
    }

    public void put(String key, String json) {
        try {
            URI createUri = URI.create(serverUrl + "/save/" + key + "?API_TOKEN=" + token);
            HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
            HttpRequest request = HttpRequest.newBuilder().uri(createUri).POST(body).build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String load(String key) {
        URI loadUri = URI.create(serverUrl + "/load/" + key + "?API_TOKEN=" + token);
        HttpRequest request = HttpRequest.newBuilder().uri(loadUri).GET().build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response == null) {
                throw new RuntimeException("Ответ на запрос не получен");
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return response.body();
    }

    private String register() {
        URI registerUri = URI.create(serverUrl + "/register");
        HttpRequest register = HttpRequest.newBuilder().uri(registerUri).GET().build();
        HttpResponse<String> response;
        try {
            response = client.send(register, HttpResponse.BodyHandlers.ofString());
            if (response == null) {
                throw new RuntimeException("Ответ на запрос не получен");
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return response.body();
    }
}
