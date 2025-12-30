package de.teamlapen.faction.common.factions.tasks.unlock;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.factions.tasks.ITaskPlayer;
import de.teamlapen.faction.api.factions.tasks.TaskUnlocker;
import de.teamlapen.faction.common.core.FactionTasks;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

public record LvlUnlocker(int reqLevel, int maxLevel) implements TaskUnlocker {

    public static final MapCodec<LvlUnlocker> CODEC = RecordCodecBuilder.mapCodec(inst -> {
        return inst.group(
                Codec.INT.fieldOf("reqLevel").forGetter(i -> i.reqLevel),
                Codec.INT.optionalFieldOf("maxLevel", -1).forGetter(i -> i.maxLevel)
        ).apply(inst, LvlUnlocker::new);
    });

    public LvlUnlocker(int reqLevel) {
        this(reqLevel, -1);
    }

    /**
     * @param maxLevel > 0 if there should be one
     */
    public LvlUnlocker {
    }

    @Override
    public Component getDescription() {
        MutableComponent t = Component.translatable("text.factionapi.level_min", reqLevel);
        if (maxLevel > 0) {
            t.append(" ").append(Component.translatable("text.factionapi.level_max", maxLevel));
        }
        return t;
    }

    @Override
    public <T extends ITaskPlayer<T>> boolean isUnlocked(T playerEntity) {
        return playerEntity.getLevel() >= reqLevel && (maxLevel <= 0 || playerEntity.getLevel() <= maxLevel);
    }

    @Override
    public MapCodec<? extends TaskUnlocker> codec() {
        return FactionTasks.LEVEL_UNLOCKER.get();
    }
}
