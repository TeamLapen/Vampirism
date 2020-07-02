package de.teamlapen.vampirism.player.tasks.req;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.Tag;

import javax.annotation.Nonnull;

/**
 * the entity tag needs a translation key with format {@code tasks.vampirism.<tagid>}
 */
public class EntityTypeRequirement implements TaskRequirement<Tag<EntityType<?>>> {
    @Nonnull
    private final Tag<EntityType<?>> entityType;
    private final int amount;

    public EntityTypeRequirement(@Nonnull Tag<EntityType<?>> entityType, int amount) {
        this.entityType = entityType;
        this.amount = amount;
    }

    @Nonnull
    @Override
    public Type getType() {
        return Type.ENTITY_TAG;
    }

    @Nonnull
    @Override
    public Tag<EntityType<?>> getStat(IFactionPlayer<?> player) {
        return entityType;
    }

    @Override
    public int getAmount(IFactionPlayer<?> player) {
        return amount;
    }
}
