package de.teamlapen.vampirism.api.entity.player.task;

public class TaskRequirement {

    private final Type type;

    public TaskRequirement(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        STATS, ITEMS
    }

}
