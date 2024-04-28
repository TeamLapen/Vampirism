package de.teamlapen.vampirism.entity.player.tasks.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.ITaskRewardInstance;
import de.teamlapen.vampirism.api.entity.player.task.TaskReward;
import de.teamlapen.vampirism.core.ModTasks;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import org.jetbrains.annotations.NotNull;


/**
 * Reward to level up (1 level) as lord
 */
public class LordLevelReward implements TaskReward, ITaskRewardInstance {

    public static final MapCodec<LordLevelReward> CODEC = RecordCodecBuilder.mapCodec(inst ->
            inst.group(
                    Codec.INT.fieldOf("targetLevel").forGetter(i -> i.targetLevel),
                    ComponentSerialization.FLAT_CODEC.fieldOf("description").forGetter(i -> i.description)
            ).apply(inst, LordLevelReward::new));

    public final int targetLevel;
    private final Component description;

    public LordLevelReward(int targetLevel, Component description) {
        this.targetLevel = targetLevel;
        this.description = description;
    }

    public LordLevelReward(int targetLevel) {
        this.targetLevel = targetLevel;
        this.description = Component.translatable("task_reward.vampirism.lord_level_reward", targetLevel);
    }

    @Override
    public void applyReward(@NotNull IFactionPlayer<?> p) {
        FactionPlayerHandler fph = FactionPlayerHandler.get(p.asEntity());
        if (fph.getLordLevel() == targetLevel - 1) {
            fph.setLordLevel(targetLevel);
        }
    }

    @Override
    public @NotNull ITaskRewardInstance createInstance(IFactionPlayer<?> player) {
        return this;
    }

    @Override
    public MapCodec<LordLevelReward> codec() {
        return ModTasks.LORD_LEVEL_REWARD.get();
    }

    @Override
    public Component description() {
        return this.description;
    }
}
