package de.teamlapen.vampirism.player.tasks.reward;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.ITaskRewardInstance;
import de.teamlapen.vampirism.api.entity.player.task.TaskReward;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;


/**
 * Reward to level up (1 level) as lord
 */
public class LordLevelReward implements TaskReward, ITaskRewardInstance {
    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "lord_level_reward");

    public final int targetLevel;

    public LordLevelReward(int targetLevel) {
        this.targetLevel = targetLevel;
    }


    @Override
    public void applyReward(IFactionPlayer<?> p) {
        FactionPlayerHandler.getOpt(p.getRepresentingPlayer()).ifPresent(fph -> {
            if (fph.getLordLevel() == targetLevel - 1) {
                fph.setLordLevel(targetLevel);
            }
        });
    }

    @Override
    public CompoundNBT writeNBT(@Nonnull CompoundNBT nbt) {
        nbt.putInt("targetLevel", this.targetLevel);
        return nbt;
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeVarInt(this.targetLevel);
    }

    @Override
    public ITaskRewardInstance createInstance(IFactionPlayer<?> player) {
        return this;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public static LordLevelReward decode(PacketBuffer buffer) {
        return new LordLevelReward(buffer.readVarInt());
    }

    public static LordLevelReward readNbt(CompoundNBT nbt) {
        return new LordLevelReward(nbt.getInt("targetLevel"));
    }
}
