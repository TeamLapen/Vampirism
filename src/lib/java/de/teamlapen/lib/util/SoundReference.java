package de.teamlapen.lib.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * 1.10
 * Reference to a ISound
 *
 * @author maxanier
 */
@SideOnly(Side.CLIENT)
public class SoundReference implements ISoundReference {

    private final ISound sound;

    public SoundReference(ISound sound) {
        this.sound = sound;
    }

    @Override
    public boolean isPlaying() {
        return Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(sound);
    }

    @Override
    public void startPlaying() {
        Minecraft.getMinecraft().getSoundHandler().playSound(sound);
    }

    @Override
    public void stopPlaying() {
        Minecraft.getMinecraft().getSoundHandler().stopSound(sound);
    }
}
