package de.teamlapen.lib.proxy;


import de.teamlapen.lib.util.ISoundReference;
import de.teamlapen.lib.util.ParticleHandler;
import de.teamlapen.lib.util.ParticleHandlerClient;
import de.teamlapen.lib.util.SoundReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nonnull;

public class ClientProxy extends CommonProxy {

    private ParticleHandler clientParticleHandler = new ParticleHandlerClient();

    @Nonnull
    @Override
    public ISoundReference createSoundReference(SoundEvent event, SoundCategory category, BlockPos pos, float volume, float pinch) {
        return new SoundReference(new PositionedSoundRecord(event, category, volume, pinch, pos));
    }

    @Nonnull
    @Override
    public ISoundReference createSoundReference(SoundEvent event, SoundCategory category, double x, double y, double z, float volume, float pinch) {
        return new SoundReference(new PositionedSoundRecord(event, category, volume, pinch, (float) x, (float) y, (float) z));
    }

    @Override
    public ParticleHandler getParticleHandler() {
        return FMLCommonHandler.instance().getEffectiveSide().isClient() ? clientParticleHandler : serverParticleHandler;
    }

    @Override
    public EntityPlayer getPlayerEntity(MessageContext ctx) {

        //Need to double check the side for some reason
        return (ctx.side.isClient() ? Minecraft.getMinecraft().thePlayer : super.getPlayerEntity(ctx));
    }
}
