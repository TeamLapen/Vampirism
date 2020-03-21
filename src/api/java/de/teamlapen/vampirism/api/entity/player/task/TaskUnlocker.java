package de.teamlapen.vampirism.api.entity.player.task;

import net.minecraft.entity.player.PlayerEntity;

@FunctionalInterface
public interface TaskUnlocker {

    boolean isUnlocked(PlayerEntity playerEntity);
}
