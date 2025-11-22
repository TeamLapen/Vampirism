package de.teamlapen.factions.common.network.packets;

import de.teamlapen.factions.api.util.REFERENCE;
import de.teamlapen.factions.common.network.packets.server.*;
import de.teamlapen.factions.common.server.ServerPayloadHandler;
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

    }
}
