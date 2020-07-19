package de.teamlapen.vampirism.player.tasks.unlock;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.TaskUnlocker;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class LvlUnlocker implements TaskUnlocker {

    private final int reqLevel;

    public LvlUnlocker(int reqLevel) {
        this.reqLevel = reqLevel;
    }

    @Override
    public boolean isUnlocked(IFactionPlayer<?> playerEntity) {
        return playerEntity.getLevel() >= reqLevel;
    }

    @Override
    public ITextComponent getDescription() {
        return new TranslationTextComponent("text.vampirism.level").appendSibling(new StringTextComponent(" " + reqLevel));
    }
}
