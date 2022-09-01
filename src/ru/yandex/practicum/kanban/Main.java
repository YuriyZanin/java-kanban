package ru.yandex.practicum.kanban;

import ru.yandex.practicum.kanban.model.Status;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.service.InMemoryTaskManager;
import ru.yandex.practicum.kanban.service.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager manager = new InMemoryTaskManager();

        Task firstSimpleTask = new Task("simpleTask1", "test simple task 1");
        Task secondSimpleTask = new Task("simpleTask2", "test simple task 2");
        manager.createSimpleTask(firstSimpleTask);
        manager.createSimpleTask(secondSimpleTask);

        Epic firstEpicTask = new Epic("epicTask1", "test epic task 1");
        manager.createEpicTask(firstEpicTask);
        SubTask subTask1 = new SubTask("sub1", "first sub of first epic task", firstEpicTask.getId());
        SubTask subTask2 = new SubTask("sub2", "second sub of first epic task", firstEpicTask.getId());
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        Epic secondEpicTask = new Epic("epicTask2", "test epic task 2");
        manager.createEpicTask(secondEpicTask);
        SubTask subTask3 = new SubTask("sub", "sub of second epic", secondEpicTask.getId());
        manager.createSubTask(subTask3);

        printTaskInfo(manager);

        firstSimpleTask.setStatus(Status.IN_PROGRESS);
        secondSimpleTask.setStatus(Status.DONE);
        manager.updateSimpleTask(firstSimpleTask);
        manager.updateSimpleTask(secondSimpleTask);

        firstEpicTask.setStatus(Status.DONE); // Должен выдать ошибку
        subTask1.setStatus(Status.IN_PROGRESS);
        subTask2.setStatus(Status.DONE);
        subTask3.setStatus(Status.DONE);
        manager.updateSubTask(subTask1);
        manager.updateSubTask(subTask2);
        manager.updateSubTask(subTask3);

        System.out.println("После смены статусов:");
        printTaskInfo(manager);

        manager.deleteSimpleTask(firstSimpleTask.getId());
        manager.deleteEpicTask(firstEpicTask.getId());
        manager.deleteSubTask(subTask3.getId());

        System.out.println("После удаления:");
        printTaskInfo(manager);
    }

    public static void printTaskInfo(TaskManager manager) {
        for (Task simpleTask : manager.getSimpleTasks()) {
            System.out.println(simpleTask);
        }
        for (Epic epic : manager.getEpicTasks()) {
            System.out.println(epic);
        }
        for (SubTask subTask : manager.getSubTasks()) {
            System.out.println(subTask);
        }
    }
}