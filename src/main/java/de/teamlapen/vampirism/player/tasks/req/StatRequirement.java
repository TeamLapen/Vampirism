package de.teamlapen.vampirism.player.tasks.req;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class StatRequirement implements TaskRequirement.Requirement<ResourceLocation> {

    @NotNull
    private final ResourceLocation stat;
    private final int amount;
    @NotNull
    private final ResourceLocation id;

    public StatRequirement(@NotNull ResourceLocation id, @NotNull ResourceLocation stat, int amount) {
        this.id = id;
        this.stat = stat;
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
    public ResourceLocation getStat(IFactionPlayer<?> player) {
        return stat;
    }

    @NotNull
    @Override
    public TaskRequirement.Type getType() {
        return TaskRequirement.Type.STATS;
    }

}