package de.teamlapen.vampirism.player.tasks.req;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

/**
 * the entity tag needs a translation key with format {@code tasks.vampirism.<tagid>}
 */
public class EntityTypeRequirement implements TaskRequirement.Requirement<TagKey<EntityType<?>>> {
    @NotNull
    private final TagKey<EntityType<?>> entityType;
    private final int amount;
    @NotNull
    private final ResourceLocation id;

    public EntityTypeRequirement(@NotNull ResourceLocation id, @NotNull TagKey<EntityType<?>> entityType, int amount) {
        this.id = id;
        this.entityType = entityType;
        this.amount = amount;
    }

    @Override
    public int getAmount(IFactionPlayer<?> player) {
        return amount;
    }

    @NotNull
    @Override
    public ResourceLocation getId() {
        return id;
    }

    @NotNull
    @Override
    public TagKey<EntityType<?>> getStat(IFactionPlayer<?> player) {
        return entityType;
    }

    @NotNull
    @Override
    public TaskRequirement.Type getType() {
        return TaskRequirement.Type.ENTITY_TAG;
    }

}
