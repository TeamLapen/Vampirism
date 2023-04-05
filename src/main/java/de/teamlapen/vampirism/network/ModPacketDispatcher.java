package de.teamlapen.vampirism.network;

import de.teamlapen.lib.lib.network.AbstractPacketDispatcher;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;

/**
 * Vampirism packet dispatcher
 */
public class ModPacketDispatcher extends AbstractPacketDispatcher {

    private static final String PROTOCOL_VERSION = Integer.toString(1);

    public ModPacketDispatcher() {
        super(NetworkRegistry.ChannelBuilder.named(new ResourceLocation(REFERENCE.MODID, "main")).clientAcceptedVersions(PROTOCOL_VERSION::equals).serverAcceptedVersions(PROTOCOL_VERSION::equals).networkProtocolVersion(() -> PROTOCOL_VERSION).simpleChannel());
    }

    @Override
    public void registerPackets() {
        registerClientBound(ClientboundSkillTreePacket.class, ClientboundSkillTreePacket::encode, ClientboundSkillTreePacket::decode, ClientboundSkillTreePacket::handle);
        registerClientBound(ClientboundOpenVampireBookPacket.class, ClientboundOpenVampireBookPacket::encode, ClientboundOpenVampireBookPacket::decode, ClientboundOpenVampireBookPacket::handle);
        registerClientBound(ClientboundBloodValuePacket.class, ClientboundBloodValuePacket::encode, ClientboundBloodValuePacket::decode, ClientboundBloodValuePacket::handle);
        registerClientBound(ClientboundPlayEventPacket.class, ClientboundPlayEventPacket::encode, ClientboundPlayEventPacket::decode, ClientboundPlayEventPacket::handle);
        registerServerBound(ServerboundSelectMinionTaskPacket.class, ServerboundSelectMinionTaskPacket::encode, ServerboundSelectMinionTaskPacket::decode, ServerboundSelectMinionTaskPacket::handle);
        registerClientBound(ClientboundRequestMinionSelectPacket.class, ClientboundRequestMinionSelectPacket::encode, ClientboundRequestMinionSelectPacket::decode, ClientboundRequestMinionSelectPacket::handle);
        registerServerBound(ServerboundAppearancePacket.class, ServerboundAppearancePacket::encode, ServerboundAppearancePacket::decode, ServerboundAppearancePacket::handle);
        registerClientBound(ClientboundTaskStatusPacket.class, ClientboundTaskStatusPacket::encode, ClientboundTaskStatusPacket::decode, ClientboundTaskStatusPacket::handle);
        registerServerBound(ServerboundTaskActionPacket.class, ServerboundTaskActionPacket::encode, ServerboundTaskActionPacket::decode, ServerboundTaskActionPacket::handle);
        registerServerBound(ServerboundUpgradeMinionStatPacket.class, ServerboundUpgradeMinionStatPacket::encode, ServerboundUpgradeMinionStatPacket::decode, ServerboundUpgradeMinionStatPacket::handle);
        registerServerBound(ServerboundActionBindingPacket.class, ServerboundActionBindingPacket::encode, ServerboundActionBindingPacket::decode, ServerboundActionBindingPacket::handle);
        registerClientBound(ClientboundTaskPacket.class, ClientboundTaskPacket::encode, ClientboundTaskPacket::decode, ClientboundTaskPacket::handle);
        registerClientBound(ClientboundUpdateMultiBossEventPacket.class, ClientboundUpdateMultiBossEventPacket::encode, ClientboundUpdateMultiBossEventPacket::decode, ClientboundUpdateMultiBossEventPacket::handle);
        registerServerBound(ServerboundSimpleInputEvent.class, ServerboundSimpleInputEvent::encode, ServerboundSimpleInputEvent::decode, ServerboundSimpleInputEvent::handle);
        registerServerBound(ServerboundStartFeedingPacket.class, ServerboundStartFeedingPacket::encode, ServerboundStartFeedingPacket::decode, ServerboundStartFeedingPacket::handle);
        registerServerBound(ServerboundToggleActionPacket.class, ServerboundToggleActionPacket::encode, ServerboundToggleActionPacket::decode, ServerboundToggleActionPacket::handle);
        registerServerBound(ServerboundUnlockSkillPacket.class, ServerboundUnlockSkillPacket::encode, ServerboundUnlockSkillPacket::decode, ServerboundUnlockSkillPacket::handle);
        registerServerBound(ServerboundNameItemPacket.class, ServerboundNameItemPacket::encode, ServerboundNameItemPacket::decode, ServerboundNameItemPacket::handle);
        registerServerBound(ServerboundToggleMinionTaskLock.class, ServerboundToggleMinionTaskLock::encode, ServerboundToggleMinionTaskLock::decode, ServerboundToggleMinionTaskLock::handle);
        registerServerBound(ServerboundDeleteRefinementPacket.class, ServerboundDeleteRefinementPacket::encode, ServerboundDeleteRefinementPacket::decode, ServerboundDeleteRefinementPacket::handle);
        registerServerBound(ServerboundSelectAmmoTypePacket.class, ServerboundSelectAmmoTypePacket::encode, ServerboundSelectAmmoTypePacket::decode, ServerboundSelectAmmoTypePacket::handle);
        registerClientBound(ClientboundSundamagePacket.class, ClientboundSundamagePacket::encode, ClientboundSundamagePacket::decode, ClientboundSundamagePacket::handle);
    }
}
