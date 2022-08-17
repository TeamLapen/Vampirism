package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.blockentity.TentBlockEntity;
import de.teamlapen.vampirism.core.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Main block for the 2x2 block tent. Handles spawning
 */
public class TentMainBlock extends TentBlock implements EntityBlock {

    public TentMainBlock() {
        super();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TentBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, @NotNull BlockState p_153213_, @NotNull BlockEntityType<T> p_153214_) {
        return p_153212_.isClientSide() ? null : createTickerHelper(p_153214_, TentBlockEntity::serverTick);
    }

    /**
     * Need this to convince generics. Copy from {@link net.minecraft.world.level.block.BaseEntityBlock}
     */
    @SuppressWarnings("unchecked")
    @Nullable
    private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> type1, BlockEntityTicker<? super E> ticker) {
        return ModTiles.TENT.get() == type1 ? (BlockEntityTicker<A>) ticker : null;
    }
}
