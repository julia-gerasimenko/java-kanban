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
        System.out.println("Новая SingleTask успешно создана: \r\n" + taskManager.getTaskById(0));

        // меняем статус сингл таск и выводим на печать
        // тут не поянятно в скобках (SingleTask) - разобраться
        SingleTask singleTask = (SingleTask) taskManager.getTaskById(0);
        taskManager.update(singleTask.withNewTaskStatus(TaskStatus.IN_PROGRESS));
        System.out.println("Статус SingleTask услпешно обновлен: \r\n"
                + taskManager.getTaskById(0));

        // создаем и сохраняем эпик
        ToCreate toCreateEpic = new ToCreate("BIG Epic task", "Step by step"); // 1
        taskManager.saveEpicTask(toCreateEpic);
        // выводим на печать эпик таск с пока что пустым массивом и статусом
        System.out.println("Новая пустая EpicTask успешно создана: \r\n" + taskManager.getTaskById(1));

        // создаем и сохраняем две подзадачи в ArrayList Эпика id 1
        EpicTask epicTask = (EpicTask) taskManager.getTaskById(1);
        ToCreate toCreateSub = new ToCreate("BIG Epic's subTask 1", "The first Step");
        taskManager.saveSubTask(toCreateSub, epicTask); // 1002
        ToCreate toCreateSubTwo = new ToCreate("BIG Epic's subTask 2", "The second Step");
        taskManager.saveSubTask(toCreateSubTwo, epicTask); // 1003

        // выводим на печать 2 и 3 SubTask
        System.out.println("1я подзадача Эпика 1 успешно создана: \r\n" + taskManager.getTaskById(2));
        System.out.println("2я подзадача Эпика 1 успешно создана: \r\n" + taskManager.getTaskById(3));

        // меняем статус SubTask 1002 НА IN_PROGRESS и печатаем
        SubTask subTask = (SubTask) taskManager.getTaskById(2);
        taskManager.update(subTask.withNewTaskStatus(TaskStatus.IN_PROGRESS));
        System.out.println("Статус 1й подзадачи Эпика 1 услпешно обновлен: \r\n"
                + taskManager.getTaskById(2));
        // выводим на печать конкретный эпик таск с подзадачами с ИЗМЕНЕННЫМ СТАТУСОМ
        System.out.println("Статус Эпика 1 успешно обновлен: \r\n" + taskManager.getTaskById(1));

        // получение списка всех задач всех типов
        System.out.println("Список всех задач: " + taskManager.getAllTasks());

        // Получение списка задач по типам: простые, эпики
        // ??? Подзадачи нет смысла выводить отдельным общим списком отдельно от эпиков???
        System.out.println("Список всех задач типа Single: " + taskManager.filterTasksByType(Type.SINGLE));
        System.out.println("Список всех задач типа Epic: " + taskManager.filterTasksByType(Type.EPIC));

        // получение списка всех подзадач определенного эпика
        System.out.println("Список всех подзадач Эпика 1: " + taskManager.getEpicSubTasks(1));

        // удаление задачи по идентификатору и проверка
        taskManager.deleteTaskById(3);
        System.out.println("Список всех подзадач Эпика 1 после удаления подзадачи 3: "
                + taskManager.getEpicSubTasks(1));

        // создаем и сохраняем еще одну SingleTask
        ToCreate toCreateSingleTwo = new ToCreate("Simple task", "Just do it"); // 4
        // сохраняем новую задачу
        taskManager.saveSingleTask(toCreateSingleTwo);
        // получение задачи по идентификатору
        System.out.println("Новая SingleTask успешно создана: \r\n" + taskManager.getTaskById(4));

        // удаление всех задач по типам: простые, эпики
        // удаляем для примера single task
        taskManager.deleteTaskByType(Type.SINGLE);
        System.out.println("удаление всех задач по типу SINGLE успешно произведено: "
                + taskManager.filterTasksByType(Type.SINGLE).isEmpty());

        // удаление всех подзадач определенного эпика
        taskManager.deleteSubTasksForEpic(1);
        System.out.println("удаление всех подзадач эпика 1 успешно произведено: "
                + ((EpicTask) taskManager.getTaskById(1)).getSubTasks().isEmpty());

        // удаление всех задач
        taskManager.deleteAllTasks();
        System.out.println("удаление всех задач успешно произведено: " + taskManager.getAllTasks().isEmpty());
    }
}