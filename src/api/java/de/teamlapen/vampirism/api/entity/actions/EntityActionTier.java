package de.teamlapen.vampirism.api.entity.actions;

public enum EntityActionTier {
    Default(0),
    Low(1),
    Medium(2),
    High(3),
    Ultimate(4);

    private final int id;

    EntityActionTier(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
