package de.teamlapen.vampirism.proxy;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;

/**
 * Serverside proxy
 */
public class ServerProxy extends CommonProxy {


    @Override
    public boolean isClientPlayerNull() {
        return false;
    }

    @Override
    public boolean isPlayerThePlayer(EntityPlayer player) {
        return false;
    }


    @Override
    public void renderScreenFullColor(int ticksOn, int ticksOff, int color) {

    }

    @Nullable
    @Override
    public Entity getMouseOverEntity() {
        return null;
    }
}
