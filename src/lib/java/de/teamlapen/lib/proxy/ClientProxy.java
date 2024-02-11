package de.teamlapen.lib.proxy;


import de.teamlapen.lib.HelperRegistry;
import de.teamlapen.lib.lib.storage.IAttachedSyncable;
import de.teamlapen.lib.lib.storage.ISyncable;
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
import net.neoforged.neoforge.attachment.AttachmentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ClientProxy extends CommonProxy {
    private static final Logger LOGGER = LogManager.getLogger();

    private static void handleCapability(Entity e, ResourceLocation key, CompoundTag data) {
        AttachmentType<IAttachedSyncable> cap = HelperRegistry.getSyncableEntityCaps().get(key);
        if (cap == null && e instanceof Player) {
            cap = HelperRegistry.getSyncablePlayerCaps().get(key);
        }
        if (cap == null) {
            LOGGER.warn("Capability with key {} is not registered in the HelperRegistry", key);
        } else {
            Optional<IAttachedSyncable> opt = Optional.ofNullable(e.getData(cap)); //Lazy Optional is kinda strange
            opt.ifPresent(inst -> inst.deserializeUpdateNBT(data));
            if (opt.isEmpty()) {
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
                        syncable.deserializeUpdateNBT(msg.getData());

                    } catch (ClassCastException ex) {
                        LOGGER.warn("Target entity {} does not implement ISyncable ({})", e, ex);
                    }
                }
                if (msg.getAttachments() != null) {

                    for (String key : msg.getAttachments().getAllKeys()) {
                        handleCapability(e, new ResourceLocation(key), msg.getAttachments().getCompound(key));
                    }


                }
            }
        }
    }
}
