package de.teamlapen.lib.proxy;


import de.teamlapen.lib.util.ISoundReference;
import de.teamlapen.lib.util.ParticleHandler;
import de.teamlapen.lib.util.ParticleHandlerClient;
import de.teamlapen.lib.util.SoundReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.annotation.Nonnull;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {

    private final ParticleHandler clientParticleHandler = new ParticleHandlerClient();

    @Nonnull
    @Override
    public ISoundReference createSoundReference(SoundEvent event, SoundCategory category, BlockPos pos, float volume, float pinch) {
        return new SoundReference(new SimpleSound(event, category, volume, pinch, pos));
    }

    @Nonnull
    @Override
    public ISoundReference createSoundReference(SoundEvent event, SoundCategory category, double x, double y, double z, float volume, float pinch) {
        return new SoundReference(new SimpleSound(event, category, volume, pinch, (float) x, (float) y, (float) z));
    }

    @Override
    public String getActiveLanguage() {
        return Minecraft.getInstance().getLanguageManager().getCurrentLanguage().toString();
    }

    @Override
    public ParticleHandler getParticleHandler() {
        return EffectiveSide.get() == LogicalSide.CLIENT ? clientParticleHandler : serverParticleHandler;
    }

    @Override
    public PlayerEntity getPlayerEntity(NetworkEvent.Context ctx) {
        //Need to double check the side for some reason
        return (EffectiveSide.get() == LogicalSide.CLIENT ? Minecraft.getInstance().player : super.getPlayerEntity(ctx));
    }

    @Override
    public List<String> listFormattedStringToWidth(String str, int wrapWidth) {
        str = StringEscapeUtils.unescapeJava(str);
        return Minecraft.getInstance().fontRenderer.listFormattedStringToWidth(str, wrapWidth);
    }
}
