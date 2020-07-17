package de.teamlapen.vampirism.api.entity.player.task;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.util.text.ITextComponent;

public interface TaskUnlocker {

    boolean isUnlocked(IFactionPlayer<?> playerEntity);

    ITextComponent getDescription();
}
