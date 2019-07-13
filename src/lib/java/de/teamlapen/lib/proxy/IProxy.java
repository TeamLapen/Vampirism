package de.teamlapen.lib.proxy;

import de.teamlapen.lib.util.ISoundReference;
import de.teamlapen.lib.util.ParticleHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.List;


public interface IProxy {
    /**
     * Create a server and client friendly reference for a sound.
     * This only does something on client side, but does not throw a Class Cast exception on server side.
     * Internally creates a ISound.
     * Does not start playing.
     */
    @Nonnull
    ISoundReference createSoundReference(SoundEvent event, SoundCategory category, BlockPos pos, float volume, float pinch);

    /**
     * Create a server and client friendly reference for a sound.
     * This only does something on client side, but does not throw a Class Cast exception on server side.
     * Internally creates a ISound.
     * Does not start playing.
     */
    @Nonnull
    ISoundReference createSoundReference(SoundEvent event, SoundCategory category, double x, double y, double z, float volume, float pinch);

    /**
     * @return The string describing the currently active language. "English" on server side
     */
    String getActiveLanguage();

    ParticleHandler getParticleHandler();//TODO delete particlehandler

    PlayerEntity getPlayerEntity(NetworkEvent.Context context);

    /**
     * Uses font rendere on client side to wrap the given string to the given width
     */
    List<String> listFormattedStringToWidth(String str, int wrapWidth);

}
