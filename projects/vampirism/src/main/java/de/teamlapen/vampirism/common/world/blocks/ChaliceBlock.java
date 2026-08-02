package de.teamlapen.vampirism.common.world.blocks;

import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.world.entity.dracula.DraculaFightData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ChaliceBlock extends Block {

    public static final BooleanProperty FILLED = BooleanProperty.create("filled");

    private static final VoxelShape SHAPE = Shapes.or(
            Shapes.box(0.3125, 0, 0.3125, 0.6875, 0.0625, 0.6875),
            Shapes.box(0.375, 0.03125, 0.375, 0.625, 0.09375, 0.625),
            Shapes.box(0.45, 0.09375, 0.45, 0.55, 0.34375, 0.55),
            Shapes.box(0.375, 0.34375, 0.375, 0.625, 0.40625, 0.625),
            Shapes.box(0.375, 0.40625, 0.3125, 0.625, 0.71875, 0.375),
            Shapes.box(0.375, 0.40625, 0.625, 0.625, 0.71875, 0.6875),
            Shapes.box(0.3125, 0.40625, 0.375, 0.375, 0.71875, 0.625),
            Shapes.box(0.625, 0.40625, 0.375, 0.6875, 0.71875, 0.625),
            Shapes.box(0.625, 0.71875, 0.3125, 0.6875, 0.78125, 0.6875),
            Shapes.box(0.3125, 0.71875, 0.3125, 0.375, 0.78125, 0.6875),
            Shapes.box(0.3875, 0.71875, 0.3125, 0.6125, 0.78125, 0.375),
            Shapes.box(0.3875, 0.71875, 0.625, 0.6125, 0.78125, 0.6875),
            Shapes.box(0.68875, 0.40625, 0.375, 0.68875, 0.7125, 0.625),
            Shapes.box(0.375, 0.40625, 0.31125, 0.625, 0.7125, 0.31125),
            Shapes.box(0.375, 0.40625, 0.68875, 0.625, 0.7125, 0.68875),
            Shapes.box(0.31125, 0.40625, 0.375, 0.31125, 0.7125, 0.625),
            Shapes.box(0.375, 0.4375, 0.375, 0.625, 0.75, 0.625)
            );

    public ChaliceBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FILLED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FILLED);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!state.getValue(FILLED) && itemStack.is(ModItems.VAMPIRE_BLOOD_BOTTLE)) {
            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }
            level.setBlock(pos, state.setValue(FILLED, true), UPDATE_ALL);
            DraculaFightData.getOpt(level).ifPresent(x -> x.registerChalice(pos));
            return InteractionResult.SUCCESS_SERVER;
        }
        return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        super.destroy(level, pos, state);
        DraculaFightData.getOpt(level).ifPresent(x -> x.registerChalice(pos));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
