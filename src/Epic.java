import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<SubTask> subTasks;

    public Epic(String name, String description) {
        super(name, description);
        this.subTasks = new ArrayList<>();
    }

    public void addSubTask(SubTask subTask) {
        subTasks.add(subTask);
    }

    public void deleteSubTask(SubTask subTask) {
        subTasks.remove(subTask);
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", tasks.size()=" + subTasks.size() +
                '}';
    }

    @Override
    public void setStatus(Status status) {
        System.err.println("Нельзя напрямую изменить статус эпику");
    }

    public void updateStatus() {
        if (subTasks.isEmpty()) {
            super.setStatus(Status.NEW);
        } else {
            boolean isExistNew = false;
            boolean isExistDone = false;
            boolean isExistInProgress = false;
            for (SubTask subTask : subTasks) {
                if (subTask.getStatus() == Status.NEW) {
                    isExistNew = true;
                } else if (subTask.getStatus() == Status.DONE) {
                    isExistDone = true;
                } else {
                    isExistInProgress = true;
                }
            }
            if (isExistInProgress || (isExistNew && isExistDone)) {
                super.setStatus(Status.IN_PROGRESS);
            } else if (!isExistNew) {
                super.setStatus(Status.DONE);
            } else {
                super.setStatus(Status.NEW);
            }
        }
    }
}
