package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.exeption.ManagerLoadException;
import ru.yandex.practicum.kanban.exeption.ManagerSaveException;
import ru.yandex.practicum.kanban.model.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path path;

    public FileBackedTaskManager(Path path) {
        super();
        this.path = path;
    }

    public FileBackedTaskManager(String directory, String fileName) {
        super();
        this.path = Paths.get(directory, fileName);
    }

    public static void main(String[] args) {
        final String HOME = System.getProperty("user.home");
        TaskManager fileManager = new FileBackedTaskManager(HOME, "task_data.csv");
        Task simple1 = new Task("Simple1", "simple task 1");
        fileManager.createSimpleTask(simple1);
        Epic epic1 = new Epic("Epic1", "epic task 1");
        fileManager.createEpicTask(epic1);
        SubTask subTask1 = new SubTask("SubTask1", "sub task 1", epic1.getId());
        fileManager.createSubTask(subTask1);
        fileManager.getSimpleTaskById(simple1.getId());
        fileManager.getSubTaskById(subTask1.getId());
        fileManager.getEpicTaskById(epic1.getId());
        printHistory(fileManager);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(Paths.get(HOME, "task_data.csv").toFile());
        printHistory(loadedManager);
    }

    public static void printHistory(TaskManager manager) {
        System.out.println("История просмотра задач:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager loadedManager = new FileBackedTaskManager(file.toPath());
        try {
            String fileContent = Files.readString(file.toPath());
            String[] lines = fileContent.split("\n");

            int maxId = 0;
            boolean isHistoryStringExists = false;
            Map<Integer, Task> loadedTasks = new HashMap<>();
            for (String line : Arrays.copyOfRange(lines, 1, lines.length)) {
                if (line.isEmpty()) {
                    isHistoryStringExists = true;
                    break;
                }

                Task task = loadedManager.fromString(line);
                if (task.getId() > maxId) {
                    maxId = task.getId();
                }
                loadedTasks.put(task.getId(), task);
            }
            loadedManager.setCurrentId(maxId + 1);

            if (isHistoryStringExists) {
                List<Integer> historyIds = historyFromString(lines[lines.length - 1]);
                for (Integer historyId : historyIds) {
                    loadedManager.getHistoryManager().add(loadedTasks.get(historyId));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка", e);
        } catch (IllegalArgumentException e) {
            throw new ManagerLoadException(String.format("Файл %s не содержит данных для загрузки", file.getAbsolutePath()));
        }

        return loadedManager;
    }

    private static String historyToString(HistoryManager manager) {
        List<String> historyIds = new ArrayList<>();
        for (Task task : manager.getHistory()) {
            historyIds.add(task.getId().toString());
        }
        return String.join(",", historyIds);
    }

    private static List<Integer> historyFromString(String value) {
        if (value.isEmpty()) {
            return Collections.emptyList();
        }

        String[] data = value.split(",");
        List<Integer> ids = new ArrayList<>();
        for (String id : data) {
            ids.add(Integer.parseInt(id));
        }
        return ids;
    }

    private Task fromString(String value) throws ManagerLoadException {
        String[] data = value.split(",");

        Integer id = Integer.parseInt(data[0]);
        TaskType type = TaskType.valueOf(data[1]);
        String name = data[2];
        Status status = Status.valueOf(data[3]);
        String description = data[4];

        switch (type) {
            case TASK:
                Task task = new Task(id, name, status, description);
                super.createSimpleTask(task);
                task.setId(id);
                return task;
            case EPIC:
                Epic epic = new Epic(id, name, status, description);
                super.createEpicTask(epic);
                return epic;
            case SUBTASK:
                SubTask subTask = new SubTask(id, name, status, description, Integer.parseInt(data[5]));
                super.createSubTask(subTask);
                return subTask;
            default:
                throw new ManagerLoadException("Ошибка определения типа задания " + value);
        }
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
            writer.write("id,type,name,status,description,epic\n");
            writeTasks(writer, getSimpleTasks());
            writeTasks(writer, getEpicTasks());
            writeTasks(writer, getSubTasks());
            writer.write("\n");
            writer.write(historyToString(getHistoryManager()));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранеии данных в файл");
        }
    }

    private <T extends Task> void writeTasks(BufferedWriter writer, List<T> tasks) throws IOException {
        for (T task : tasks) {
            writer.write(task.toString() + "\n");
        }
    }

    @Override
    public void clearSimpleTasks() {
        super.clearSimpleTasks();
        save();
    }

    @Override
    public void clearEpicTasks() {
        super.clearEpicTasks();
        save();
    }

    @Override
    public void clearSubTasks() {
        super.clearSubTasks();
        save();
    }

    @Override
    public void createSimpleTask(Task task) {
        super.createSimpleTask(task);
        save();
    }

    @Override
    public void updateSimpleTask(Task task) {
        super.updateSimpleTask(task);
        save();
    }

    @Override
    public void deleteSimpleTask(int id) {
        super.deleteSimpleTask(id);
        save();
    }

    @Override
    public void createEpicTask(Epic epic) {
        super.createEpicTask(epic);
        save();
    }

    @Override
    public void updateEpicTask(Epic epic) {
        super.updateEpicTask(epic);
        save();
    }

    @Override
    public void deleteEpicTask(int id) {
        super.deleteEpicTask(id);
        save();
    }

    @Override
    public void createSubTask(SubTask subTask) {
        super.createSubTask(subTask);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        save();
    }

    @Override
    public Task getSimpleTaskById(int id) {
        Task simpleTask = super.getSimpleTaskById(id);
        save();
        return simpleTask;
    }

    @Override
    public Epic getEpicTaskById(int id) {
        Epic epicTask = super.getEpicTaskById(id);
        save();
        return epicTask;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = super.getSubTaskById(id);
        save();
        return subTask;
    }
}