package de.teamlapen.vampirism.player.tasks.req;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class StatRequirement implements TaskRequirement<ResourceLocation> {

    private final @Nonnull ResourceLocation stat;
    private final int amount;

    public StatRequirement(@Nonnull ResourceLocation stat, int amount) {
        this.stat = stat;
        this.amount = amount;
    }

    @Nonnull
    @Override
    public Type getType() {
        return Type.STATS;
    }

    @Nonnull
    @Override
    public ResourceLocation getStat(IFactionPlayer<?> player) {
        return stat;
    }

    @Override
    public int getAmount(IFactionPlayer<?> player) {
        return amount;
    }
}