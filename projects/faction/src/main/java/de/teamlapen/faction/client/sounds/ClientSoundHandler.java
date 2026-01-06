package de.teamlapen.faction.client.sounds;

import de.teamlapen.faction.common.sounds.ISoundHandler;
import de.teamlapen.faction.common.sounds.ISoundReference;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

public class ClientSoundHandler implements ISoundHandler {

    @NotNull
    @Override
    public ISoundReference createMasterSoundReference(@NotNull SoundEvent event, float volume, float pinch) {
        return new SoundReference(SimpleSoundInstance.forUI(event, volume, pinch));
    }

    @NotNull
    @Override
    public ISoundReference createSoundReference(@NotNull SoundEvent event, @NotNull SoundSource category, @NotNull BlockPos pos, float volume, float pinch) {
        return new SoundReference(new SimpleSoundInstance(event, category, volume, pinch, RandomSource.create(), pos));
    }

    @NotNull
    @Override
    public ISoundReference createSoundReference(@NotNull SoundEvent event, @NotNull SoundSource category, double x, double y, double z, float volume, float pinch) {
        return new SoundReference(new SimpleSoundInstance(event, category, volume, pinch, RandomSource.create(), (float) x, (float) y, (float) z));
    }
}
