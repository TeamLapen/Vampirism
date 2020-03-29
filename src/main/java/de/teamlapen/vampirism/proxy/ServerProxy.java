package de.teamlapen.vampirism.proxy;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;

/**
 * Serverside proxy
 */
public class ServerProxy extends CommonProxy {


    @Nullable
    @Override
    public PlayerEntity getClientPlayer() {
        return null;
    }

    @Nullable
    @Override
    public Entity getMouseOverEntity() {
        return null;
    }

    @Override
    public void renderScreenFullColor(int ticksOn, int ticksOff, int color) {

    }

    @Override
    public void handleSleepClient(PlayerEntity player) {

    }

}
