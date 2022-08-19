package de.teamlapen.vampirism.entity.player.tasks.req;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class EntityRequirement implements TaskRequirement.Requirement<EntityType<?>> {
    @NotNull
    private final EntityType<?> entityType;
    private final int amount;
    @NotNull
    private final ResourceLocation id;

    public EntityRequirement(@NotNull ResourceLocation id, @NotNull EntityType<?> entityType, int amount) {
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
    public EntityType<?> getStat(IFactionPlayer<?> player) {
        return entityType;
    }

    @NotNull
    @Override
    public TaskRequirement.Type getType() {
        return TaskRequirement.Type.ENTITY;
    }

}
