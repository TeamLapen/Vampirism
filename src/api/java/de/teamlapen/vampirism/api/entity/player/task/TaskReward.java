package de.teamlapen.vampirism.api.entity.player.task;

import net.minecraft.entity.player.PlayerEntity;

@FunctionalInterface
public interface TaskReward {

    void applyReward(PlayerEntity player);
}
