package de.teamlapen.vampirism.proxy;

import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.network.TaskFinishedPacket;
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

    @Override
    public void handleTaskFinishedPacket(TaskFinishedPacket msg, PlayerEntity playerEntity) {
        FactionPlayerHandler.getOpt(playerEntity).ifPresent(factionPlayerHandler -> factionPlayerHandler.getCurrentFactionPlayer().ifPresent(sd -> sd.getTaskManager().addCompletedTask(msg.task)));
    }
}
