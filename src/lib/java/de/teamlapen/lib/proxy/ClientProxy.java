package de.teamlapen.lib.proxy;


import de.teamlapen.lib.util.ISoundReference;
import de.teamlapen.lib.util.SoundReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ClientProxy extends CommonProxy {

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

    @Override
    public @NotNull String getActiveLanguage() {
        return Minecraft.getInstance().getLanguageManager().getSelected();
    }

    @Override
    public Level getWorldFromKey(ResourceKey<Level> world) {
        Level serverWorld = super.getWorldFromKey(world);
        if (serverWorld != null) return serverWorld;
        Level clientWorld = Minecraft.getInstance().level;
        if (clientWorld != null) {
            if (clientWorld.dimension().equals(world)) {
                return clientWorld;
            }
        }
        return null;
    }

}
