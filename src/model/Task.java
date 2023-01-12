package model;

public abstract class Task {
    private String name;
    private int id;
    private String description;

    public Task (String name, int id, String description) {
        this.name = name;
        this.id = id;
        this.description = description;
    }
    // повторить про геттеры и сеттеры
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

}
