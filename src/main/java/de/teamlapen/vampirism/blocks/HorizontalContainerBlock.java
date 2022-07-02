package de.teamlapen.vampirism.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class HorizontalContainerBlock extends VampirismHorizontalBlock implements ITileEntityProvider {

    public HorizontalContainerBlock(Properties properties, VoxelShape shape) {
        super(properties, shape);
    }

    public HorizontalContainerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean triggerEvent(@Nonnull BlockState state, @Nonnull World level, @Nonnull BlockPos pos, int p_189539_4_, int p_189539_5_) {
        super.triggerEvent(state, level, pos, p_189539_4_, p_189539_5_);
        TileEntity tileentity = level.getBlockEntity(pos);
        return tileentity != null && tileentity.triggerEvent(p_189539_4_, p_189539_5_);
    }

    @Override
    @Nullable
    public INamedContainerProvider getMenuProvider(@Nonnull BlockState state, World level, @Nonnull BlockPos pos) {
        TileEntity tileentity = level.getBlockEntity(pos);
        return tileentity instanceof INamedContainerProvider ? (INamedContainerProvider)tileentity : null;
    }
}
