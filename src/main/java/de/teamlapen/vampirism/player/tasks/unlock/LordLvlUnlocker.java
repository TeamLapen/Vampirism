package de.teamlapen.vampirism.player.tasks.unlock;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.TaskUnlocker;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

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
        return new TranslatableComponent("text.vampirism.lord").append(new TextComponent(" ")).append(new TranslatableComponent("text.vampirism.level")).append(new TextComponent((exact ? " = " : " ") + reqLordLevel));
    }

    @Override
    public boolean isUnlocked(IFactionPlayer<?> playerEntity) {
        int aL = FactionPlayerHandler.getOpt(playerEntity.getRepresentingPlayer()).map(FactionPlayerHandler::getLordLevel).orElse(0);
        return exact ? aL == reqLordLevel : aL >= reqLordLevel;
    }
}
