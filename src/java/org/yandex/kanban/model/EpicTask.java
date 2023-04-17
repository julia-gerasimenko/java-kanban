package org.yandex.kanban.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EpicTask extends Task {
    private List<SubTask> subTasks;
    private Status status;
    private LocalDateTime startTime = null;
    private LocalDateTime endTime = null;
    private Long durationInMins = null;


    public EpicTask(String name, int id, String description, List<SubTask> subTasks,
                    Status status) {
        super(name, id, description);
        this.subTasks = subTasks == null ? new ArrayList<>() : subTasks;
        this.status = status;
    }

    @Override
    public Status getStatus() {
        int newStatusQuantity = 0;
        int doneStatusQuantity = 0;

        for (SubTask subTask : subTasks) {
            if (Status.NEW.equals(subTask.getStatus())) {
                newStatusQuantity++;
                if (newStatusQuantity == subTasks.size()) {
                    status = Status.NEW;
                }

            } else if (Status.DONE.equals(subTask.getStatus())) {
                doneStatusQuantity++;
                if (doneStatusQuantity == subTasks.size()) {
                    status = Status.DONE;
                }
            }

            if (newStatusQuantity != subTasks.size() && doneStatusQuantity != subTasks.size()) {
                status = Status.IN_PROGRESS;
            }
        }
        return status;
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<SubTask> subTasks) {
        this.subTasks = subTasks == null ? new ArrayList<>() : subTasks;
        recalculateTimeProperties();
    }
    
    public void recalculateTimeProperties(){
        startTime = null;
        endTime = null;
        durationInMins = subTasks.stream()
                .peek(subTask -> {
                    updateStartTime(subTask.getStartTime());
                    updateEndTime(subTask.getEndTime());
                })
                .map(SubTask::getDurationInMins)
                .filter(Objects::nonNull)
                .mapToLong(Long::longValue)
                .sum();
    }

    private void updateStartTime(LocalDateTime newValue) {
        if (newValue == null) {
            return;
        }
        if (startTime == null) {
            startTime = newValue;
        } else {
            if (startTime.isAfter(newValue)) {
                startTime = newValue;
            }
        }
    }

    private void updateEndTime(LocalDateTime newValue) {
        if (newValue == null) {
            return;
        }
        if (endTime == null) {
            endTime = newValue;
        } else {
            if (endTime.isBefore(newValue)) {
                endTime = newValue;
            }
        }
    }

    @Override
    public Type getType() {
        return Type.EPIC;
    }

    @Override
    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public Long getDurationInMins() {
        return durationInMins;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        EpicTask task = (EpicTask) obj;
        List<SubTask> subsForThis = this.getSubTasks();
        List<SubTask> subsForObj = task.getSubTasks();
        boolean listsAreEqual = false;

        if (subsForThis.isEmpty() && subsForObj.isEmpty()) {
            listsAreEqual = true;
        } else if (subsForThis.size() == subsForObj.size()) {
            int counter = 0;
            for (int i = 0; i < subsForThis.size(); i++) {
                if (subsForThis.get(i).equals(subsForObj.get(i))) {
                    counter++;
                } else {
                    break;
                }
            }
            if (counter == subsForThis.size()) {
                listsAreEqual = true;
            }
        }

        return this.getId() == task.getId()
                && (Objects.equals(this.getName(), task.getName())
                && Objects.equals(this.getDescription(), task.getDescription())
                && this.getStatus() == task.getStatus()
                && listsAreEqual
        );
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "id=" + getId() +
                ", taskStatus=" + getStatus() +
                ", taskName=" + getName() +
                ", taskDescription=" + getDescription() + ",\r\n" +
                "subTasks=" + getSubTasks() +
                '}';
    }
}
