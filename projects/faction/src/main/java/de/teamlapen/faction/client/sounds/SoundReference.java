package de.teamlapen.faction.client.sounds;

import de.teamlapen.faction.common.sounds.ISoundReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;

public record SoundReference(SoundInstance sound) implements ISoundReference {

    @Override
    public boolean isPlaying() {
        return soundManager().isActive(this.sound);
    }

    @Override
    public void startPlaying() {
        soundManager().play(this.sound);
    }

    @Override
    public void stopPlaying() {
        soundManager().stop(this.sound);
    }

    private SoundManager soundManager() {
        return Minecraft.getInstance().getSoundManager();
    }
}
