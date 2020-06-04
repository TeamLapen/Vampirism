package de.teamlapen.vampirism.player.tasks.req;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * the entity tag needs a translation key with format {@code tasks.vampirism.<tagid>}
 */
public class EntityTypeRequirement implements TaskRequirement.Requirement<Tag<EntityType<?>>> {
    @Nonnull
    private final Tag<EntityType<?>> entityType;
    private final int amount;
    @Nonnull
    private final ResourceLocation id;

    public EntityTypeRequirement(@Nonnull ResourceLocation id, @Nonnull Tag<EntityType<?>> entityType, int amount) {
        this.id = id;
        this.entityType = entityType;
        this.amount = amount;
    }

    @Nonnull
    @Override
    public TaskRequirement.Type getType() {
        return TaskRequirement.Type.ENTITY_TAG;
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

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return id;
    }

}
