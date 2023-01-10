import model.*;
import service.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        // создаем новую задачу
        ToCreate toCreateSingle = new ToCreate("Pure safe task", "Just do it", Type.SINGLE);
        // сохраняем новую задачу
        taskManager.saveSingleTask(toCreateSingle);
        // выводим на печать сингл таск
        System.out.println(taskManager.getTaskById(0));

        // меняем статус сингл таск и выводим на печать
        //тут не поянятно в скобках (SingleTask) - разобраться
        SingleTask singleTask = (SingleTask) taskManager.getTaskById(0);
        taskManager.update(singleTask.withTaskStatus(TaskStatus.IN_PROGRESS));
        System.out.println(taskManager.getTaskById(0));

        // создаем и сохраняем эпик
        ToCreate toCreateEpic = new ToCreate("BIG Epic task", "Step by step", Type.EPIC);
        taskManager.saveEpicTask(toCreateEpic);
        // выводим на печать эпик таск с пока что пустым массивом и статусом
        System.out.println(taskManager.getTaskById(1));

        // создаем и сохраняем две подзадачи в ArrayList Эпика
        EpicTask epicTask = (EpicTask) taskManager.getTaskById(1);
        ToCreate toCreateSub = new ToCreate("BIG Epic's subTask 1", "The first Step", Type.SUB);
        taskManager.saveSubTask(toCreateSub, epicTask);
        ToCreate toCreateSubTwo = new ToCreate("BIG Epic's subTask 2", "The second Step",
                Type.SUB);
        taskManager.saveSubTask(toCreateSubTwo, epicTask);
        // меняем статус toCreateSub НА IN_PROGRESS

        // выводим на печать эпик таск с подзадачами
        System.out.println(taskManager.getTaskById(1));



        // получение списка всех задач всех типов
        System.out.println(taskManager.getAllTasks());

        // Получение списка задач по типам: простые, эпики и получение списка всех подзадач определенного эпика.
        // ??? Подзадачи нет смысла выводить отдельным общим списком отдельно от эпиков???

        // получение задачи по идентификатору

        // удаление задачи по идентификатору

        // удаление всех задач по типам: простые, эпики, подзадачи определенного эпика

        //удаление всех задач всех типов

    }
}