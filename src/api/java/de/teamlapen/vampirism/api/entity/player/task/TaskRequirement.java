package de.teamlapen.vampirism.api.entity.player.task;

import javax.annotation.Nonnull;

public interface TaskRequirement<T> {

    @Nonnull
    Type getType();

    @Nonnull
    T getStat();

    int getAmount();

    enum Type {
        STATS, ITEMS, ENTITY, BOOLEAN
    }

}
