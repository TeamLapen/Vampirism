package de.teamlapen.faction.common.world.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import org.jetbrains.annotations.Nullable;

public abstract class NetworkedContainerBlockEntity extends BaseContainerBlockEntity {

    protected NetworkedContainerBlockEntity(BlockEntityType<?> entityType, BlockPos pos, BlockState state) {
        super(entityType, pos, state);
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
