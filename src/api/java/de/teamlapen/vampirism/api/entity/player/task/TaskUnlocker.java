package de.teamlapen.vampirism.api.entity.player.task;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.util.text.ITextComponent;

public interface TaskUnlocker {

    ITextComponent getDescription();

    boolean isUnlocked(IFactionPlayer<?> playerEntity);
}
