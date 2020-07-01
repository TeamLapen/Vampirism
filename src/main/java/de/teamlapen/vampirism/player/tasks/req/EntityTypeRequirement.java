package de.teamlapen.vampirism.player.tasks.req;

import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.Tag;

import javax.annotation.Nonnull;

public class EntityTypeRequirement implements TaskRequirement<Tag<EntityType<?>>> {
    private final @Nonnull Tag<EntityType<?>> entityType;
    private final int amount;

    public EntityTypeRequirement(@Nonnull Tag<EntityType<?>> entityType, int amount) {
        this.entityType = entityType;
        this.amount = amount;
    }

    @Nonnull
    @Override
    public Type getType() {
        return Type.ENTITY_TYPE;
    }

    @Nonnull
    @Override
    public Tag<EntityType<?>> getStat() {
        return entityType;
    }

    @Override
    public int getAmount() {
        return amount;
    }
}
