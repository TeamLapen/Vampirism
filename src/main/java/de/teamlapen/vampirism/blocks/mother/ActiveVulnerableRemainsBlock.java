package de.teamlapen.vampirism.blocks.mother;

import de.teamlapen.vampirism.blockentity.VulnerableRemainsBlockEntity;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static de.teamlapen.vampirism.blocks.HorizontalContainerBlock.createTickerHelper;

public class ActiveVulnerableRemainsBlock extends RemainsBlock implements EntityBlock {

    public ActiveVulnerableRemainsBlock(Properties properties) {
        super(properties, true, true);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new VulnerableRemainsBlockEntity(pos, state);
    }

    private Optional<VulnerableRemainsBlockEntity> getBlockEntity(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof VulnerableRemainsBlockEntity) {
            return Optional.of((VulnerableRemainsBlockEntity) blockEntity);
        }
        return Optional.empty();
    }

    @Override
    public void attack(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            getBlockEntity(level, pos).ifPresent(entity -> entity.attacked(state, serverPlayer));
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, ModTiles.VULNERABLE_CURSED_ROOTED_DIRT.get(), VulnerableRemainsBlockEntity::serverTick);
    }

    @Override
    public void freeze(Level level, BlockPos pos, BlockState state) {
        level.setBlock(pos, ModBlocks.VULNERABLE_REMAINS.get().defaultBlockState(), 3);
    }
}
