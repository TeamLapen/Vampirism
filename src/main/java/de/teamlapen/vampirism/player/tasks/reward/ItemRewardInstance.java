package de.teamlapen.vampirism.player.tasks.reward;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.ITaskRewardInstance;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class ItemRewardInstance implements ITaskRewardInstance {
    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "item");


    @Nonnull
    protected final ItemStack reward;

    public ItemRewardInstance(@Nonnull ItemStack reward) {
        this.reward = reward;
    }

    @Override
    public void applyReward(IFactionPlayer<?> player) {
        if (!player.getRepresentingPlayer().addItemStackToInventory(this.reward.copy())) {
            player.getRepresentingPlayer().dropItem(this.reward.copy(), true);
        }
    }

    @Nonnull
    public ItemStack getReward() {
        return reward.copy();
    }

    @Override
    public CompoundNBT writeNBT(@Nonnull CompoundNBT nbt) {
        nbt.put("reward", this.reward.write(new CompoundNBT()));
        return nbt;
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeItemStack(this.reward);
    }

    public static ItemRewardInstance decode(PacketBuffer buffer) {
        return new ItemRewardInstance(buffer.readItemStack());
    }

    public static ItemRewardInstance readNbt(CompoundNBT nbt) {
        return new ItemRewardInstance(ItemStack.read(nbt.getCompound("reward")));
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }
}
