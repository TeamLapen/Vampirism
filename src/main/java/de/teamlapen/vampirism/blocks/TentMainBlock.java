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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Main block for the 2x2 block tent. Handles spawning
 */
public class TentMainBlock extends TentBlock implements EntityBlock {

    public TentMainBlock() {
        super();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return new TentBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, @Nonnull BlockState p_153213_, @Nonnull BlockEntityType<T> p_153214_) {
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
