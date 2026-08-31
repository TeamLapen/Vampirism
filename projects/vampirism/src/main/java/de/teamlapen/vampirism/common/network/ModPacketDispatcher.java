package de.teamlapen.vampirism.common.network;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.network.ClientPayloadHandler;
import de.teamlapen.vampirism.common.network.packets.client.*;
import de.teamlapen.vampirism.common.network.packets.common.PlayerOwnedBlockEntityLockPacket;
import de.teamlapen.vampirism.common.network.packets.server.*;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModPacketDispatcher {

    private static final String PROTOCOL_VERSION = Integer.toString(2);

    public static void registerHandler(RegisterPayloadHandlersEvent event) {
        registerPackets(event.registrar(REFERENCE.MODID).versioned(PROTOCOL_VERSION));
    }

    @SuppressWarnings("Convert2MethodRef")
    public static void registerPackets(PayloadRegistrar registrar) {
        registrar.playToClient(ClientboundOpenVampireBookPacket.TYPE, ClientboundOpenVampireBookPacket.CODEC, (p, l) -> ClientPayloadHandler.handleVampireBookPacket(p, l));
        registrar.playToClient(ClientboundPlayEventPacket.TYPE, ClientboundPlayEventPacket.CODEC, (msg, context) -> ClientPayloadHandler.handlePlayEventPacket(msg, context));
        registrar.playToClient(ClientboundSundamagePacket.TYPE, ClientboundSundamagePacket.CODEC, (msg, context) -> ClientPayloadHandler.handleSundamageData(msg, context));
        registrar.playToClient(ClientboundBossEventSoundPacket.TYPE, ClientboundBossEventSoundPacket.CODEC, (msg, context) -> ClientPayloadHandler.handleBossEventSound(msg, context));
        registrar.playToClient(ClientboundUpdateGarlicEmitterPacket.TYPE, ClientboundUpdateGarlicEmitterPacket.CODEC, (msg, context) -> ClientPayloadHandler.handleUpdateGarlicEmitterPacket(msg, context));
        registrar.playToClient(ClientboundAddGarlicEmitterPacket.TYPE, ClientboundAddGarlicEmitterPacket.CODEC, (msg, context) -> ClientPayloadHandler.handleAddGarlicEmitterPacket(msg, context));
        registrar.playToClient(ClientboundRemoveGarlicEmitterPacket.TYPE, ClientboundRemoveGarlicEmitterPacket.CODEC, (msg, context) -> ClientPayloadHandler.handleRemoveGarlicEmitterPacket(msg, context));
        registrar.playToClient(ClientboundUpdateFogEmitterPacket.TYPE, ClientboundUpdateFogEmitterPacket.CODEC, (msg, context) -> ClientPayloadHandler.handleUpdateFogEmitterPacket(msg, context));
        registrar.playToClient(ClientboundAddFogEmitterPacket.TYPE, ClientboundAddFogEmitterPacket.CODEC, (msg, context) -> ClientPayloadHandler.handleAddFogEmitterPacket(msg, context));
        registrar.playToClient(ClientboundRemoveFogEmitterPacket.TYPE, ClientboundRemoveFogEmitterPacket.CODEC, (msg, context) -> ClientPayloadHandler.handleRemoveFogEmitterPacket(msg, context));
        registrar.playToClient(ClientboundUpdateDimensionsPacket.TYPE, ClientboundUpdateDimensionsPacket.STREAM_CODEC, (msg, context) -> ClientPayloadHandler.handleUpdateDimensionPacket(msg, context));
        registrar.playToClient(ClientboundDraculaEventPacket.TYPE, ClientboundDraculaEventPacket.CODEC, (msg, context) -> ClientPayloadHandler.handleDraculaEventPacket(msg, context));
        registrar.playToClient(ClientboundVelmorraCollapsePacket.TYPE, ClientboundVelmorraCollapsePacket.CODEC, (msg, context) -> ClientPayloadHandler.handleVelmorraCollapsePacket(msg, context));
        registrar.playToClient(ClientboundHeritagePacket.TYPE, ClientboundHeritagePacket.CODEC, (msg, context) -> ClientPayloadHandler.handleHeritagePacket(msg, context));

        registrar.playToServer(ServerboundAppearancePacket.TYPE, ServerboundAppearancePacket.CODEC, (msg, context) -> ServerPayloadHandler.handleAppearancePacket(msg, context));
        registrar.playToServer(ServerboundStartFeedingPacket.TYPE, ServerboundStartFeedingPacket.CODEC, (msg, context) -> ServerPayloadHandler.handleStartFeedingPacket(msg, context));
        registrar.playToServer(ServerboundNameItemPacket.TYPE, ServerboundNameItemPacket.CODEC, (msg, context) -> ServerPayloadHandler.handleNameItemPacket(msg, context));
        registrar.playToServer(ServerboundSelectAmmoTypePacket.TYPE, ServerboundSelectAmmoTypePacket.CODEC, (msg, context) -> ServerPayloadHandler.handleSelectAmmoTypePacket(msg, context));
        registrar.playToServer(ServerboundSetVampireBeaconPacket.TYPE, ServerboundSetVampireBeaconPacket.CODEC, (msg, context) -> ServerPayloadHandler.handleSetVampireBeaconPacket(msg, context));
        registrar.playToServer(ServerboundSimpleInputEvent.TYPE, ServerboundSimpleInputEvent.CODEC, (msg, context) -> ServerPayloadHandler.handleSimpleInputEvent(msg, context));
        registrar.playToServer(ServerboundRequestHeritagePacket.TYPE, ServerboundRequestHeritagePacket.CODEC, (msg, context) -> ServerPayloadHandler.handleRequestHeritagePacket(msg, context));

        registrar.playBidirectional(PlayerOwnedBlockEntityLockPacket.TYPE, PlayerOwnedBlockEntityLockPacket.CODEC, (msg, context) -> CommonPayloadHandler.handlePlayerOwnedBlockEntityLockPacket(msg, context), (msg, context) -> CommonPayloadHandler.handlePlayerOwnedBlockEntityLockPacket(msg, context));

    }

}
