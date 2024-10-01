package de.teamlapen.vampirism.network;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.ClientPayloadHandler;
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
        registrar.playToClient(ClientboundSundamagePacket.TYPE, ClientboundSundamagePacket.CODEC, (msg, context) -> ClientPayloadHandler.handleSundamageData(msg, context));
        registrar.playToClient(ClientboundBossEventSoundPacket.TYPE, ClientboundBossEventSoundPacket.CODEC, (msg, context) -> ClientPayloadHandler.handleBossEventSound(msg, context));
        registrar.playToClient(ClientboundSkillTreePacket.TYPE, ClientboundSkillTreePacket.CODEC, (msg, context) -> ClientPayloadHandler.handleSkillTreePacket(msg, context));

        registrar.playToServer(ServerboundSelectMinionTaskPacket.TYPE, ServerboundSelectMinionTaskPacket.CODEC, (msg, context) -> ServerPayloadHandler.getInstance().handleSelectMinionTaskPacket(msg, context));
        registrar.playToServer(ServerboundAppearancePacket.TYPE, ServerboundAppearancePacket.CODEC, (msg, context) -> ServerPayloadHandler.getInstance().handleAppearancePacket(msg, context));
        registrar.playToServer(ServerboundTaskActionPacket.TYPE, ServerboundTaskActionPacket.CODEC, (msg, context) -> ServerPayloadHandler.getInstance().handleTaskActionPacket(msg, context));
        registrar.playToServer(ServerboundUpgradeMinionStatPacket.TYPE, ServerboundUpgradeMinionStatPacket.CODEC, (msg, context) -> ServerPayloadHandler.getInstance().handleUpgradeMinionStatPacket(msg, context));
        registrar.playToServer(ServerboundActionBindingPacket.TYPE, ServerboundActionBindingPacket.CODEC, (msg, context) -> ServerPayloadHandler.getInstance().handleActionBindingPacket(msg, context));
        registrar.playToServer(ServerboundSimpleInputEvent.TYPE, ServerboundSimpleInputEvent.CODEC, (msg, context) -> ServerPayloadHandler.getInstance().handleSimpleInputEvent(msg, context));
        registrar.playToServer(ServerboundStartFeedingPacket.TYPE, ServerboundStartFeedingPacket.CODEC, (msg, context) -> ServerPayloadHandler.getInstance().handleStartFeedingPacket(msg, context));
        registrar.playToServer(ServerboundToggleActionPacket.TYPE, ServerboundToggleActionPacket.CODEC, (msg, context) -> ServerPayloadHandler.getInstance().handleToggleActionPacket(msg, context));
        registrar.playToServer(ServerboundUnlockSkillPacket.TYPE, ServerboundUnlockSkillPacket.CODEC, (msg, context) -> ServerPayloadHandler.getInstance().handleUnlockSkillPacket(msg, context));
        registrar.playToServer(ServerboundNameItemPacket.TYPE, ServerboundNameItemPacket.CODEC, (msg, context) -> ServerPayloadHandler.getInstance().handleNameItemPacket(msg, context));
        registrar.playToServer(ServerboundToggleMinionTaskLock.TYPE, ServerboundToggleMinionTaskLock.CODEC, (msg, context) -> ServerPayloadHandler.getInstance().handleToggleMinionTaskLock(msg, context));
        registrar.playToServer(ServerboundDeleteRefinementPacket.TYPE, ServerboundDeleteRefinementPacket.CODEC, (msg, context) -> ServerPayloadHandler.getInstance().handleDeleteRefinementPacket(msg, context));
        registrar.playToServer(ServerboundSelectAmmoTypePacket.TYPE, ServerboundSelectAmmoTypePacket.CODEC, (msg, context) -> ServerPayloadHandler.getInstance().handleSelectAmmoTypePacket(msg, context));
        registrar.playToServer(ServerboundSetVampireBeaconPacket.TYPE, ServerboundSetVampireBeaconPacket.CODEC, (msg, context) -> ServerPayloadHandler.getInstance().handleSetVampireBeaconPacket(msg, context));
        registrar.playToServer(ServerboundRequestSkillTreePacket.TYPE, ServerboundRequestSkillTreePacket.CODEC, (msg, context) -> ServerPayloadHandler.getInstance().handleRequestSkillTreePacket(msg, context));

    }

}
