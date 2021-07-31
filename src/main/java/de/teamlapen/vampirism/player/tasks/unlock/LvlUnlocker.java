package de.teamlapen.vampirism.player.tasks.unlock;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.TaskUnlocker;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class LvlUnlocker implements TaskUnlocker {

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
    public ITextComponent getDescription() {
        TextComponent t = new TranslationTextComponent("text.vampirism.level_min", reqLevel);
        if (maxLevel > 0) {
            t.append(" ").append(new TranslationTextComponent("text.vampirism.level_max", maxLevel));
        }
        return t;
    }

    @Override
    public boolean isUnlocked(IFactionPlayer<?> playerEntity) {
        return playerEntity.getLevel() >= reqLevel && (maxLevel <= 0 || playerEntity.getLevel() <= maxLevel);
    }
}
