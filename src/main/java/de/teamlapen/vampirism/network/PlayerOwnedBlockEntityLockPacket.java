package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.blockentity.PlayerOwnedBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record PlayerOwnedBlockEntityLockPacket(int menuId, @NotNull PlayerOwnedBlockEntity.LockDataHolder lockData)  implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "player_owned_block_entity_lock");
    public static final Codec<PlayerOwnedBlockEntityLockPacket> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    Codec.INT.fieldOf("menuId").forGetter(PlayerOwnedBlockEntityLockPacket::menuId),
                    PlayerOwnedBlockEntity.LockDataHolder.CODEC.fieldOf("lockStatus").forGetter(PlayerOwnedBlockEntityLockPacket::lockData)
            ).apply(inst, PlayerOwnedBlockEntityLockPacket::new)
    );

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeJsonWithCodec(CODEC, this);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
