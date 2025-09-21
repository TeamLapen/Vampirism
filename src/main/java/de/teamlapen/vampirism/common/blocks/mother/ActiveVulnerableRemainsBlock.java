package de.teamlapen.vampirism.common.blocks.mother;

import de.teamlapen.vampirism.common.blockentity.VulnerableRemainsBlockEntity;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static de.teamlapen.vampirism.common.blocks.HorizontalContainerBlock.createTickerHelper;

public class ActiveVulnerableRemainsBlock extends RemainsBlock implements EntityBlock {

    public ActiveVulnerableRemainsBlock(Properties properties) {
        super(properties, true, true);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VulnerableRemainsBlockEntity(pos, state);
    }

    private Optional<VulnerableRemainsBlockEntity> getBlockEntity(LevelReader level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof VulnerableRemainsBlockEntity) {
            return Optional.of((VulnerableRemainsBlockEntity) blockEntity);
        }
        return Optional.empty();
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, ModBlockEntities.VULNERABLE_CURSED_ROOTED_DIRT.get(), (level1, pos, state1, entity) -> VulnerableRemainsBlockEntity.serverTick((ServerLevel) level1, pos, state1, entity));
    }

    @Override
    public void freeze(Level level, BlockPos pos, BlockState state) {
        level.setBlock(pos, ModBlocks.VULNERABLE_REMAINS.get().defaultBlockState(), 3);
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(state, level, pos, neighbor);
        getBlockEntity(level, pos).ifPresent(x -> x.checkNeighbor(neighbor));
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
        getBlockEntity(pLevel, pPos).ifPresent(VulnerableRemainsBlockEntity::onPlaced);
    }
}
