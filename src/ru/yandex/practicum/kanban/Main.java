package ru.yandex.practicum.kanban;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.service.TaskManager;
import ru.yandex.practicum.kanban.utils.Managers;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager manager = Managers.getDefault();
        Task simple1 = new Task("Simple1", "simple task 1");
        Task simple2 = new Task("Simple2", "simple task 2");
        manager.createSimpleTask(simple1);
        manager.createSimpleTask(simple2);
        Epic epic1 = new Epic("Epic1", "epic task 1");
        manager.createEpicTask(epic1);
        SubTask subTask1 = new SubTask("SubTask1", "sub task 1", epic1.getId());
        SubTask subTask2 = new SubTask("SubTask2", "sub task 2", epic1.getId());
        SubTask subTask3 = new SubTask("SubTask3", "sub task 3", epic1.getId());
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);
        manager.createSubTask(subTask3);
        Epic epic2 = new Epic("Epic2", "epic task 2");
        manager.createEpicTask(epic2);

        manager.getSimpleTaskById(simple2.getId());
        manager.getSimpleTaskById(simple1.getId());
        manager.getSimpleTaskById(simple2.getId());
        manager.getSubTaskById(subTask2.getId());
        manager.getSubTaskById(subTask3.getId());
        manager.getSubTaskById(subTask1.getId());
        manager.getSubTaskById(subTask3.getId());
        manager.getEpicTaskById(epic1.getId());
        manager.getEpicTaskById(epic1.getId());
        manager.getEpicTaskById(epic2.getId());
        printHistory(manager);

        manager.deleteSimpleTask(simple1.getId());
        manager.deleteEpicTask(epic1.getId());
        System.out.println("После удаления");
        printHistory(manager);
    }

    public static void printHistory(TaskManager manager) {
        System.out.println("История просмотра задач:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
