public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager manager = new TaskManager();

        Task firstSimpleTask = new Task("simpleTask1", "test simple task 1");
        Task secondSimpleTask = new Task("simpleTask2", "test simple task 2");
        manager.createOrUpdateSimpleTask(firstSimpleTask);
        manager.createOrUpdateSimpleTask(secondSimpleTask);

        Epic firstEpicTask = new Epic("epicTask1", "test epic task 1");
        manager.createOrUpdateEpicTask(firstEpicTask);
        SubTask subTask1 = new SubTask("sub1", "first sub of first epic task", firstEpicTask);
        SubTask subTask2 = new SubTask("sub2", "second sub of first epic task", firstEpicTask);
        manager.createOrUpdateSubTask(subTask1);
        manager.createOrUpdateSubTask(subTask2);

        Epic secondEpicTask = new Epic("epicTask2", "test epic task 2");
        SubTask subTask3 = new SubTask("sub", "sub of second epic", secondEpicTask);
        manager.createOrUpdateEpicTask(secondEpicTask);
        manager.createOrUpdateSubTask(subTask3);

        printTaskInfo(manager);

        firstSimpleTask.setStatus(Status.IN_PROGRESS);
        secondSimpleTask.setStatus(Status.DONE);
        manager.createOrUpdateSimpleTask(firstSimpleTask);
        manager.createOrUpdateSimpleTask(secondSimpleTask);

        firstEpicTask.setStatus(Status.DONE); // Должен выдать ошибку
        subTask1.setStatus(Status.IN_PROGRESS);
        subTask2.setStatus(Status.DONE);
        subTask3.setStatus(Status.DONE);
        manager.createOrUpdateSubTask(subTask1);
        manager.createOrUpdateSubTask(subTask2);
        manager.createOrUpdateSubTask(subTask3);

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
            for (SubTask subTask : epic.getSubTasks()) {
                System.out.println(subTask);
            }
        }
    }
}
