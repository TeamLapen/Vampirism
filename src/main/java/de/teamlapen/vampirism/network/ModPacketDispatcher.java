package de.teamlapen.vampirism.network;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.ClientPayloadHandler;
import de.teamlapen.vampirism.common.CommonPayloadHandler;
import de.teamlapen.vampirism.network.packet.fog.ClientboundAddFogEmitterPacket;
import de.teamlapen.vampirism.network.packet.fog.ClientboundRemoveFogEmitterPacket;
import de.teamlapen.vampirism.network.packet.fog.ClientboundUpdateFogEmitterPacket;
import de.teamlapen.vampirism.network.packet.garlic.ClientboundAddGarlicEmitterPacket;
import de.teamlapen.vampirism.network.packet.garlic.ClientboundRemoveGarlicEmitterPacket;
import de.teamlapen.vampirism.network.packet.garlic.ClientboundUpdateGarlicEmitterPacket;
import de.teamlapen.vampirism.server.ServerPayloadHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModPacketDispatcher {

    private static final String PROTOCOL_VERSION = Integer.toString(1);

    @SubscribeEvent
    public static void registerHandler(RegisterPayloadHandlersEvent event) {
        registerPackets(event.registrar(REFERENCE.MODID).versioned(PROTOCOL_VERSION));
    }

    @SuppressWarnings("Convert2MethodRef")
    public static void registerPackets(PayloadRegistrar registrar) {
        registrar.playToClient(ClientboundOpenVampireBookPacket.TYPE, ClientboundOpenVampireBookPacket.CODEC, (p, l) -> ClientPayloadHandler.handleVampireBookPacket(p, l));
        registrar.playToClient(ClientboundPlayEventPacket.TYPE, ClientboundPlayEventPacket.CODEC, (msg, context) -> ClientPayloadHandler.handlePlayEventPacket(msg, context));
        registrar.playToClient(ClientboundRequestMinionSelectPacket.TYPE, ClientboundRequestMinionSelectPacket.CODEC, (msg, context) -> ClientPayloadHandler.handleRequestMinionSelectPacket(msg, context));
        registrar.playToClient(ClientboundTaskStatusPacket.TYPE, ClientboundTaskStatusPacket.CODEC, (msg, context) -> ClientPayloadHandler.handleTaskStatusPacket(msg, context));
        registrar.playToClient(ClientboundTaskPacket.TYPE, ClientboundTaskPacket.CODEC, (msg, context) -> ClientPayloadHandler.handleTaskPacket(msg, context));
        registrar.playToClient(ClientboundUpdateMultiBossEventPacket.TYPE, ClientboundUpdateMultiBossEventPacket.CODEC,(msg, context) -> ClientPayloadHandler.handleUpdateMultiBossInfoPacket(msg, context));
        registrar.commonToClient(ClientboundSundamagePacket.TYPE, ClientboundSundamagePacket.CODEC, (msg, context) -> ClientPayloadHandler.handleSundamageData(msg, context));
        registrar.playToClient(ClientboundBossEventSoundPacket.TYPE, ClientboundBossEventSoundPacket.CODEC, (msg, context) -> ClientPayloadHandler.handleBossEventSound(msg, context));
        registrar.playToClient(ClientboundSkillTreePacket.TYPE, ClientboundSkillTreePacket.CODEC, (msg, context) -> ClientPayloadHandler.handleSkillTreePacket(msg, context));
        registrar.playToClient(ClientboundUpdateGarlicEmitterPacket.TYPE, ClientboundUpdateGarlicEmitterPacket.CODEC, (msg, context) -> ClientPayloadHandler.handleUpdateGarlicEmitterPacket(msg, context));
        registrar.playToClient(ClientboundAddGarlicEmitterPacket.TYPE, ClientboundAddGarlicEmitterPacket.CODEC, (msg, context) -> ClientPayloadHandler.handleAddGarlicEmitterPacket(msg, context));
        registrar.playToClient(ClientboundRemoveGarlicEmitterPacket.TYPE, ClientboundRemoveGarlicEmitterPacket.CODEC, (msg, context) -> ClientPayloadHandler.handleRemoveGarlicEmitterPacket(msg, context));
        registrar.playToClient(ClientboundUpdateFogEmitterPacket.TYPE, ClientboundUpdateFogEmitterPacket.CODEC, (msg, context) -> ClientPayloadHandler.handleUpdateFogEmitterPacket(msg, context));
        registrar.playToClient(ClientboundAddFogEmitterPacket.TYPE, ClientboundAddFogEmitterPacket.CODEC, (msg, context) -> ClientPayloadHandler.handleAddFogEmitterPacket(msg, context));
        registrar.playToClient(ClientboundRemoveFogEmitterPacket.TYPE, ClientboundRemoveFogEmitterPacket.CODEC, (msg, context) -> ClientPayloadHandler.handleRemoveFogEmitterPacket(msg, context));

        registrar.playToServer(ServerboundSelectMinionTaskPacket.TYPE, ServerboundSelectMinionTaskPacket.CODEC, (msg, context) -> ServerPayloadHandler.handleSelectMinionTaskPacket(msg, context));
        registrar.playToServer(ServerboundAppearancePacket.TYPE, ServerboundAppearancePacket.CODEC, (msg, context) -> ServerPayloadHandler.handleAppearancePacket(msg, context));
        registrar.playToServer(ServerboundTaskActionPacket.TYPE, ServerboundTaskActionPacket.CODEC, (msg, context) -> ServerPayloadHandler.handleTaskActionPacket(msg, context));
        registrar.playToServer(ServerboundUpgradeMinionStatPacket.TYPE, ServerboundUpgradeMinionStatPacket.CODEC, (msg, context) -> ServerPayloadHandler.handleUpgradeMinionStatPacket(msg, context));
        registrar.playToServer(ServerboundActionBindingPacket.TYPE, ServerboundActionBindingPacket.CODEC, (msg, context) -> ServerPayloadHandler.handleActionBindingPacket(msg, context));
        registrar.playToServer(ServerboundSimpleInputEvent.TYPE, ServerboundSimpleInputEvent.CODEC, (msg, context) -> ServerPayloadHandler.handleSimpleInputEvent(msg, context));
        registrar.playToServer(ServerboundStartFeedingPacket.TYPE, ServerboundStartFeedingPacket.CODEC, (msg, context) -> ServerPayloadHandler.handleStartFeedingPacket(msg, context));
        registrar.playToServer(ServerboundToggleActionPacket.TYPE, ServerboundToggleActionPacket.CODEC, (msg, context) -> ServerPayloadHandler.handleToggleActionPacket(msg, context));
        registrar.playToServer(ServerboundUnlockSkillPacket.TYPE, ServerboundUnlockSkillPacket.CODEC, (msg, context) -> ServerPayloadHandler.handleUnlockSkillPacket(msg, context));
        registrar.playToServer(ServerboundNameItemPacket.TYPE, ServerboundNameItemPacket.CODEC, (msg, context) -> ServerPayloadHandler.handleNameItemPacket(msg, context));
        registrar.playToServer(ServerboundToggleMinionTaskLock.TYPE, ServerboundToggleMinionTaskLock.CODEC, (msg, context) -> ServerPayloadHandler.handleToggleMinionTaskLock(msg, context));
        registrar.playToServer(ServerboundDeleteRefinementPacket.TYPE, ServerboundDeleteRefinementPacket.CODEC, (msg, context) -> ServerPayloadHandler.handleDeleteRefinementPacket(msg, context));
        registrar.playToServer(ServerboundSelectAmmoTypePacket.TYPE, ServerboundSelectAmmoTypePacket.CODEC, (msg, context) -> ServerPayloadHandler.handleSelectAmmoTypePacket(msg, context));
        registrar.playToServer(ServerboundSetVampireBeaconPacket.TYPE, ServerboundSetVampireBeaconPacket.CODEC, (msg, context) -> ServerPayloadHandler.handleSetVampireBeaconPacket(msg, context));
        registrar.playToServer(ServerboundRequestSkillTreePacket.TYPE, ServerboundRequestSkillTreePacket.CODEC, (msg, context) -> ServerPayloadHandler.handleRequestSkillTreePacket(msg, context));
        
        registrar.playBidirectional(PlayerOwnedBlockEntityLockPacket.TYPE, PlayerOwnedBlockEntityLockPacket.CODEC, (msg, context) -> CommonPayloadHandler.handlePlayerOwnedBlockEntityLockPacket(msg, context));

    }

}
