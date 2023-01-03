package model;

import java.util.ArrayList;
import java.util.List;

public class SubTask extends Task{
    private TaskStatus taskStatus; // "NEW" / "IN_PROGRESS" / "DONE"
    private EpicTask epicTask;

    public SubTask(String taskName, Integer taskId, TaskStatus taskStatus, EpicTask epicTask) {
        super(taskName, taskId);
        this.taskStatus = taskStatus;
        this.epicTask = epicTask;
    }

    @Override
    public TaskStatus getTaskStatus() {
        return taskStatus;
    }
}
