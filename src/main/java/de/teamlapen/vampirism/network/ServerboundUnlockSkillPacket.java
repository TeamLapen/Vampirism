package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;


public record ServerboundUnlockSkillPacket(ResourceLocation skillId) implements CustomPacketPayload {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "unlock_skill");
    public static final Codec<ServerboundUnlockSkillPacket> CODEC = ResourceLocation.CODEC.xmap(ServerboundUnlockSkillPacket::new, msg -> msg.skillId);

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeJsonWithCodec(CODEC, this);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
