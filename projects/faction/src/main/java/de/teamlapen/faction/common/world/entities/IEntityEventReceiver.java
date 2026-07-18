package de.teamlapen.faction.common.world.entities;

import de.teamlapen.faction.api.world.entities.extensions.IEntity;
import de.teamlapen.faction.common.network.packets.client.ClientboundEventPacket;
import de.teamlapen.faction.common.world.IEventReceiver;
import net.minecraft.resources.Identifier;

public interface IEntityEventReceiver extends IEntity, IEventReceiver {

    default void sendEvent(Identifier id) {
        ClientboundEventPacket.sendToTracking(asEntity(), id);
    }
}
