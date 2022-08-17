package de.teamlapen.lib.proxy;

import de.teamlapen.lib.util.ISoundReference;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class CommonProxy implements IProxy {
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

    @Override
    public @NotNull String getActiveLanguage() {
        return "English";
    }


    @Override
    public @Nullable Player getPlayerEntity(NetworkEvent.@NotNull Context ctx) {
        return ctx.getSender();
    }

    @Override
    public @Nullable Level getWorldFromKey(@NotNull ResourceKey<Level> world) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            return server.getLevel(world);
        }
        return null;
    }
}
