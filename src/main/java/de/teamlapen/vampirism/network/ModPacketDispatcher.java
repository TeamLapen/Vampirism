package de.teamlapen.vampirism.network;

import de.teamlapen.lib.lib.network.AbstractPacketDispatcher;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;

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
        dispatcher.registerMessage(nextID(), SSkillTreePacket.class, SSkillTreePacket::encode, SSkillTreePacket::decode, SSkillTreePacket::handle);
        dispatcher.registerMessage(nextID(), SOpenVampireBookPacket.class, SOpenVampireBookPacket::encode, SOpenVampireBookPacket::decode, SOpenVampireBookPacket::handle);
        dispatcher.registerMessage(nextID(), SBloodValuePacket.class, SBloodValuePacket::encode, SBloodValuePacket::decode, SBloodValuePacket::handle);
        dispatcher.registerMessage(nextID(), SPlayEventPacket.class, SPlayEventPacket::encode, SPlayEventPacket::decode, SPlayEventPacket::handle);
        dispatcher.registerMessage(nextID(), CSelectMinionTaskPacket.class, CSelectMinionTaskPacket::encode, CSelectMinionTaskPacket::decode, CSelectMinionTaskPacket::handle);
        dispatcher.registerMessage(nextID(), SRequestMinionSelectPacket.class, SRequestMinionSelectPacket::encode, SRequestMinionSelectPacket::decode, SRequestMinionSelectPacket::handle);
        dispatcher.registerMessage(nextID(), CAppearancePacket.class, CAppearancePacket::encode, CAppearancePacket::decode, CAppearancePacket::handle);
        dispatcher.registerMessage(nextID(), STaskStatusPacket.class, STaskStatusPacket::encode, STaskStatusPacket::decode, STaskStatusPacket::handle);
        dispatcher.registerMessage(nextID(), CTaskActionPacket.class, CTaskActionPacket::encode, CTaskActionPacket::decode, CTaskActionPacket::handle);
        dispatcher.registerMessage(nextID(), CUpgradeMinionStatPacket.class, CUpgradeMinionStatPacket::encode, CUpgradeMinionStatPacket::decode, CUpgradeMinionStatPacket::handle);
        dispatcher.registerMessage(nextID(), CActionBindingPacket.class, CActionBindingPacket::encode, CActionBindingPacket::decode, CActionBindingPacket::handle);
        dispatcher.registerMessage(nextID(), STaskPacket.class, STaskPacket::encode, STaskPacket::decode, STaskPacket::handle);
        dispatcher.registerMessage(nextID(), SUpdateMultiBossInfoPacket.class, SUpdateMultiBossInfoPacket::encode, SUpdateMultiBossInfoPacket::decode, SUpdateMultiBossInfoPacket::handle);
        dispatcher.registerMessage(nextID(), CSimpleInputEvent.class, CSimpleInputEvent::encode, CSimpleInputEvent::decode, CSimpleInputEvent::handle);
        dispatcher.registerMessage(nextID(), CStartFeedingPacket.class, CStartFeedingPacket::encode, CStartFeedingPacket::decode, CStartFeedingPacket::handle);
        dispatcher.registerMessage(nextID(), CToggleActionPacket.class, CToggleActionPacket::encode, CToggleActionPacket::decode, CToggleActionPacket::handle);
        dispatcher.registerMessage(nextID(), CUnlockSkillPacket.class, CUnlockSkillPacket::encode, CUnlockSkillPacket::decode, CUnlockSkillPacket::handle);
        dispatcher.registerMessage(nextID(), CNameItemPacket.class, CNameItemPacket::encode, CNameItemPacket::decode, CNameItemPacket::handle);
        dispatcher.registerMessage(nextID(), CToggleMinionTaskLock.class, CToggleMinionTaskLock::encode, CToggleMinionTaskLock::decode, CToggleMinionTaskLock::handle);
        dispatcher.registerMessage(nextID(), CDeleteRefinementPacket.class, CDeleteRefinementPacket::encode, CDeleteRefinementPacket::decode, CDeleteRefinementPacket::handle);
    }
}
