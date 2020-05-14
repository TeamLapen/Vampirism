package de.teamlapen.vampirism.proxy;

import de.teamlapen.vampirism.network.AppearancePacket;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
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
    public void handleAppearancePacket(PlayerEntity player, AppearancePacket msg) {
        VampirePlayer.getOpt(player).ifPresent(vampire -> {
            vampire.setFangType(msg.fangType);
            vampire.setEyeType(msg.eyeType);
        });
    }
}
