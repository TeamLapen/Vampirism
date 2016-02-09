package de.teamlapen.vampirism.network;

import de.teamlapen.lib.lib.network.AbstractPacketDispatcher;
import de.teamlapen.vampirism.util.REFERENCE;

/**
 * Vampirism packet dispatcher
 */
public class ModPacketDispatcher extends AbstractPacketDispatcher {
    public ModPacketDispatcher() {
        super(REFERENCE.MODID);
    }

    @Override
    public void registerPackets() {

    }
}
