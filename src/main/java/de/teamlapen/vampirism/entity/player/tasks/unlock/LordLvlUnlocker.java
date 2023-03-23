package de.teamlapen.vampirism.entity.player.tasks.unlock;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.TaskUnlocker;
import de.teamlapen.vampirism.core.ModTasks;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class LordLvlUnlocker implements TaskUnlocker {

    public static final Codec<LordLvlUnlocker> CODEC = RecordCodecBuilder.create(inst -> {
        return inst.group(
                Codec.INT.fieldOf("reqLordLevel").forGetter(i -> i.reqLordLevel),
                Codec.BOOL.fieldOf("exact").forGetter(i -> i.exact)
        ).apply(inst, LordLvlUnlocker::new);
    });

    private final int reqLordLevel;
    private final boolean exact;

    public LordLvlUnlocker(int reqLordLevel, boolean exact) {
        this.reqLordLevel = reqLordLevel;
        this.exact = exact;
    }

    public LordLvlUnlocker(int reqLordLevel) {
        this(reqLordLevel, false);
    }

    @Override
    public @NotNull Component getDescription() {
        return Component.translatable("text.vampirism.lord").append(Component.literal(" ")).append(Component.translatable("text.vampirism.level")).append(Component.literal((exact ? " = " : " ") + reqLordLevel));
    }

    @Override
    public boolean isUnlocked(@NotNull IFactionPlayer<?> playerEntity) {
        int aL = FactionPlayerHandler.getOpt(playerEntity.getRepresentingPlayer()).map(FactionPlayerHandler::getLordLevel).orElse(0);
        return exact ? aL == reqLordLevel : aL >= reqLordLevel;
    }

    @Override
    public Codec<? extends TaskUnlocker> codec() {
        return ModTasks.LORD_LEVEL_UNLOCKER.get();
    }
}
