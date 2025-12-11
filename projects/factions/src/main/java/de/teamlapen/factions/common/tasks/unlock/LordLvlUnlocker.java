package de.teamlapen.factions.common.tasks.unlock;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.factions.api.tasks.ITaskPlayer;
import de.teamlapen.factions.api.tasks.TaskUnlocker;
import de.teamlapen.factions.common.core.FactionTasks;
import de.teamlapen.factions.common.factions.FactionPlayerHandler;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public record LordLvlUnlocker(int reqLordLevel, boolean exact) implements TaskUnlocker {

    public static final MapCodec<LordLvlUnlocker> CODEC = RecordCodecBuilder.mapCodec(inst -> {
        return inst.group(
                Codec.INT.fieldOf("reqLordLevel").forGetter(i -> i.reqLordLevel),
                Codec.BOOL.fieldOf("exact").forGetter(i -> i.exact)
        ).apply(inst, LordLvlUnlocker::new);
    });

    public LordLvlUnlocker(int reqLordLevel) {
        this(reqLordLevel, false);
    }

    @Override
    public Component getDescription() {
        return Component.translatable("text.factions.lord").append(Component.literal(" ")).append(Component.translatable("text.factions.level")).append(Component.literal((exact ? " = " : " ") + reqLordLevel));
    }

    @Override
    public <T extends ITaskPlayer<T>> boolean isUnlocked(@NotNull T playerEntity) {
        int aL = FactionPlayerHandler.get(playerEntity.asEntity()).getLordLevel();
        return exact ? aL == reqLordLevel : aL >= reqLordLevel;
    }

    @Override
    public MapCodec<? extends TaskUnlocker> codec() {
        return FactionTasks.LORD_LEVEL_UNLOCKER.get();
    }
}
