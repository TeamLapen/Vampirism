package de.teamlapen.lib.common.sounds;

import de.teamlapen.lib.client.Client;
import de.teamlapen.lib.client.sound.ISoundReference;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class SoundHelper {

    private static final ISoundHandler soundHandler = FMLEnvironment.getDist() == Dist.CLIENT ? Client.createSoundHandler() : new DummySoundHandler();

    public static ISoundHandler getSoundHandler() {
        return soundHandler;
    }

    private static class DummySoundHandler implements ISoundHandler {
        private final static Logger LOGGER = LogManager.getLogger();

        @NotNull
        @Override
        public ISoundReference createMasterSoundReference(SoundEvent event, float volume, float pinch) {
            LOGGER.warn("Created sound reference server side. Nothing will happen");
            return new ISoundReference.Dummy();
        }

        @NotNull
        @Override
        public ISoundReference createSoundReference(SoundEvent event, SoundSource category, BlockPos pos, float volume, float pinch) {
            LOGGER.warn("Created sound reference server side. Nothing will happen");
            return new ISoundReference.Dummy();
        }

        @NotNull
        @Override
        public ISoundReference createSoundReference(SoundEvent event, SoundSource category, double x, double y, double z, float volume, float pinch) {
            LOGGER.warn("Created sound reference server side. Nothing will happen");
            return new ISoundReference.Dummy();
        }
    }
}
