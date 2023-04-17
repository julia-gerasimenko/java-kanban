package org.yandex.kanban.service;

import org.yandex.kanban.exception.ManagerSaveException;
import org.yandex.kanban.model.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private static final String HEADLINE = "id,type,name,status,description,epic,startTime,durationInMins";

    public FileBackedTasksManager(HistoryManager historyManager, Path storagePath) {
        super(historyManager);
        this.storagePath = storagePath;
    }

    private final Path storagePath;

    public void save() { // будет сохранять текущее состояние менеджера в указанный файл (один файл для истории и задач)
        try (BufferedWriter writer = Files.newBufferedWriter(storagePath)) {
            writer.write(HEADLINE);
            writer.newLine();

            for (int i = 0; i < taskById.size(); i++) {
                writer.write(toString(taskById.get(i)));
            }
            writer.newLine();
            writer.write(historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка во время сохранения", e);
        }
    }

    // будет восстанавливать текущее состояние менеджера из указанного файла (один файл для истории и задач)
    public static FileBackedTasksManager loadFromFile(Path storagePath) {
        FileBackedTasksManager manager = new FileBackedTasksManager(new InMemoryHistoryManager(), storagePath);
        List<String> lines;

        try {
            lines = Files.readAllLines(storagePath);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка во время загрузки менеджера", e);
        }

        if (lines.size() <= 2) {
            return manager;
        }

        for (int i = 1; i < lines.size() && !lines.get(i).isEmpty(); i++) {
            Map.Entry<Task, Integer> taskWithEpicId = manager.fromString(lines.get(i));
            Task task = taskWithEpicId.getKey();
            switch (task.getType()) {
                case SINGLE:
                case EPIC:
                    manager.taskById.put(task.getId(), task);
                    break;
                case SUB:
                    EpicTask epicTask = (EpicTask) manager.taskById.get(taskWithEpicId.getValue());
                    List<SubTask> updatedSubTasks = epicTask.getSubTasks();
                    updatedSubTasks.add((SubTask) task);
                    epicTask.setSubTasks(updatedSubTasks);
                    epicTask.getStatus();
            }
        }
        if (!lines.get(lines.size() - 1).isEmpty()) {
            String historyOrderLine = lines.get(lines.size() - 1);

            for (Integer id : historyFromString(historyOrderLine)) {
                historyManager.addTaskToHistory(manager.findTaskById(id));
            }
        }

        return manager;
    }

    public String toString(Task task) { // метод сохранения задачи в строку
        if (Type.EPIC.equals(task.getType())) {
            EpicTask epicTask = (EpicTask) task;

            StringBuilder tasksToLine = new StringBuilder();
            String epicToLine = String.format("%d,%s,%s,%s,%s,,,", task.getId(), task.getType(),
                    task.getName(), task.getStatus(), task.getDescription());
            tasksToLine.append(epicToLine).append("\n");

            for (SubTask subTask : epicTask.getSubTasks()) {
                String subTaskToString = String.format("%d,%s,%s,%s,%s,%d,%s,%d", subTask.getId(), subTask.getType(),
                        subTask.getName(), subTask.getStatus(), subTask.getDescription(), epicTask.getId(),
                        subTask.getStartTime(), subTask.getDurationInMins());
                tasksToLine.append(subTaskToString).append("\n");
            }

            return String.valueOf(tasksToLine);
        } else {
            return String.format("%d,%s,%s,%s,%s,,%s,%d\n", task.getId(), task.getType(),
                    task.getName(), task.getStatus(), task.getDescription(),
                    task.getStartTime(), task.getDurationInMins());
        }
    }

    public Map.Entry<Task, Integer> fromString(String value) { // метод восстановления задачи из строки
        String[] list = value.split(",");
        String taskType = list[1];

        switch (taskType) {
            case "SINGLE":
                return Map.entry(new SingleTask(list[2], Integer.parseInt(list[0]), list[4], Status.valueOf(list[3]),
                                parseDateTime(list[6]), parseLong(list[7])),
                        -1);
            case "EPIC":
                return Map.entry(new EpicTask(list[2], Integer.parseInt(list[0]), list[4], null,
                        Status.valueOf(list[3])), -1);
            case "SUB":
                return Map.entry(new SubTask(list[2], Integer.parseInt(list[0]), list[4], Status.valueOf(list[3]),
                                parseDateTime(list[6]), parseLong(list[7])),
                        Integer.parseInt(list[5]));
        }
        return null;
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(value);
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Long.parseLong(value);
    }

    public static String historyToString(HistoryManager historyManager) { // метод сохранения истории в строку
        List<Integer> historyTasks = historyManager.getHistoryIds();
        Collections.reverse(historyTasks);
        StringBuilder history = new StringBuilder();

        for (int id : historyTasks) {
            history.append(id).append(",");
        }

        if (history.length() > 0) {
            history.deleteCharAt(history.length() - 1);
        }
        return history.toString();
    }

    static List<Integer> historyFromString(String value) { // метод восстановления истории из строки
        String[] historyToList = value.split(",");
        List<Integer> historyTaskIds = new LinkedList<>();

        for (String s : historyToList) {
            historyTaskIds.add(Integer.parseInt(s));
        }
        return historyTaskIds;
    }

    @Override
    public void saveSingleTask(TaskCreateDto taskCreateDto) {
        super.saveSingleTask(taskCreateDto);
        save();
    }

    @Override
    public void saveEpicTask(TaskCreateDto taskCreateDto) {
        super.saveEpicTask(taskCreateDto);
        save();
    }

    @Override
    public void saveSubTask(TaskCreateDto taskCreateDto, EpicTask epicTask) {
        super.saveSubTask(taskCreateDto, epicTask);
        save();
    }

    @Override
    public void update(Task task) {
        super.update(task);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = findTaskById(id);
        if (task != null) {
            historyManager.addTaskToHistory(task);
            save();
        }
        return task;
    }

    @Override
    public void removeTaskFromHistory(Task task) {
        super.removeTaskFromHistory(task);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteTaskByType(Type type) {
        super.deleteTaskByType(type);
        save();
    }

    @Override
    public void deleteSubTasksForEpic(int epicId) {
        super.deleteSubTasksForEpic(epicId);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }
}



