package org.yandex.kanban.model;

import java.time.LocalDateTime;

public abstract class Task  implements Overlappable {
    private final String name;
    private final int id;
    private final String description;

    public Task (String name, int id, String description) {
        this.name = name;
        this.id = id;
        this.description = description;
    }
    public String getName() {
        return name;
    }

    public String getDescription() { return description; }

    public int getId() {
        return id;
    }

    public abstract Status getStatus();

    public abstract Type getType();

    public abstract String toString ();

    public abstract LocalDateTime getStartTime();
    public abstract LocalDateTime getEndTime();
    public abstract Long getDurationInMins();

}
