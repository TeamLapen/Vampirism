package de.teamlapen.vampirism.api.entity.player.task;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public interface ITaskRewardInstance {
    /**
     * applies the reward to the player upon task completion
     *
     * @param player the player which completed the task
     */
    void applyReward(IFactionPlayer<?> player);

    CompoundNBT writeNBT(@Nonnull CompoundNBT nbt);

    void encode(PacketBuffer buffer);

    ResourceLocation getId();

}
