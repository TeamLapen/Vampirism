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
        dispatcher.registerMessage(nextID(), ClientboundSkillTreePacket.class, ClientboundSkillTreePacket::encode, ClientboundSkillTreePacket::decode, ClientboundSkillTreePacket::handle);
        dispatcher.registerMessage(nextID(), ClientboundOpenVampireBookPacket.class, ClientboundOpenVampireBookPacket::encode, ClientboundOpenVampireBookPacket::decode, ClientboundOpenVampireBookPacket::handle);
        dispatcher.registerMessage(nextID(), ClientboundBloodValuePacket.class, ClientboundBloodValuePacket::encode, ClientboundBloodValuePacket::decode, ClientboundBloodValuePacket::handle);
        dispatcher.registerMessage(nextID(), ClientboundPlayEventPacket.class, ClientboundPlayEventPacket::encode, ClientboundPlayEventPacket::decode, ClientboundPlayEventPacket::handle);
        dispatcher.registerMessage(nextID(), ServerboundSelectMinionTaskPacket.class, ServerboundSelectMinionTaskPacket::encode, ServerboundSelectMinionTaskPacket::decode, ServerboundSelectMinionTaskPacket::handle);
        dispatcher.registerMessage(nextID(), ClientboundRequestMinionSelectPacket.class, ClientboundRequestMinionSelectPacket::encode, ClientboundRequestMinionSelectPacket::decode, ClientboundRequestMinionSelectPacket::handle);
        dispatcher.registerMessage(nextID(), ServerboundAppearancePacket.class, ServerboundAppearancePacket::encode, ServerboundAppearancePacket::decode, ServerboundAppearancePacket::handle);
        dispatcher.registerMessage(nextID(), ClientboundTaskStatusPacket.class, ClientboundTaskStatusPacket::encode, ClientboundTaskStatusPacket::decode, ClientboundTaskStatusPacket::handle);
        dispatcher.registerMessage(nextID(), ServerboundTaskActionPacket.class, ServerboundTaskActionPacket::encode, ServerboundTaskActionPacket::decode, ServerboundTaskActionPacket::handle);
        dispatcher.registerMessage(nextID(), ServerboundUpgradeMinionStatPacket.class, ServerboundUpgradeMinionStatPacket::encode, ServerboundUpgradeMinionStatPacket::decode, ServerboundUpgradeMinionStatPacket::handle);
        dispatcher.registerMessage(nextID(), ServerboundActionBindingPacket.class, ServerboundActionBindingPacket::encode, ServerboundActionBindingPacket::decode, ServerboundActionBindingPacket::handle);
        dispatcher.registerMessage(nextID(), ClientboundTaskPacket.class, ClientboundTaskPacket::encode, ClientboundTaskPacket::decode, ClientboundTaskPacket::handle);
        dispatcher.registerMessage(nextID(), ClientboundUpdateMultiBossEventPacket.class, ClientboundUpdateMultiBossEventPacket::encode, ClientboundUpdateMultiBossEventPacket::decode, ClientboundUpdateMultiBossEventPacket::handle);
        dispatcher.registerMessage(nextID(), ServerboundSimpleInputEvent.class, ServerboundSimpleInputEvent::encode, ServerboundSimpleInputEvent::decode, ServerboundSimpleInputEvent::handle);
        dispatcher.registerMessage(nextID(), ServerboundStartFeedingPacket.class, ServerboundStartFeedingPacket::encode, ServerboundStartFeedingPacket::decode, ServerboundStartFeedingPacket::handle);
        dispatcher.registerMessage(nextID(), ServerboundToggleActionPacket.class, ServerboundToggleActionPacket::encode, ServerboundToggleActionPacket::decode, ServerboundToggleActionPacket::handle);
        dispatcher.registerMessage(nextID(), ServerboundUnlockSkillPacket.class, ServerboundUnlockSkillPacket::encode, ServerboundUnlockSkillPacket::decode, ServerboundUnlockSkillPacket::handle);
        dispatcher.registerMessage(nextID(), ServerboundNameItemPacket.class, ServerboundNameItemPacket::encode, ServerboundNameItemPacket::decode, ServerboundNameItemPacket::handle);
        dispatcher.registerMessage(nextID(), ServerboundToggleMinionTaskLock.class, ServerboundToggleMinionTaskLock::encode, ServerboundToggleMinionTaskLock::decode, ServerboundToggleMinionTaskLock::handle);
        dispatcher.registerMessage(nextID(), ServerboundDeleteRefinementPacket.class, ServerboundDeleteRefinementPacket::encode, ServerboundDeleteRefinementPacket::decode, ServerboundDeleteRefinementPacket::handle);
    }
}
