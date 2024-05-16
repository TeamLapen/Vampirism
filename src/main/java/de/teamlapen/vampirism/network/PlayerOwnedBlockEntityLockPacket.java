package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.blockentity.PlayerOwnedBlockEntity;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record PlayerOwnedBlockEntityLockPacket(int menuId, @NotNull PlayerOwnedBlockEntity.LockDataHolder lockData)  implements CustomPacketPayload {
    public static final Type<PlayerOwnedBlockEntityLockPacket> TYPE = new Type<>(new ResourceLocation(REFERENCE.MODID, "player_owned_block_entity_lock"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerOwnedBlockEntityLockPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PlayerOwnedBlockEntityLockPacket::menuId,
            PlayerOwnedBlockEntity.LockDataHolder.STREAM_CODEC, PlayerOwnedBlockEntityLockPacket::lockData,
            PlayerOwnedBlockEntityLockPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
