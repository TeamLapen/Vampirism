package de.teamlapen.factions.common.world.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import org.jetbrains.annotations.Nullable;

/**
 * A class for block entities that require updated data on the client.
 * IMPORTANT: By default, the whole data is sent to the client, but {@link #saveUpdate(HolderLookup.Provider)} and {@link #handleUpdateTag(ValueInput)} can be overridden.
 */
public abstract class NetworkedBlockEntity extends BlockEntity {

    public NetworkedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.level != null) {
            if (this.level.isClientSide()) {
                requestModelDataUpdate();
            } else {
                this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveUpdate(registries);
    }

    protected CompoundTag saveUpdate(HolderLookup.Provider registries) {
        return this.saveCustomOnly(registries);
    }

    @Override
    public void handleUpdateTag(ValueInput input) {
        this.loadCustomOnly(input);
    }
}
