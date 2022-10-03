package de.teamlapen.lib.proxy;


import de.teamlapen.lib.HelperRegistry;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.lib.network.ClientboundUpdateEntityPacket;
import de.teamlapen.lib.util.ISoundReference;
import de.teamlapen.lib.util.SoundReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {
    private static final Logger LOGGER = LogManager.getLogger();

    private static void handleCapability(Entity e, ResourceLocation key, CompoundTag data) {
        Capability<ISyncable.ISyncableEntityCapabilityInst> cap = HelperRegistry.getSyncableEntityCaps().get(key);
        if (cap == null && e instanceof Player) {
            cap = HelperRegistry.getSyncablePlayerCaps().get(key);
        }
        if (cap == null) {
            LOGGER.warn("Capability with key {} is not registered in the HelperRegistry", key);
        } else {
            LazyOptional<ISyncable.ISyncableEntityCapabilityInst> opt = e.getCapability(cap); //Lazy Optional is kinda strange
            opt.ifPresent(inst -> inst.loadUpdateFromNBT(data));
            if (!opt.isPresent()) {
                LOGGER.warn("Target entity {} does not have capability {}", e, cap);
            }
        }
    }

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
        return Minecraft.getInstance().getLanguageManager().getSelected().toString();
    }

    @Override
    public @Nullable Player getPlayerEntity(NetworkEvent.@NotNull Context ctx) {
        //Need to double-check the side for some reason
        return (EffectiveSide.get() == LogicalSide.CLIENT ? Minecraft.getInstance().player : super.getPlayerEntity(ctx));
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

    @Override
    public void handleUpdateEntityPacket(@NotNull ClientboundUpdateEntityPacket msg) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            LOGGER.error("Cannot handle update package because sending player entity is null. Message: {}", msg);
        } else {
            Entity e = player.getCommandSenderWorld().getEntity(msg.getId());
            if (e == null) {
                LOGGER.error("Did not find entity {}", msg.getId());
                if (msg.isPlayerItself()) {
                    LOGGER.error("Message is meant for player itself, but id mismatch {} {}. Loading anyway.", player.getId(), msg.getId());
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

                    for (String key : msg.getCaps().getAllKeys()) {
                        handleCapability(e, new ResourceLocation(key), msg.getCaps().getCompound(key));
                    }


                }
            }
        }
    }
}
