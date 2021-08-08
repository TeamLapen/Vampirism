package de.teamlapen.vampirism.api.entity.player.task;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.network.chat.Component;

public interface TaskUnlocker {

    Component getDescription();

    boolean isUnlocked(IFactionPlayer<?> playerEntity);
}
