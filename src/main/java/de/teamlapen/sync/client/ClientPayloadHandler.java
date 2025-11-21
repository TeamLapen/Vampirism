package de.teamlapen.sync.client;

import de.teamlapen.sync.common.packages.ClientboundUpdateEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
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
//                if (pkt.getData() != null) {
//                    if (e instanceof ISyncable syncable) {
//                        syncable.deserializeUpdate(player.registryAccess(), pkt.getData());
//                    } else {
//                        LOGGER.warn("Target entity {} does not implement ISyncable", e);
//                    }
//                }
//                if (pkt.getAttachments() != null) {
//                    for (String key : pkt.getAttachments().getAllKeys()) {
//                        handleCapability(e, ResourceLocation.parse(key), pkt.getAttachments().getCompound(key));
//                    }
//                }
            }
        });
    }
}
