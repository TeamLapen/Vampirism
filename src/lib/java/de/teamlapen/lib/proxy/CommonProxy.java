package de.teamlapen.lib.proxy;

import de.teamlapen.lib.util.ISoundReference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;


public class CommonProxy implements IProxy {
    private final static Logger LOGGER = LogManager.getLogger();

    @Nonnull
    @Override
    public ISoundReference createSoundReference(SoundEvent event, SoundCategory category, BlockPos pos, float volume, float pinch) {
        LOGGER.warn("Created sound reference server side. Nothing will happen");
        return new ISoundReference.Dummy();
    }

    @Nonnull
    @Override
    public ISoundReference createSoundReference(SoundEvent event, SoundCategory category, double x, double y, double z, float volume, float pinch) {
        LOGGER.warn("Created sound reference server side. Nothing will happen");
        return new ISoundReference.Dummy();
    }

    @Override
    public String getActiveLanguage() {
        return "English";
    }


    @Override
    public PlayerEntity getPlayerEntity(NetworkEvent.Context ctx) {
        return ctx.getSender();
    }

    @Override
    public List<String> listFormattedStringToWidth(String str, int wrapWidth) {
        return Arrays.asList(WordUtils.wrap(str, wrapWidth / 6, "\n", false).split("\n"));
    }
}
