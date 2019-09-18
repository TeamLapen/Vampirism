package de.teamlapen.lib.proxy;


import de.teamlapen.lib.HelperRegistry;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.lib.network.UpdateEntityPacket;
import de.teamlapen.lib.util.ISoundReference;
import de.teamlapen.lib.util.SoundReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {
    private static final Logger LOGGER = LogManager.getLogger();

    private static void handleCapability(Entity e, ResourceLocation key, CompoundNBT data) {
        Capability cap = HelperRegistry.getSyncableEntityCaps().get(key);
        if (cap == null && e instanceof PlayerEntity) {
            cap = HelperRegistry.getSyncablePlayerCaps().get(key);
        }
        if (cap == null) {
            LOGGER.warn("Capability with key {} is not registered in the HelperRegistry", key);
        } else {
            LazyOptional opt = e.getCapability(cap, null); //Lazy Optional is kinda strange
            opt.ifPresent(inst -> {
                if (inst instanceof ISyncable) {
                    ((ISyncable) inst).loadUpdateFromNBT(data);
                } else {
                    LOGGER.warn("Target entity's capability {} ({})does not implement ISyncable", inst, key);
                }
            });
            if (!opt.isPresent()) {
                LOGGER.warn("Target entity {} does not have capability {}", e, cap);

            }
        }
    }

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
    public PlayerEntity getPlayerEntity(NetworkEvent.Context ctx) {
        //Need to double check the side for some reason
        return (EffectiveSide.get() == LogicalSide.CLIENT ? Minecraft.getInstance().player : super.getPlayerEntity(ctx));
    }

    @Override
    public void handleUpdateEntityPacket(UpdateEntityPacket msg) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null) {
            LOGGER.error("Cannot handle update package because sending player entity is null. Message: {}", msg);
        } else {
            Entity e = player.getEntityWorld().getEntityByID(msg.getId());
            if (e == null) {
                LOGGER.error("Did not find entity {}", msg.getId());
                if (msg.isPlayerItself()) {
                    LOGGER.error("Message is meant for player itself, but id mismatch {} {}. Loading anyway.", player.getEntityId(), msg.getId());
                    e = player;
                }
            }
            if (e != null) {
                if (msg.getData() != null) {
                    ISyncable syncable;
                    try {
                        syncable = (ISyncable) e;
                        syncable.loadUpdateFromNBT(msg.getData());

                    } catch (ClassCastException ex) {
                        LOGGER.warn("Target entity {} does not implement ISyncable ({})", e, ex);
                    }
                }
                if (msg.getCaps() != null) {

                    for (String key : msg.getCaps().keySet()) {
                        handleCapability(e, new ResourceLocation(key), msg.getCaps().getCompound(key));
                    }


                }
            }
        }
    }

    @Override
    public List<String> listFormattedStringToWidth(String str, int wrapWidth) {
        str = StringEscapeUtils.unescapeJava(str);
        return Minecraft.getInstance().fontRenderer.listFormattedStringToWidth(str, wrapWidth);
    }
}
