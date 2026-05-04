package de.teamlapen.vampirism.common.world.blocks;

import de.teamlapen.vampirism.common.core.ModBlockEntities;
import de.teamlapen.vampirism.common.core.ModStats;
import de.teamlapen.vampirism.common.world.blockentity.VaporStillBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class VaporStillBlock extends HorizontalContainerBlock {

    protected static final VoxelShape SHAPE = Stream.of(
            Block.box(9, 0, 1, 14, 6, 6),
            Block.box(2, 0, 3, 6, 4, 7),
            Block.box(1, 0, 9, 5, 6, 13),
            Block.box(2, 6, 10, 4, 9, 12),
            Block.box(8, 0, 8, 14, 13, 14),
            Block.box(9, 13, 9, 13, 16, 13)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public VaporStillBlock(Properties properties) {
        super(properties, SHAPE);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VaporStillBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        BlockEntity tile = level.getBlockEntity(pos);
        if (tile instanceof VaporStillBlockEntity potionTableBlockEntity && placer instanceof Player player) {
            potionTableBlockEntity.setOwnerID(player);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && player instanceof ServerPlayer) {
            BlockEntity tile = level.getBlockEntity(pos);
            if (tile instanceof VaporStillBlockEntity potionTable) {
                if (potionTable.canOpen(player)) {
                    player.openMenu(potionTable, buffer -> buffer.writeBoolean(potionTable.isExtended()));
                    player.awardStat(ModStats.INTERACT_WITH_VAPOR_STILL.get());
                }
            }
        }

        return InteractionResult.SUCCESS_SERVER;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.VAPOR_STILL.get(), VaporStillBlockEntity::tick);
    }
}
