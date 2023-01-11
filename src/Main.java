import model.*;
import service.TaskManager;
import service.ToCreate;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        // создаем новую задачу
        ToCreate toCreateSingle = new ToCreate("Simple task", "Just do it"); // 0
        // сохраняем новую задачу
        taskManager.saveSingleTask(toCreateSingle);
        // получение задачи по идентификатору
        System.out.println(taskManager.getTaskById(0));

        // меняем статус сингл таск и выводим на печать
        // тут не поянятно в скобках (SingleTask) - разобраться
        SingleTask singleTask = (SingleTask) taskManager.getTaskById(0);
        taskManager.update(singleTask.withNewTaskStatus(TaskStatus.IN_PROGRESS));
        System.out.println(taskManager.getTaskById(0));

        // создаем и сохраняем эпик
        ToCreate toCreateEpic = new ToCreate("BIG Epic task", "Step by step"); // 1
        taskManager.saveEpicTask(toCreateEpic);
        // выводим на печать эпик таск с пока что пустым массивом и статусом
        System.out.println(taskManager.getTaskById(1));

        // создаем и сохраняем две подзадачи в ArrayList Эпика id 1
        EpicTask epicTask = (EpicTask) taskManager.getTaskById(1);
        ToCreate toCreateSub = new ToCreate("BIG Epic's subTask 1", "The first Step");
        taskManager.saveSubTask(toCreateSub, epicTask); // 1002
        ToCreate toCreateSubTwo = new ToCreate("BIG Epic's subTask 2", "The second Step");
        taskManager.saveSubTask(toCreateSubTwo, epicTask); // 1003

        // выводим на печать 1002 и 1003 SubTask
        System.out.println(taskManager.getTaskById(1002));
        System.out.println(taskManager.getTaskById(1003));

        // меняем статус SubTask 1002 НА IN_PROGRESS и печатаем
        SubTask subTask = (SubTask) taskManager.getTaskById(1002);
        taskManager.update(subTask.withNewTaskStatus(TaskStatus.IN_PROGRESS));
        System.out.println(taskManager.getTaskById(1002));
        // выводим на печать конкретный эпик таск с подзадачами с ИЗМЕНЕННЫМ СТАТУСОМ
        System.out.println(taskManager.getTaskById(1));

        // получение списка всех задач всех типов
        System.out.println(taskManager.getAllTasks());

        // Получение списка задач по типам: простые, эпики
        // ??? Подзадачи нет смысла выводить отдельным общим списком отдельно от эпиков???
        System.out.println(taskManager.TasksByType(Type.SINGLE));
        System.out.println(taskManager.TasksByType(Type.EPIC));

        // получение списка всех подзадач определенного эпика
        System.out.println(taskManager.TasksForEpic(1));

        // удаление задачи по идентификатору и проверка
        taskManager.deleteTaskById(0);
        System.out.println(taskManager.TasksByType(Type.SINGLE));


        // удаление всех задач по типам: простые, эпики
        // удаляем созданную single task
        ToCreate toCreateSingleTwo = new ToCreate("Second Simple task", "Just do it again");//4
        taskManager.saveSingleTask(toCreateSingleTwo);
        System.out.println(taskManager.TasksByType(Type.SINGLE));
        taskManager.deleteTaskByType(Type.SINGLE);
        System.out.println(taskManager.TasksByType(Type.SINGLE));

        // удаление всех подзадач определенного эпика
        taskManager.deleteTaskForEpicId(1);
        System.out.println(taskManager.TasksByType(Type.EPIC));

        // удаление всех задач
        taskManager.deleteAllTasks();
        System.out.println(taskManager.getAllTasks());
    }
}