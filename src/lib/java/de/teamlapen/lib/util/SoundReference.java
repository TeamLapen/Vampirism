package de.teamlapen.lib.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Reference to a ISound
 *
 * @author maxanier
 */
@OnlyIn(Dist.CLIENT)
public class SoundReference implements ISoundReference {

    private final ISound sound;

    public SoundReference(ISound sound) {
        this.sound = sound;
    }

    @Override
    public boolean isPlaying() {
        return Minecraft.getInstance().getSoundHandler().isSoundPlaying(sound);
    }

    @Override
    public void startPlaying() {
        Minecraft.getInstance().getSoundHandler().playSound(sound);
    }

    @Override
    public void stopPlaying() {
        Minecraft.getInstance().getSoundHandler().stopSound(sound);
    }
}
