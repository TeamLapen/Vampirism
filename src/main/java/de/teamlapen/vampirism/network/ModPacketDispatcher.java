package de.teamlapen.vampirism.network;

import de.teamlapen.lib.lib.network.AbstractPacketDispatcher;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraftforge.api.distmarker.Dist;

/**
 * Vampirism packet dispatcher
 */
public class ModPacketDispatcher extends AbstractPacketDispatcher {
    public ModPacketDispatcher() {
        super(REFERENCE.MODID);
    }

    @Override
    public void registerPackets() {
        registerMessage(InputEventPacket.Handler.class, InputEventPacket.class, Side.SERVER);
        registerMessage(SyncConfigPacket.Handler.class, SyncConfigPacket.class, Dist.CLIENT);
        //registerMessage(BloodValuePacket.Handler.class, BloodValuePacket.class, Dist.CLIENT);
    }
}
