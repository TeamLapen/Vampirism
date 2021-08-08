package de.teamlapen.vampirism.proxy;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

/**
 * Serverside proxy
 */
public class ServerProxy extends CommonProxy {


    @Nullable
    @Override
    public Player getClientPlayer() {
        return null;
    }

    @Nullable
    @Override
    public Entity getMouseOverEntity() {
        return null;
    }

    @Override
    public void handleSleepClient(Player player) {

    }

    @Override
    public void renderScreenFullColor(int ticksOn, int ticksOff, int color) {

    }


}
