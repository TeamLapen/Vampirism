package de.teamlapen.library.common.network;

import de.teamlapen.library.common.network.packets.PlayerOwnedBlockEntityLockPacket;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class LibraryPacketDispatcher {

    private static final String PROTOCOL_VERSION = String.format("%s/%s/%s", de.teamlapen.faction.api.util.REFERENCE.MOD_ID, "library", 1);

    public static void registerHandler(RegisterPayloadHandlersEvent event) {
        registerPackets(event.registrar(PROTOCOL_VERSION));
    }

    @SuppressWarnings("Convert2MethodRef")
    public static void registerPackets(PayloadRegistrar registrar) {
        registrar.playBidirectional(PlayerOwnedBlockEntityLockPacket.TYPE, PlayerOwnedBlockEntityLockPacket.CODEC, (msg, context) -> LibraryPayloadHandler.handlePlayerOwnedBlockEntityLockPacket(msg, context), (msg, context) -> LibraryPayloadHandler.handlePlayerOwnedBlockEntityLockPacket(msg, context));
    }
}
