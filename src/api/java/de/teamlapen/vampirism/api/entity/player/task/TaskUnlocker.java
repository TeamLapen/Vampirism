package de.teamlapen.vampirism.api.entity.player.task;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;

@FunctionalInterface
public interface TaskUnlocker {

    boolean isUnlocked(IFactionPlayer<?> playerEntity);
}
