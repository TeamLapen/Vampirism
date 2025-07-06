package de.teamlapen.lib.lib.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * A class for block entities that require interaction with metadata used on client.
 * IMPORTANT: Override {@code loadMetaData} and {@code saveMetaData} to make it save the variables that are added in {@code getModelData}.
 */
public abstract class NetworkedBlockEntity extends BlockEntity {

    public NetworkedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        loadMetaData(tag, lookupProvider);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        loadMetaData(pkt.getTag(), lookupProvider);
        setChanged();
    }

    public abstract void loadMetaData(CompoundTag tag, HolderLookup.Provider lookupProvider);

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveMetaData(tag, registries);
        return tag;
    }

    public abstract void saveMetaData(CompoundTag tag, HolderLookup.Provider registries);

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null) {
            if (level.isClientSide) {
                requestModelDataUpdate();
            } else {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
