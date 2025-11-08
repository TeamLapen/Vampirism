package de.teamlapen.lib.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;

public interface IMinecraftAccessor {

    default Minecraft mc() {
        return Minecraft.getInstance();
    }

    default AbstractClientPlayer player() {
        return mc().player;
    }

    default ClientLevel level() {
        return mc().level;
    }
}
