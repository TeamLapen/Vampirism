package de.teamlapen.vampirism.common.world.blocks;

import de.teamlapen.faction.common.util.ShapeUtil;
import de.teamlapen.faction.common.world.blocks.base.BaseHorizontalBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Util;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Locale;
import java.util.Map;

public class PortalGatewayBlock extends BaseHorizontalBlock {

    private static final VoxelShape FIRST_SHAPE = Shapes.or(
            Shapes.box(0.625, 0.3125, 0, 0.9375, 1, 0.125),
            Shapes.box(0.625, 0.3125, 0.875, 0.9375, 1, 1),
            Shapes.box(0.0625, 0.3125, 0.0625, 1, 0.5, 0.9375),
            Shapes.box(0, 0, 0, 1, 0.3125, 1),
            Shapes.box(0.125, 0.5, 0.125, 1, 1, 0.875)
    );
    private static final VoxelShape SECOND_SHAPE = Shapes.or(
            Shapes.box(0.125, 0, 0.125, 1, 1, 0.875),
            Shapes.box(0.625, 0, 0, 0.9375, 1, 0.125),
            Shapes.box(0.625, 0, 0.875, 0.9375, 1, 1),
            Shapes.box(0.125, 0.375, 0, 0.625, 0.6875, 0.125),
            Shapes.box(0.125, 0.375, 0.875, 0.625, 0.6875, 1),
            Shapes.box(0, 0.375, 0, 0.125, 0.6875, 1)
    );
    private static final VoxelShape THIRD_SHAPE = Shapes.or(
            Shapes.box(0.1875, 0, 0.1875, 1, 0.75, 0.8125),
            Shapes.box(0.0625, 0.75, 0.0625, 1, 1, 0.9375),
            Shapes.box(0.5, 0, 0, 1, 0.25, 0.1875),
            Shapes.box(0.5, 0, 0.8125, 1, 0.25, 1),
            Shapes.box(0.75, 0.25, 0.8125, 1, 0.5625, 1),
            Shapes.box(0.75, 0.25, 0, 1, 0.5625, 0.1875),
            Shapes.box(0.8125, 0.5625, 0.8125, 1.0625, 0.875, 1),
            Shapes.box(0.8125, 0.5625, 0, 1.0625, 0.875, 0.1875),
            Shapes.box(0.875, 0.875, 0.8125, 1.125, 1, 1),
            Shapes.box(0.875, 0.875, 0, 1.125, 1, 0.1875),
            Shapes.box(1, 0.5, 0.1875, 1.125, 1, 0.8125),
            Shapes.box(1.125, 0.6875, 0.1875, 1.25, 1, 0.8125),
            Shapes.box(1.25, 0.875, 0.1875, 1.375, 1, 0.8125)
    );
    private static final VoxelShape FORTH_SHAPE = Shapes.or(
            Shapes.box(0.1875, 0, 0.1875, 0.9375, 0.25, 0.8125),
            Shapes.box(0.3125, 0.25, 0.3125, 0.8125, 0.5, 0.6875),
            Shapes.box(0.625, 0.5, 0.3125, 0.8125, 0.6875, 0.6875),
            Shapes.box(0.9375, 0, 0.3125, 1, 1, 0.6875),
            Shapes.box(1, 0, 0, 1.125, 0.1875, 1),
            Shapes.box(1, 0.1875, 0, 1.1875, 0.5, 1),
            Shapes.box(1.125, 0, 0.1875, 1.25, 0.5, 0.8125),
            Shapes.box(1, 0.5, 0, 1.25, 0.8125, 1),
            Shapes.box(1.1875, 0.8125, 0, 1.4375, 1, 1),
            Shapes.box(1.25, 0, 0.1875, 1.375, 0.8125, 0.8125),

            Shapes.box(1.375, 0.0625, 0.1875, 1.5, 1, 0.8125),
            Shapes.box(1, 0.8125, 0.3125, 1.1875, 1, 0.6875),
            Shapes.box(1.5, 0.25, 0.1875, 1.625, 1, 0.8125),
            Shapes.box(1.625, 0.375, 0.1875, 1.75, 1, 0.8125),
            Shapes.box(1.75, 0.5, 0.1875, 1.875, 1, 0.8125),
            Shapes.box(1.875, 0.625, 0.1875, 2, 1, 0.8125),
            Shapes.box(0.875, 0, 0, 1, 0.1875, 1),
            Shapes.box(0.8125, 0, 0.3125, 0.9375, 1, 0.6875),
            Shapes.box(0.9375, 0.1875, 0, 1, 0.5, 1)
    );
    private static final VoxelShape FIFTH_SHAPE = Shapes.or(
            Shapes.box(0.375, 1, 0.3125, 0.5625, 1.0625, 0.6875),
            Shapes.box(0.5625, 1, 0.3125, 0.75, 1.375, 0.6875),
            Shapes.box(0.75, 1, 0.3125, 0.9375, 1.6875, 0.6875),
            Shapes.box(0.9375, 1, 0.3125, 1.125, 2, 0.6875),
            Shapes.box(0.75, 1, 0, 1, 1.0625, 1),
            Shapes.box(0.9375, 1.0625, 0, 1.1875, 1.375, 1),
            Shapes.box(1.125, 1.375, 0, 1.375, 1.6875, 1),
            Shapes.box(1.375, 1.375, 0, 1.5, 1.6875, 1),
            Shapes.box(1, 1, 0.1875, 1.125, 1.0625, 0.8125),
            Shapes.box(1.125, 1, 0.1875, 1.25, 1.375, 0.8125),
            Shapes.box(1.25, 1, 0.1875, 1.375, 1.375, 0.8125),
            Shapes.box(1.375, 1, 0.1875, 1.5, 1.375, 0.8125),
            Shapes.box(0.9375, 1, 0.3125, 1.125, 2, 0.6875),
            Shapes.box(0.375, 0, 0.1875, 0.5, 0.125, 0.8125),
            Shapes.box(0.5, 0, 0.1875, 0.625, 0.125, 0.8125),
            Shapes.box(0.1875, 0, 0, 0.4375, 0.125, 1),
            Shapes.box(0, 0, 0.3125, 0.1875, 0.4375, 0.6875),
            Shapes.box(0.1875, 0.125, 0.3125, 0.375, 0.75, 0.6875),
            Shapes.box(0.375, 0.125, 0, 0.625, 0.4375, 1),
            Shapes.box(0.625, 0, 0.1875, 0.75, 0.4375, 0.8125),
            Shapes.box(0.75, 0, 0.1875, 0.875, 0.75, 0.8125),
            Shapes.box(0.875, 0, 0.1875, 1, 0.75, 0.8125),
            Shapes.box(0.5625, 0.4375, 0, 0.8125, 0.75, 1),
            Shapes.box(1.375, -0.25, 0.1875, 1.5, 1, 0.8125),
            Shapes.box(1.25, -0.25, 0.1875, 1.375, 1, 0.8125),
            Shapes.box(1.125, -0.25, 0.1875, 1.25, 1, 0.8125),
            Shapes.box(1, -0.3125, 0.1875, 1.125, 1, 0.8125),
            Shapes.box(0.75, 0.75, 0, 1, 1, 1),
            Shapes.box(0.5625, 0.75, 0.3125, 0.75, 1, 0.6875),
            Shapes.box(0.375, 0.4375, 0.3125, 0.5625, 1, 0.6875),
            Shapes.box(1.125, 1.6875, 0.3125, 1.3125, 2, 0.6875),
            Shapes.box(1.3125, 1.6875, 0.3125, 1.5, 2, 0.6875));

