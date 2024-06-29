package de.teamlapen.vampirism.network;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.blockentity.PlayerOwnedBlockEntity;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record PlayerOwnedBlockEntityLockPacket(int menuId, @NotNull PlayerOwnedBlockEntity.LockDataHolder lockData) implements CustomPacketPayload {
    public static final Type<PlayerOwnedBlockEntityLockPacket> TYPE = new Type<>(VResourceLocation.mod("player_owned_block_entity_lock"));
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
