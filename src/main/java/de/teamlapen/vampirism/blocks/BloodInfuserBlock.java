package de.teamlapen.vampirism.blocks;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.blockentity.InfuserBlockEntity;
import de.teamlapen.vampirism.core.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class BloodInfuserBlock extends BaseEntityBlock {

    public static final MapCodec<BloodInfuserBlock> CODEC = simpleCodec(BloodInfuserBlock::new);

    public static final BooleanProperty IS_ACTIVE = BooleanProperty.create("active");

    public static final VoxelShape SHAPE = Stream.of(Block.box(0, 0.3125, 0.8125, 0.1875, 0.875, 1), Block.box(0, 0, 0, 1, 0.0625, 1), Shapes.box(0.0625, 0.0625, 0.0625, 0.9375, 0.125, 0.9375), Shapes.box(0, 0.125, 0, 1, 0.1875, 1), Shapes.box(0.0625, 0.1875, 0.0625, 0.9375, 0.25, 0.9375), Shapes.box(0, 0.25, 0, 1, 0.3125, 1), Shapes.box(0, 0.3125, 0.1875, 0.125, 0.5625, 0.8125), Shapes.box(0.1875, 0.3125, 0, 0.8125, 0.5625, 0.125), Shapes.box(0.875, 0.3125, 0.1875, 1, 0.5625, 0.8125), Shapes.box(0.1875, 0.3125, 0.875, 0.8125, 0.5625, 1), Shapes.box(0, 0.3125, 0, 0.1875, 0.875, 0.1875), Shapes.box(0.8125, 0.3125, 0, 1, 0.875, 0.1875), Shapes.box(0.8125, 0.3125, 0.8125, 1, 0.875, 1)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElseGet(Shapes::empty);

    public BloodInfuserBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(IS_ACTIVE, false));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new InfuserBlockEntity(pos, state);
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type){
        return level.isClientSide() ? null : createTickerHelper(type, ModTiles.INFUSER.get(), InfuserBlockEntity::serverTick);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            openContainer(level, pos, player);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    protected void openContainer(Level level, BlockPos pos, Player player) {
        level.getBlockEntity(pos, ModTiles.INFUSER.get()).ifPresent(player::openMenu);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(IS_ACTIVE);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