    private static final Map<Direction, VoxelShape> FIRST_SHAPES = ShapeUtil.getShapesRotatedFromNorth(FIRST_SHAPE);
    private static final Map<Direction, VoxelShape> SECOND_SHAPES = ShapeUtil.getShapesRotatedFromNorth(SECOND_SHAPE);
    private static final Map<Direction, VoxelShape> THIRD_SHAPES = ShapeUtil.getShapesRotatedFromNorth(THIRD_SHAPE);
    private static final Map<Direction, VoxelShape> FORTH_SHAPES = ShapeUtil.getShapesRotatedFromNorth(FORTH_SHAPE);
    private static final Map<Direction, VoxelShape> FIFTH_SHAPES = ShapeUtil.getShapesRotatedFromNorth(FIFTH_SHAPE);

    public static final EnumProperty<Type> TYPE = EnumProperty.create("portal_arch_type", Type.class);

    public static final Map<Type, Map<Direction,VoxelShape>> SHAPES = Util.makeEnumMap(Type.class, type -> switch (type) {
        case FIRST -> FIRST_SHAPES;
        case SECOND -> SECOND_SHAPES;
        case THIRD -> THIRD_SHAPES;
        case FOURTH -> FORTH_SHAPES;
        case FIFTH -> FIFTH_SHAPES;
    });

    public PortalGatewayBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(TYPE, Type.FIRST));
    }

//    @Override
//    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
//        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
//    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(TYPE)).get(state.getValue(FACING));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(TYPE);
    }

    public enum Type implements StringRepresentable {
        FIRST,
        SECOND,
        THIRD,
        FOURTH,
        FIFTH,
        ;

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
