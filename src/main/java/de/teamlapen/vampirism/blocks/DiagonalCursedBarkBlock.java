package de.teamlapen.vampirism.blocks;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class DiagonalCursedBarkBlock extends CursedBarkBlock {

    public static final Table<Direction, Direction, BooleanProperty> PROPERTY_TABLE = HashBasedTable.create();
    private static final Map<BooleanProperty, Pair<Direction, Direction>> DIRECTION_MAP = new HashMap<>();

    public static final BooleanProperty UP_WEST = createProperty(Direction.UP, Direction.WEST);
    public static final BooleanProperty UP_NORTH = createProperty(Direction.UP, Direction.NORTH);
    public static final BooleanProperty UP_EAST = createProperty(Direction.UP, Direction.EAST);
    public static final BooleanProperty UP_SOUTH = createProperty(Direction.UP, Direction.SOUTH);
    public static final BooleanProperty DOWN_WEST = createProperty(Direction.DOWN, Direction.WEST);
    public static final BooleanProperty DOWN_NORTH = createProperty(Direction.DOWN, Direction.NORTH);
    public static final BooleanProperty DOWN_EAST = createProperty(Direction.DOWN, Direction.EAST);
    public static final BooleanProperty DOWN_SOUTH = createProperty(Direction.DOWN, Direction.SOUTH);
    public static final BooleanProperty NORTH_WEST = createProperty(Direction.NORTH, Direction.WEST);
    public static final BooleanProperty WEST_SOUTH = createProperty(Direction.WEST, Direction.SOUTH);
    public static final BooleanProperty SOUTH_EAST = createProperty(Direction.SOUTH, Direction.EAST);
    public static final BooleanProperty EAST_NORTH = createProperty(Direction.EAST, Direction.NORTH);

    public DiagonalCursedBarkBlock() {
        super(BlockBehaviour.Properties.of().sound(SoundType.EMPTY));
        this.registerDefaultState(this.defaultBlockState()
                .setValue(UP_WEST, false)
                .setValue(UP_NORTH, false)
                .setValue(UP_EAST, false)
                .setValue(UP_SOUTH, false)
                .setValue(DOWN_WEST, false)
                .setValue(DOWN_NORTH, false)
                .setValue(DOWN_EAST, false)
                .setValue(DOWN_SOUTH, false)
                .setValue(NORTH_WEST, false)
                .setValue(WEST_SOUTH, false)
                .setValue(SOUTH_EAST, false)
                .setValue(EAST_NORTH, false)
        );
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return DIRECTION_MAP.entrySet().stream().filter(entry -> state.getValue(entry.getKey())).anyMatch(e -> level.getBlockState(pos.relative(e.getValue().getKey()).relative(e.getValue().getValue())).getBlock() instanceof CursedSpruceBlock);
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState otherState, @NotNull LevelAccessor levelAccessor, @NotNull BlockPos pos, @NotNull BlockPos otherPos) {
        if (!otherState.is(ModBlocks.DIRECT_CURSED_BARK.get())) {
            for (Map.Entry<Direction, BooleanProperty> entry : PROPERTY_TABLE.column(direction).entrySet()) {
                state = state.setValue(entry.getValue(), false);
            }
            if (!anyProperty(state)) {
                state = Blocks.AIR.defaultBlockState();
            }
        } else {
            for (Map.Entry<Direction, BooleanProperty> entry : PROPERTY_TABLE.row(direction).entrySet()) {
                if (levelAccessor.getBlockState(pos.relative(entry.getKey())).is(ModBlocks.DIRECT_CURSED_BARK.get())) {
                    state.setValue(entry.getValue(), true);
                }
            }
        }

        return state;
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        if (Helper.isVampire(entity) || (entity instanceof Player && ((Player) entity).getAbilities().invulnerable)) return;
        BlockPos targetPos = pos;
        for (Map.Entry<BooleanProperty, Pair<Direction, Direction>> entry : DIRECTION_MAP.entrySet()) {
            if (state.getValue(entry.getKey())) {
                targetPos = targetPos.relative(entry.getValue().getLeft()).relative(entry.getValue().getRight());
            }
        }
        moveEntityTo(level, entity, targetPos);
    }

    private boolean anyProperty(BlockState state) {
        for (BooleanProperty property : PROPERTY_TABLE.values()) {
            if (state.getValue(property)) return true;
        }
        return false;
    }

    private static BooleanProperty createProperty(Direction direction1, Direction direction2) {
        var property = BooleanProperty.create(direction1.getSerializedName() + "_" + direction2.getSerializedName());
        PROPERTY_TABLE.put(direction1, direction2, property);
        PROPERTY_TABLE.put(direction2, direction1, property);
        DIRECTION_MAP.put(property, Pair.of(direction1, direction2));
        return property;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UP_WEST, UP_NORTH, UP_EAST, UP_SOUTH, DOWN_WEST, DOWN_NORTH, DOWN_EAST, DOWN_SOUTH, NORTH_WEST, WEST_SOUTH, SOUTH_EAST, EAST_NORTH);
    }
}
