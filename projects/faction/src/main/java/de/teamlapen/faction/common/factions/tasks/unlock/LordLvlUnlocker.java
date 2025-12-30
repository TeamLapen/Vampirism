package de.teamlapen.faction.common.factions.tasks.unlock;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.factions.tasks.ITaskPlayer;
import de.teamlapen.faction.api.factions.tasks.TaskUnlocker;
import de.teamlapen.faction.common.core.FactionTasks;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import net.minecraft.network.chat.Component;

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
        return Component.translatable("text.factionapi.lord").append(Component.literal(" ")).append(Component.translatable("text.factionapi.level")).append(Component.literal((exact ? " = " : " ") + reqLordLevel));
    }

    @Override
    public <T extends ITaskPlayer<T>> boolean isUnlocked(T playerEntity) {
        int aL = FactionPlayerHandler.get(playerEntity.asEntity()).getLordLevel();
        return exact ? aL == reqLordLevel : aL >= reqLordLevel;
    }

    @Override
    public MapCodec<? extends TaskUnlocker> codec() {
        return FactionTasks.LORD_LEVEL_UNLOCKER.get();
    }
}
