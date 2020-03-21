package de.teamlapen.vampirism.player.tasks.req;

import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import net.minecraft.entity.EntityType;

import javax.annotation.Nonnull;

public class EntityRequirement implements TaskRequirement<EntityType<?>> {
    private final @Nonnull EntityType<?> entityType;
    private final int amount;

    public EntityRequirement(@Nonnull EntityType<?> entityType, int amount) {
        this.entityType = entityType;
        this.amount = amount;
    }

    @Nonnull
    @Override
    public Type getType() {
        return Type.ENTITY;
    }

    @Nonnull
    @Override
    public EntityType<?> getStat() {
        return entityType;
    }

    @Override
    public int getAmount() {
        return amount;
    }
}
