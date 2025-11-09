package de.teamlapen.lib.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;

public interface IMinecraftAccessor {

    default Minecraft mc() {
        return Minecraft.getInstance();
    }

    default LocalPlayer player() {
        return mc().player;
    }

    default ClientLevel level() {
        return mc().level;
    }

    default Font font() { return mc().font; }
}
