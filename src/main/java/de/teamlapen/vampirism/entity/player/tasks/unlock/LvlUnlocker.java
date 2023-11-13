package de.teamlapen.vampirism.entity.player.tasks.unlock;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.TaskUnlocker;
import de.teamlapen.vampirism.core.ModTasks;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

public class LvlUnlocker implements TaskUnlocker {

    public static final Codec<LvlUnlocker> CODEC = RecordCodecBuilder.create(inst -> {
        return inst.group(
                Codec.INT.fieldOf("reqLevel").forGetter(i -> i.reqLevel),
                Codec.INT.optionalFieldOf("maxLevel", -1).forGetter(i -> i.maxLevel)
        ).apply(inst, LvlUnlocker::new);
    });

    private final int reqLevel;
    private final int maxLevel;

    public LvlUnlocker(int reqLevel) {
        this(reqLevel, -1);
    }

    /**
     * @param maxLevel > 0 if there should be one
     */
    public LvlUnlocker(int reqLevel, int maxLevel) {
        this.reqLevel = reqLevel;
        this.maxLevel = maxLevel;
    }

    @Override
    public @NotNull Component getDescription() {
        MutableComponent t = Component.translatable("text.vampirism.level_min", reqLevel);
        if (maxLevel > 0) {
            t.append(" ").append(Component.translatable("text.vampirism.level_max", maxLevel));
        }
        return t;
    }

    @Override
    public boolean isUnlocked(@NotNull IFactionPlayer<?> playerEntity) {
        return playerEntity.getLevel() >= reqLevel && (maxLevel <= 0 || playerEntity.getLevel() <= maxLevel);
    }

    @Override
    public Codec<? extends TaskUnlocker> codec() {
        return ModTasks.LEVEL_UNLOCKER.get();
    }
}
