import org.yandex.kanban.model.*;
import org.yandex.kanban.service.Managers;
import org.yandex.kanban.service.TaskCreateDto;
import org.yandex.kanban.service.TaskManager;

import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.taskManager();

        // создаем и сохраняем 1-ю новую простую задачу
        taskManager.saveSingleTask(new TaskCreateDto("Simple task", "Just do it")); //0

        // создаем и сохраняем 2-ю новую простую задачу
        taskManager.saveSingleTask(new TaskCreateDto("Simple 2nd task", "Do it again")); //1

        // создаем и сохраняем 3-ю новую простую задачу
        taskManager.saveSingleTask(new TaskCreateDto("Simple 3rd task", "Again and again")); //2

        // создаем и сохраняем эпик с тремя подзадачами
        taskManager.saveEpicTask(new TaskCreateDto("BIG Epic task", "Step by step")); //3

        taskManager.saveSubTask(new TaskCreateDto("BIG Epic's subTask 1", "The first Step"),
                (EpicTask) taskManager.findTaskById(3)); // 4
        taskManager.saveSubTask(new TaskCreateDto("BIG Epic's subTask 2", "The second Step"),
                (EpicTask) taskManager.findTaskById(3)); // 5
        taskManager.saveSubTask(new TaskCreateDto("BIG Epic's subTask 3", "The third Step"),
                (EpicTask) taskManager.findTaskById(3)); // 6

        // создаем и сохраняем эпик без подзадач
        taskManager.saveEpicTask(new TaskCreateDto("2nd BIG Epic task", "New steps")); //7

        // запрашиваем задачи несколько раз в разном порядке
        taskManager.getTaskById(0);
        taskManager.getTaskById(1);




       // выводим историю и убеждаемся, что в ней нет повторов
        System.out.println("Ожидался размер 2, выводится размер: " + taskManager.getHistory().size());
        System.out.println(taskManager.getHistory());

        // удаляем задачу, которая есть в истории, и проверяем, что при печати она не будет выводиться
        taskManager.deleteTaskById(0);
        System.out.println("Ожидался размер 1, выводится размер: " + taskManager.getHistory().size());
        System.out.println(taskManager.getHistory());

        // удаляем эпик с тремя подзадачами и убеждаемся, что из истории удалился как сам эпик, так и все его подзадачи
        taskManager.deleteTaskById(3);
        System.out.println("Ожидался размер 1, выводится размер: " + taskManager.getHistory().size());
        System.out.println(taskManager.getHistory());




        /*

        // создаем новую задачу
        TaskCreateDto taskCreateDtoSingle = new TaskCreateDto("Simple task", "Just do it"); // 0
        // сохраняем новую задачу
        inMemoryTaskManager.saveSingleTask(taskCreateDtoSingle);
        // получение задачи по идентификатору
        System.out.println("Новая SingleTask успешно создана: \r\n" + inMemoryTaskManager.getTaskById(0));

        // меняем статус сингл таск и выводим на печать
        SingleTask singleTask = (SingleTask) inMemoryTaskManager.getTaskById(0);
        inMemoryTaskManager.update(singleTask.withNewStatus(Status.IN_PROGRESS));
        System.out.println("Статус SingleTask услпешно обновлен: \r\n"
                + inMemoryTaskManager.getTaskById(0));

        // создаем и сохраняем эпик
        TaskCreateDto taskCreateDtoEpic = new TaskCreateDto("BIG Epic task", "Step by step"); // 1
        inMemoryTaskManager.saveEpicTask(taskCreateDtoEpic);
        // выводим на печать эпик таск с пока что пустым массивом и статусом
        System.out.println("Новая пустая EpicTask успешно создана: \r\n" + inMemoryTaskManager.getTaskById(1));

        // создаем и сохраняем две подзадачи в ArrayList Эпика id 1
        EpicTask epicTask = (EpicTask) inMemoryTaskManager.getTaskById(1);
        TaskCreateDto taskCreateDtoSub = new TaskCreateDto("BIG Epic's subTask 1", "The first Step");
        inMemoryTaskManager.saveSubTask(taskCreateDtoSub, epicTask); // 2
        TaskCreateDto taskCreateDtoSubTwo = new TaskCreateDto("BIG Epic's subTask 2", "The second Step");
        inMemoryTaskManager.saveSubTask(taskCreateDtoSubTwo, epicTask); // 3

        // выводим на печать 2 и 3 SubTask
        System.out.println("1я подзадача Эпика 1 успешно создана: \r\n" + inMemoryTaskManager.getTaskById(2));
        System.out.println("2я подзадача Эпика 1 успешно создана: \r\n" + inMemoryTaskManager.getTaskById(3));

        // меняем статус SubTask 2 НА IN_PROGRESS и печатаем
        SubTask subTask = (SubTask) inMemoryTaskManager.getTaskById(2);
        inMemoryTaskManager.update(subTask.withNewStatus(Status.IN_PROGRESS));
        System.out.println("Статус 1й подзадачи Эпика 1 услпешно обновлен: \r\n"
                + inMemoryTaskManager.getTaskById(2));
        // выводим на печать конкретный эпик таск с подзадачами с ИЗМЕНЕННЫМ СТАТУСОМ
        System.out.println("Статус Эпика 1 успешно обновлен: \r\n" + inMemoryTaskManager.getTaskById(1));

        // выводим историю просмотров
        System.out.println("История просмотра задач: \r\n" + inMemoryTaskManager.getHistory());

        // получение списка всех задач всех типов
        System.out.println("Список всех задач: " + inMemoryTaskManager.getAllTasks());

        // Получение списка задач по типам: простые, эпики
        // ??? Подзадачи нет смысла выводить отдельным общим списком отдельно от эпиков???
        System.out.println("Список всех задач типа Single: " + inMemoryTaskManager.filterTasksByType(Type.SINGLE));
        System.out.println("Список всех задач типа Epic: " + inMemoryTaskManager.filterTasksByType(Type.EPIC));

        // получение списка всех подзадач определенного эпика
        System.out.println("Список всех подзадач Эпика 1: " + inMemoryTaskManager.getEpicSubTasks(1));

        // удаление задачи по идентификатору и проверка
        inMemoryTaskManager.deleteTaskById(3);
        System.out.println("Список всех подзадач Эпика 1 после удаления подзадачи 3: "
                + inMemoryTaskManager.getEpicSubTasks(1));

        // создаем и сохраняем еще одну SingleTask
        TaskCreateDto taskCreateDtoSingleTwo = new TaskCreateDto("Simple task", "Just do it"); // 4
        // сохраняем новую задачу
        inMemoryTaskManager.saveSingleTask(taskCreateDtoSingleTwo);
        // получение задачи по идентификатору
        System.out.println("Новая SingleTask успешно создана: \r\n" + inMemoryTaskManager.getTaskById(4));

        // удаление всех задач по типам: простые, эпики
        // удаляем для примера single task
        inMemoryTaskManager.deleteTaskByType(Type.SINGLE);
        System.out.println("удаление всех задач по типу SINGLE успешно произведено: "
                + inMemoryTaskManager.filterTasksByType(Type.SINGLE).isEmpty());

        // удаление всех подзадач определенного эпика
        inMemoryTaskManager.deleteSubTasksForEpic(1);
        System.out.println("удаление всех подзадач эпика 1 успешно произведено: "
                + ((EpicTask) inMemoryTaskManager.getTaskById(1)).getSubTasks().isEmpty());

        // удаление всех задач
        inMemoryTaskManager.deleteAllTasks();
        System.out.println("удаление всех задач успешно произведено: " + inMemoryTaskManager.getAllTasks().isEmpty());

        //refactoring.guru

         */
    }
}