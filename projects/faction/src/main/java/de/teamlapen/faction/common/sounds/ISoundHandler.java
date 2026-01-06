package de.teamlapen.faction.common.sounds;

import de.teamlapen.faction.FactionsMod;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.NotNull;

public interface ISoundHandler {

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

    static ISoundHandler getSoundHandler() {
        return FactionsMod.proxy.getSoundHandler();
    }

    class NoOpSoundHandler implements ISoundHandler {
        private final static ISoundReference DUMMY = new ISoundReference.NoOp();

        @NotNull
        @Override
        public ISoundReference createMasterSoundReference(SoundEvent event, float volume, float pinch) { return DUMMY; }

        @NotNull
        @Override
        public ISoundReference createSoundReference(SoundEvent event, SoundSource category, BlockPos pos, float volume, float pinch) { return DUMMY; }

        @NotNull
        @Override
        public ISoundReference createSoundReference(SoundEvent event, SoundSource category, double x, double y, double z, float volume, float pinch) { return DUMMY; }
    }
}
