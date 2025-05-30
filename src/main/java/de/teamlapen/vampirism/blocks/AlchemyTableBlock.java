package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.blockentity.AlchemyTableBlockEntity;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.core.ModBlockEntities;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class AlchemyTableBlock extends HorizontalContainerBlock {

    public static final BooleanProperty HAS_BOTTLE_INPUT_0 = BooleanProperty.create("has_bottle_input_0");
    public static final BooleanProperty HAS_BOTTLE_INPUT_1 = BooleanProperty.create("has_bottle_input_1");
    public static final BooleanProperty HAS_BOTTLE_OUTPUT_0 = BooleanProperty.create("has_bottle_output_0");
    public static final BooleanProperty HAS_BOTTLE_OUTPUT_1 = BooleanProperty.create("has_bottle_output_1");

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 12, 16);

    public AlchemyTableBlock(Properties properties) {
        super(properties, SHAPE);
        this.registerDefaultState(this.defaultBlockState().setValue(HAS_BOTTLE_INPUT_0, false).setValue(HAS_BOTTLE_INPUT_1, false).setValue(HAS_BOTTLE_OUTPUT_0, false).setValue(HAS_BOTTLE_OUTPUT_1, false));
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AlchemyTableBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.ALCHEMICAL_TABLE.get(), AlchemyTableBlockEntity::serverTick);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult rayTrace) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            if (Helper.isHunter(player)) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof AlchemyTableBlockEntity alchemyTableBlockEntity) {
                    player.openMenu(alchemyTableBlockEntity);
                    player.awardStat(ModStats.INTERACT_WITH_ALCHEMY_TABLE.get());
                }
            } else {
                player.displayClientMessage(Component.translatable("text.vampirism.unfamiliar"), true);
            }

            return InteractionResult.CONSUME;
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof AlchemyTableBlockEntity alchemyTableBlockEntity) {
                Containers.dropContents(level, pos, alchemyTableBlockEntity);
            }
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HAS_BOTTLE_INPUT_0).add(HAS_BOTTLE_INPUT_1).add(HAS_BOTTLE_OUTPUT_0).add(HAS_BOTTLE_OUTPUT_1);
    }
}
