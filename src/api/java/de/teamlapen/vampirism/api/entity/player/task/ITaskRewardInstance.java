package de.teamlapen.vampirism.api.entity.player.task;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public interface ITaskRewardInstance {
    /**
     * applies the reward to the player upon task completion
     *
     * @param player the player which completed the task
     */
    void applyReward(IFactionPlayer<?> player);

    void encode(FriendlyByteBuf buffer);

    ResourceLocation getId();

    CompoundTag writeNBT(@NotNull CompoundTag nbt);

}
