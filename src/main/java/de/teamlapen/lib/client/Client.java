package de.teamlapen.lib.client;

import de.teamlapen.lib.client.sound.ClientSoundHandler;
import de.teamlapen.lib.common.sounds.ISoundHandler;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

public class Client {

    public static ISoundHandler createSoundHandler() {
        return new ClientSoundHandler();
    }

    public static @NotNull String getActiveLanguage() {
        return Minecraft.getInstance().getLanguageManager().getSelected();
    }

    public static String getActiveLanguageCode() {
        return Minecraft.getInstance().getLanguageManager().getSelected();
    }
}
