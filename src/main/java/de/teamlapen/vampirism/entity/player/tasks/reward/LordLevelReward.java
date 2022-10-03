package de.teamlapen.vampirism.entity.player.tasks.reward;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.ITaskRewardInstance;
import de.teamlapen.vampirism.api.entity.player.task.TaskReward;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;


/**
 * Reward to level up (1 level) as lord
 */
@SuppressWarnings("ClassCanBeRecord")
public class LordLevelReward implements TaskReward, ITaskRewardInstance {
    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "lord_level_reward");

    public static @NotNull LordLevelReward decode(@NotNull FriendlyByteBuf buffer) {
        return new LordLevelReward(buffer.readVarInt());
    }

    public static @NotNull LordLevelReward readNbt(@NotNull CompoundTag nbt) {
        return new LordLevelReward(nbt.getInt("targetLevel"));
    }

    public final int targetLevel;

    public LordLevelReward(int targetLevel) {
        this.targetLevel = targetLevel;
    }

    @Override
    public void applyReward(@NotNull IFactionPlayer<?> p) {
        FactionPlayerHandler.getOpt(p.getRepresentingPlayer()).ifPresent(fph -> {
            if (fph.getLordLevel() == targetLevel - 1) {
                fph.setLordLevel(targetLevel);
            }
        });
    }

    @Override
    public @NotNull ITaskRewardInstance createInstance(IFactionPlayer<?> player) {
        return this;
    }

    @Override
    public void encode(@NotNull FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.targetLevel);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public @NotNull CompoundTag writeNBT(@NotNull CompoundTag nbt) {
        nbt.putInt("targetLevel", this.targetLevel);
        return nbt;
    }
}
