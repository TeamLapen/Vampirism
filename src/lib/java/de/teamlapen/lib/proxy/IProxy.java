package de.teamlapen.lib.proxy;

import de.teamlapen.lib.network.UpdateEntityPacket;
import de.teamlapen.lib.util.ISoundReference;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public interface IProxy {
    @NotNull
    ISoundReference createMasterSoundReference(SoundEvent event, float volume, float pinch);

    /**
     * Create a server and client friendly reference for a sound.
     * This only does something on client side, but does not throw a Class Cast exception on server side.
     * Internally creates a ISound.
     * Does not start playing.
     */
    @NotNull
    ISoundReference createSoundReference(SoundEvent event, SoundSource category, BlockPos pos, float volume, float pinch);

    /**
     * Create a server and client friendly reference for a sound.
     * This only does something on client side, but does not throw a Class Cast exception on server side.
     * Internally creates a ISound.
     * Does not start playing.
     */
    @NotNull
    ISoundReference createSoundReference(SoundEvent event, SoundSource category, double x, double y, double z, float volume, float pinch);

    /**
     * @return The string describing the currently active language. "English" on server side
     */
    String getActiveLanguage();

    Player getPlayerEntity(NetworkEvent.Context context);

    /**
     * Try to obtain the world from the given key. Null if not loaded or not accessible (on client)
     */
    @Nullable
    Level getWorldFromKey(ResourceKey<Level> dimension);

    default void handleUpdateEntityPacket(UpdateEntityPacket msg) {
    }

}
