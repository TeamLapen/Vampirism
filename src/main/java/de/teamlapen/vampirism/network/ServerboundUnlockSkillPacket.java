package de.teamlapen.vampirism.network;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;


public record ServerboundUnlockSkillPacket(ResourceLocation skillId) implements CustomPacketPayload {
    public static final Type<ServerboundUnlockSkillPacket> TYPE = new Type<>(VResourceLocation.mod("unlock_skill"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundUnlockSkillPacket> CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, ServerboundUnlockSkillPacket::skillId,
            ServerboundUnlockSkillPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
