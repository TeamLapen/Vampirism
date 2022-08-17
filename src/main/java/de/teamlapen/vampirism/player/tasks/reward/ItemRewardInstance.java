package de.teamlapen.vampirism.player.tasks.reward;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.ITaskRewardInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("ClassCanBeRecord")
public class ItemRewardInstance implements ITaskRewardInstance {
    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "item");

    public static @NotNull ItemRewardInstance decode(@NotNull FriendlyByteBuf buffer) {
        return new ItemRewardInstance(buffer.readItem());
    }

    public static @NotNull ItemRewardInstance readNbt(@NotNull CompoundTag nbt) {
        return new ItemRewardInstance(ItemStack.of(nbt.getCompound("reward")));
    }

    @NotNull
    protected final ItemStack reward;

    public ItemRewardInstance(@NotNull ItemStack reward) {
        this.reward = reward;
    }

    @Override
    public void applyReward(@NotNull IFactionPlayer<?> player) {
        if (!player.getRepresentingPlayer().addItem(this.reward.copy())) {
            player.getRepresentingPlayer().drop(this.reward.copy(), true);
        }
    }

    @Override
    public void encode(@NotNull FriendlyByteBuf buffer) {
        buffer.writeItem(this.reward);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @NotNull
    public ItemStack getReward() {
        return reward.copy();
    }

    @Override
    public @NotNull CompoundTag writeNBT(@NotNull CompoundTag nbt) {
        nbt.put("reward", this.reward.save(new CompoundTag()));
        return nbt;
    }
}
