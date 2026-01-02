package de.teamlapen.vampirism.common.world.blocks;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.common.core.ModBlockEntities;
import de.teamlapen.vampirism.common.core.ModStats;
import de.teamlapen.vampirism.common.util.BloodHelper;
import de.teamlapen.vampirism.common.world.blockentity.AltarInspirationBlockEntity;
import de.teamlapen.factions.common.world.blocks.base.BaseContainerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

/**
 * Altar of inspiration used for vampire levels 1-4
 */
public class AltarInspirationBlock extends BaseContainerBlock {

    public static final MapCodec<AltarInspirationBlock> CODEC = simpleCodec(AltarInspirationBlock::new);

    private static final VoxelShape SHAPE = Stream.of(
            Block.box(4, 0, 4, 12, 1, 12),
            Block.box(12, 0, 4, 14, 12, 12),
            Block.box(2.1, 0, 2, 13.9, 12, 4),
            Block.box(2.1, 0, 12, 13.9, 12, 14),
            Block.box(2, 0, 4, 4, 12, 12)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public AltarInspirationBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AltarInspirationBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : createTickerHelper(blockEntityType, ModBlockEntities.ALTAR_INSPIRATION.get(), AltarInspirationBlockEntity::serverTick);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!player.isShiftKeyDown()) {
            if (BloodHelper.handleFluidItemBlockInteraction(stack, level, pos, player, hand, hitResult.getDirection())) {
                player.awardStat(ModStats.INTERACT_WITH_ALTAR_INSPIRATION.get());

                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof AltarInspirationBlockEntity altarBlockEntity) {
            player.awardStat(ModStats.ALTAR_OF_INSPIRATION_RITUALS_PERFORMED.get());
            altarBlockEntity.startRitual(player);

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
