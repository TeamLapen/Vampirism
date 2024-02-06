package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.ClientPayloadHandler;
import de.teamlapen.vampirism.server.ServerPayloadHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public class ModPacketDispatcher {

    private static final String PROTOCOL_VERSION = Integer.toString(1);

    @SubscribeEvent
    public static void registerHandler(RegisterPayloadHandlerEvent event) {
        registerPackets(event.registrar(REFERENCE.MODID).versioned(PROTOCOL_VERSION));
    }

    @SuppressWarnings("Convert2MethodRef")
    public static void registerPackets(IPayloadRegistrar registrar) {
        registrar.play(ClientboundOpenVampireBookPacket.ID, jsonReader(ClientboundOpenVampireBookPacket.CODEC), handler -> handler.client((p, l) -> ClientPayloadHandler.handleVampireBookPacket(p, l)));
        registrar.play(ClientboundPlayEventPacket.ID, jsonReader(ClientboundPlayEventPacket.CODEC), handler -> handler.client((msg, context) -> ClientPayloadHandler.handlePlayEventPacket(msg, context)));
        registrar.play(ClientboundRequestMinionSelectPacket.ID, jsonReader(ClientboundRequestMinionSelectPacket.CODEC), handler -> handler.client((msg, context) -> ClientPayloadHandler.handleRequestMinionSelectPacket(msg, context)));
        registrar.play(ClientboundTaskStatusPacket.ID, jsonReader(ClientboundTaskStatusPacket.CODEC), handler -> handler.client((msg, context) -> ClientPayloadHandler.handleTaskStatusPacket(msg, context)));
        registrar.play(ClientboundTaskPacket.ID, jsonReader(ClientboundTaskPacket.CODEC), handler -> handler.client((msg, context) -> ClientPayloadHandler.handleTaskPacket(msg, context)));
        registrar.play(ClientboundUpdateMultiBossEventPacket.ID, jsonReader(ClientboundUpdateMultiBossEventPacket.CODEC), handler -> handler.client((msg, context) -> ClientPayloadHandler.handleUpdateMultiBossInfoPacket(msg, context)));
        registrar.common(ClientboundSundamagePacket.ID, jsonReader(ClientboundSundamagePacket.CODEC), handler -> handler.client((msg, context) -> ClientPayloadHandler.handleSundamageData(msg, context)));
        registrar.play(ClientboundBossEventSoundPacket.ID, jsonReader(ClientboundBossEventSoundPacket.CODEC), handler -> handler.client((msg, context) -> ClientPayloadHandler.handleBossEventSound(msg, context)));
        registrar.play(ClientboundSkillTreePacket.ID, jsonReader(ClientboundSkillTreePacket.CODEC), handler -> handler.client((msg, context) -> ClientPayloadHandler.handleSkillTreePacket(msg, context)));

        registrar.common(ServerboundSelectMinionTaskPacket.ID, jsonReader(ServerboundSelectMinionTaskPacket.CODEC), handler -> handler.server(ServerPayloadHandler.getInstance()::handleSelectMinionTaskPacket));
        registrar.common(ServerboundAppearancePacket.ID, jsonReader(ServerboundAppearancePacket.CODEC), handler -> handler.server(ServerPayloadHandler.getInstance()::handleAppearancePacket));
        registrar.common(ServerboundTaskActionPacket.ID, jsonReader(ServerboundTaskActionPacket.CODEC), handler -> handler.server(ServerPayloadHandler.getInstance()::handleTaskActionPacket));
        registrar.common(ServerboundUpgradeMinionStatPacket.ID, jsonReader(ServerboundUpgradeMinionStatPacket.CODEC), handler -> handler.server(ServerPayloadHandler.getInstance()::handleUpgradeMinionStatPacket));
        registrar.common(ServerboundActionBindingPacket.ID, jsonReader(ServerboundActionBindingPacket.CODEC), handler -> handler.server(ServerPayloadHandler.getInstance()::handleActionBindingPacket));
        registrar.common(ServerboundSimpleInputEvent.ID, jsonReader(ServerboundSimpleInputEvent.CODEC), handler -> handler.server(ServerPayloadHandler.getInstance()::handleSimpleInputEvent));
        registrar.common(ServerboundStartFeedingPacket.ID, jsonReader(ServerboundStartFeedingPacket.CODEC), handler -> handler.server(ServerPayloadHandler.getInstance()::handleStartFeedingPacket));
        registrar.common(ServerboundToggleActionPacket.ID, jsonReader(ServerboundToggleActionPacket.CODEC), handler -> handler.server(ServerPayloadHandler.getInstance()::handleToggleActionPacket));
        registrar.common(ServerboundUnlockSkillPacket.ID, jsonReader(ServerboundUnlockSkillPacket.CODEC), handler -> handler.server(ServerPayloadHandler.getInstance()::handleUnlockSkillPacket));
        registrar.common(ServerboundNameItemPacket.ID, jsonReader(ServerboundNameItemPacket.CODEC), handler -> handler.server(ServerPayloadHandler.getInstance()::handleNameItemPacket));
        registrar.common(ServerboundToggleMinionTaskLock.ID, jsonReader(ServerboundToggleMinionTaskLock.CODEC), handler -> handler.server(ServerPayloadHandler.getInstance()::handleToggleMinionTaskLock));
        registrar.common(ServerboundDeleteRefinementPacket.ID, jsonReader(ServerboundDeleteRefinementPacket.CODEC), handler -> handler.server(ServerPayloadHandler.getInstance()::handleDeleteRefinementPacket));
        registrar.common(ServerboundSelectAmmoTypePacket.ID, jsonReader(ServerboundSelectAmmoTypePacket.CODEC), handler -> handler.server(ServerPayloadHandler.getInstance()::handleSelectAmmoTypePacket));
        registrar.common(ServerboundSetVampireBeaconPacket.ID, jsonReader(ServerboundSetVampireBeaconPacket.CODEC), handler -> handler.server(ServerPayloadHandler.getInstance()::handleSetVampireBeaconPacket));
        registrar.play(ServerboundRequestSkillTreePacket.ID, ServerboundRequestSkillTreePacket::new, handler -> handler.server(ServerPayloadHandler.getInstance()::handleRequestSkillTreePacket));

    }

    protected static <T> FriendlyByteBuf.Reader<T> jsonReader(Codec<T> codec) {
        return buf -> buf.readJsonWithCodec(codec);
    }
}
