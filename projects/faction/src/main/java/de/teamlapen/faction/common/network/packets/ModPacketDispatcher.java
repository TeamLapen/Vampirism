package de.teamlapen.faction.common.network.packets;

import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.client.network.FactionClientPayloadHandler;
import de.teamlapen.faction.common.factions.skills.ClientboundSkillTreePacket;
import de.teamlapen.faction.common.network.packets.client.*;
import de.teamlapen.faction.common.network.packets.server.*;
import de.teamlapen.faction.common.server.ServerPayloadHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModPacketDispatcher {

    private static final String PROTOCOL_VERSION = Integer.toString(1);

    @SubscribeEvent
    public void registerHandler(RegisterPayloadHandlersEvent event) {
        registerPackets(event.registrar(REFERENCE.MOD_ID).versioned(PROTOCOL_VERSION));
    }

    @SuppressWarnings("Convert2MethodRef")
    public void registerPackets(PayloadRegistrar registrar) {
        registrar.playToServer(ServerboundSelectMinionTaskPacket.TYPE, ServerboundSelectMinionTaskPacket.CODEC, (msg, context) -> ServerPayloadHandler.handleSelectMinionTaskPacket(msg, context));
        registrar.playToServer(ServerboundTaskActionPacket.TYPE, ServerboundTaskActionPacket.CODEC, (msg, context) -> ServerPayloadHandler.handleTaskActionPacket(msg, context));
        registrar.playToServer(ServerboundActionBindingPacket.TYPE, ServerboundActionBindingPacket.CODEC, (msg, context) -> ServerPayloadHandler.handleActionBindingPacket(msg, context));
        registrar.playToServer(ServerboundToggleActionPacket.TYPE, ServerboundToggleActionPacket.CODEC, (msg, context) -> ServerPayloadHandler.handleToggleActionPacket(msg, context));
        registrar.playToServer(ServerboundUnlockSkillPacket.TYPE, ServerboundUnlockSkillPacket.CODEC, (msg, context) -> ServerPayloadHandler.handleUnlockSkillPacket(msg, context));
        registrar.playToServer(ServerboundToggleMinionTaskLock.TYPE, ServerboundToggleMinionTaskLock.CODEC, (msg, context) -> ServerPayloadHandler.handleToggleMinionTaskLock(msg, context));
        registrar.playToServer(ServerboundDeleteRefinementPacket.TYPE, ServerboundDeleteRefinementPacket.CODEC, (msg, context) -> ServerPayloadHandler.handleDeleteRefinementPacket(msg, context));
        registrar.playToServer(ServerboundRequestSkillTreePacket.TYPE, ServerboundRequestSkillTreePacket.CODEC, (msg, context) -> ServerPayloadHandler.handleRequestSkillTreePacket(msg, context));
        registrar.playToServer(ServerboundSimpleInputEvent.TYPE, ServerboundSimpleInputEvent.CODEC, (msg, context) -> ServerPayloadHandler.handleSimpleInputEvent(msg, context));
        registrar.playToClient(ClientboundTaskStatusPacket.TYPE, ClientboundTaskStatusPacket.CODEC, (msg, context) -> FactionClientPayloadHandler.handleTaskStatusPacket(msg, context));
        registrar.playToClient(ClientboundRequestMinionSelectPacket.TYPE, ClientboundRequestMinionSelectPacket.CODEC, (msg, context) -> FactionClientPayloadHandler.handleRequestMinionSelectPacket(msg, context));
        registrar.playToClient(ClientboundTaskPacket.TYPE, ClientboundTaskPacket.CODEC, (msg, context) -> FactionClientPayloadHandler.handleTaskPacket(msg, context));
        registrar.playToClient(ClientboundUpdateMultiBossEventPacket.TYPE, ClientboundUpdateMultiBossEventPacket.CODEC, (msg, context) -> FactionClientPayloadHandler.handleUpdateMultiBossInfoPacket(msg, context));
        registrar.playToClient(ClientboundSkillTreePacket.TYPE, ClientboundSkillTreePacket.CODEC, (msg, context) -> FactionClientPayloadHandler.handleSkillTreePacket(msg, context));
        registrar.playToClient(ClientboundPlaySoundEventPacket.TYPE, ClientboundPlaySoundEventPacket.CODEC, (msg, context) -> FactionClientPayloadHandler.handlePlaySoundEventPacket(msg, context));
        registrar.playToServer(ServerboundUpgradeMinionStatPacket.TYPE, ServerboundUpgradeMinionStatPacket.CODEC, (msg, context) -> ServerPayloadHandler.handleUpgradeMinionStatPacket(msg, context));
    }
}
