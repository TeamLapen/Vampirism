package de.teamlapen.vampirism.player.tasks.unlock;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.TaskUnlocker;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.network.chat.Component;

public class LordLvlUnlocker implements TaskUnlocker {

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
    public Component getDescription() {
        return Component.translatable("text.vampirism.lord").append(Component.literal(" ")).append(Component.translatable("text.vampirism.level")).append(Component.literal((exact ? " = " : " ") + reqLordLevel));
    }

    @Override
    public boolean isUnlocked(IFactionPlayer<?> playerEntity) {
        int aL = FactionPlayerHandler.getOpt(playerEntity.getRepresentingPlayer()).map(FactionPlayerHandler::getLordLevel).orElse(0);
        return exact ? aL == reqLordLevel : aL >= reqLordLevel;
    }
}
