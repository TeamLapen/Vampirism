package de.teamlapen.faction.common.factions.tasks.unlock;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.api.factions.tasks.ITaskManager;
import de.teamlapen.faction.api.factions.tasks.TaskUnlocker;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
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
        return Component.translatable(exact ? "message.factionapi.task_unlocker.lord_level.exact" : "message.factionapi.task_unlocker.lord_level.min", reqLordLevel);
    }

    @Override
    public boolean isUnlocked(ITaskManager taskManager, IFactionPlayer<?> factionPlayer) {
        int aL = FactionPlayerHandler.get(factionPlayer.asEntity()).getPlayerLord().map(ILordPlayer::getLordLevel).orElse(0);
        return exact ? aL == reqLordLevel : aL >= reqLordLevel;
    }

    @Override
    public MapCodec<? extends TaskUnlocker> codec() {
        return FactionTasks.LORD_LEVEL_UNLOCKER.get();
    }
}
