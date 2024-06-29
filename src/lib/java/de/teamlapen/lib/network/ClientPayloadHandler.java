package de.teamlapen.lib.network;

import de.teamlapen.lib.HelperRegistry;
import de.teamlapen.lib.lib.storage.IAttachedSyncable;
import de.teamlapen.lib.lib.storage.ISyncable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientPayloadHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

    public static ClientPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void handleUpdateEntityPacket(ClientboundUpdateEntityPacket pkt, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            Level level = player.level();
            Entity e = level.getEntity(pkt.getId());
            if (e == null) {
                LOGGER.error("Did not find entity {}", pkt.getId());
                if (pkt.isPlayerItself()) {
                    LOGGER.error("Message is meant for player itself, but id mismatch {} {}. Loading anyway.", player.getId(), pkt.getId());
                    e = player;
                }
            }
            if (e != null) {
                if (pkt.getData() != null) {
                    if (e instanceof ISyncable syncable) {
                        syncable.deserializeUpdateNBT(player.registryAccess(), pkt.getData());
                    } else {
                        LOGGER.warn("Target entity {} does not implement ISyncable", e);
                    }
                }
                if (pkt.getAttachments() != null) {
                    for (String key : pkt.getAttachments().getAllKeys()) {
                        handleCapability(e, ResourceLocation.parse(key), pkt.getAttachments().getCompound(key));
                    }
                }
            }
        });
    }

    private static void handleCapability(Entity e, ResourceLocation key, CompoundTag data) {
        AttachmentType<IAttachedSyncable> cap = HelperRegistry.getSyncableEntityCaps().get(key);
        if (cap == null && e instanceof Player) {
            cap = HelperRegistry.getSyncablePlayerCaps().get(key);
        }
        if (cap == null) {
            LOGGER.warn("Capability with key {} is not registered in the HelperRegistry", key);
        } else {
            e.getData(cap).deserializeUpdateNBT(e.registryAccess(), data);
        }
    }
}
