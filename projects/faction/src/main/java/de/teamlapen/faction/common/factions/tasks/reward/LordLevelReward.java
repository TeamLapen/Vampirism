package de.teamlapen.faction.common.factions.tasks.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.factions.LevelingChange;
import de.teamlapen.faction.api.factions.tasks.ITaskRewardInstance;
import de.teamlapen.faction.api.factions.tasks.TaskReward;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.faction.common.core.FactionTasks;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;


/**
 * Reward to level up (1 level) as lord
 */
public class LordLevelReward implements TaskReward, ITaskRewardInstance {

    public static final MapCodec<LordLevelReward> CODEC = RecordCodecBuilder.mapCodec(inst ->
            inst.group(
                    Codec.INT.fieldOf("targetLevel").forGetter(i -> i.targetLevel),
                    ComponentSerialization.CODEC.fieldOf("description").forGetter(i -> i.description)
            ).apply(inst, LordLevelReward::new));

    public final int targetLevel;
    private final Component description;

    public LordLevelReward(int targetLevel, Component description) {
        this.targetLevel = targetLevel;
        this.description = description;
    }

    public LordLevelReward(int targetLevel) {
        this.targetLevel = targetLevel;
        this.description = Component.translatable("task_reward.factionapi.lord_level_reward", targetLevel);
    }

    @Override
    public void applyReward(IFactionPlayer<?> p) {
        FactionPlayerHandler fph = FactionPlayerHandler.get(p.asEntity());
        if (fph.getLordLevel() == targetLevel - 1) {
            fph.setFaction(LevelingChange.builder().lordLevel(targetLevel).build());
        }
    }

    @Override
    public ITaskRewardInstance createInstance(IFactionPlayer<?> player) {
        return this;
    }

    @Override
    public MapCodec<LordLevelReward> codec() {
        return FactionTasks.LORD_LEVEL_REWARD.get();
    }

    @Override
    public Component description() {
        return this.description;
    }
}
