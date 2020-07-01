package de.teamlapen.vampirism.api.entity.player.task;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.entity.player.PlayerEntity;

@FunctionalInterface
public interface TaskUnlocker {

    boolean isUnlocked(IFactionPlayer<?> playerEntity);
}
