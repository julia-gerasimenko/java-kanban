import model.*;
import service.TaskManager;

public class Main {
    public static void main(String[] args) {
        SingleTask singleTask = new SingleTask("Pure task", null, TaskStatus.NEW);
        EpicTask epicTask = new EpicTask("Epic task", null);
        SubTask subTask = new SubTask("Sub task", null, TaskStatus.NEW, epicTask);

        TaskManager taskManager = new TaskManager();
        SingleTaskToCreate singleTaskToCreate = new SingleTaskToCreate("Pure safe task");
        taskManager.saveNewTask(singleTaskToCreate);
    }
}
