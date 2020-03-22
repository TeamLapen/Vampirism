package de.teamlapen.vampirism.network;

import de.teamlapen.lib.lib.network.AbstractPacketDispatcher;
import de.teamlapen.vampirism.util.REFERENCE;
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
        dispatcher.registerMessage(nextID(), InputEventPacket.class, InputEventPacket::encode, InputEventPacket::decode, InputEventPacket::handle);
        dispatcher.registerMessage(nextID(), SkillTreePacket.class, SkillTreePacket::encode, SkillTreePacket::decode, SkillTreePacket::handle);
        dispatcher.registerMessage(nextID(), OpenVampireBookPacket.class, OpenVampireBookPacket::encode, OpenVampireBookPacket::decode, OpenVampireBookPacket::handle);
        dispatcher.registerMessage(nextID(), BloodValuePacket.class, BloodValuePacket::encode, BloodValuePacket::decode, BloodValuePacket::handle);
        dispatcher.registerMessage(nextID(), PlayEventPacket.class, PlayEventPacket::encode, PlayEventPacket::decode, PlayEventPacket::handle);
        dispatcher.registerMessage(nextID(), SelectMinionTaskPacket.class, SelectMinionTaskPacket::encode, SelectMinionTaskPacket::decode, SelectMinionTaskPacket::handle);
        dispatcher.registerMessage(nextID(), RequestMinionSelectPacket.class, RequestMinionSelectPacket::encode, RequestMinionSelectPacket::decode, RequestMinionSelectPacket::handle);
        dispatcher.registerMessage(nextID(), AppearancePacket.class, AppearancePacket::encode, AppearancePacket::decode, AppearancePacket::handle);
        dispatcher.registerMessage(nextID(), TaskFinishedPacket.class, TaskFinishedPacket::encode, TaskFinishedPacket::decode, TaskFinishedPacket::handle);
        dispatcher.registerMessage(nextID(), TaskStatusPacket.class, TaskStatusPacket::encode, TaskStatusPacket::decode, TaskStatusPacket::handle);
    }
}
