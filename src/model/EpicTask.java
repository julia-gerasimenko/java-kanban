package model;

import java.util.ArrayList;
import java.util.List;

public class EpicTask extends Task {
    private TaskStatus taskStatus; // "NEW" / "IN_PROGRESS" / "DONE"
    private List<SubTask> subTasks;

    public EpicTask(String taskName, Integer taskId) {
        super(taskName, taskId);
        this.subTasks = new ArrayList<>();
    }

    @Override
    public TaskStatus getTaskStatus() {
        // TODO
        return TaskStatus.NEW;
    }
}
