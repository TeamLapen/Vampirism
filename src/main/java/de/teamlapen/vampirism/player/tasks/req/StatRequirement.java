package de.teamlapen.vampirism.player.tasks.req;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class StatRequirement implements TaskRequirement.Requirement<ResourceLocation> {

    @Nonnull
    private final ResourceLocation stat;
    private final int amount;
    @Nonnull
    private final ResourceLocation id;

    public StatRequirement(@Nonnull ResourceLocation id, @Nonnull ResourceLocation stat, int amount) {
        this.id = id;
        this.stat = stat;
        this.amount = amount;
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

    @Nonnull
    @Override
    public ResourceLocation getStat(IFactionPlayer<?> player) {
        return stat;
    }

    @Nonnull
    @Override
    public TaskRequirement.Type getType() {
        return TaskRequirement.Type.STATS;
    }

}