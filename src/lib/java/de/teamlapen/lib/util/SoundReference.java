package de.teamlapen.lib.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;

/**
 * Reference to a ISound
 *
 * @author maxanier
 */
public class SoundReference implements ISoundReference {

    private final SoundInstance sound;

    public SoundReference(SoundInstance sound) {
        this.sound = sound;
    }

    @Override
    public boolean isPlaying() {
        return Minecraft.getInstance().getSoundManager().isActive(sound);
    }

    @Override
    public void startPlaying() {
        Minecraft.getInstance().getSoundManager().play(sound);
    }

    @Override
    public void stopPlaying() {
        Minecraft.getInstance().getSoundManager().stop(sound);
    }
}
