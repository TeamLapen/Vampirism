package de.teamlapen.lib.proxy;

import de.teamlapen.lib.network.UpdateEntityPacket;
import de.teamlapen.lib.util.ISoundReference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


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

    PlayerEntity getPlayerEntity(NetworkEvent.Context context);

    default void handleUpdateEntityPacket(UpdateEntityPacket msg) {
    }

    /**
     * Try to obtain the world from the given key. Null if not loaded or not accessible (on client)
     */
    @Nullable
    World getWorldFromKey(RegistryKey<World> dimension);

}
