package de.teamlapen.vampirism.blocks;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;

public class DirectCursedBarkBlock extends CursedBarkBlock {

    public static final EnumProperty<Type> UP_TYPE = EnumProperty.create("up_type", Type.class);
    public static final EnumProperty<Type> DOWN_TYPE = EnumProperty.create("down_type", Type.class);
    public static final EnumProperty<Type> NORTH_TYPE = EnumProperty.create("north_type", Type.class);
    public static final EnumProperty<Type> SOUTH_TYPE = EnumProperty.create("south_type", Type.class);
    public static final EnumProperty<Type> WEST_TYPE = EnumProperty.create("west_type", Type.class);
    public static final EnumProperty<Type> EAST_TYPE = EnumProperty.create("east_type", Type.class);
    public static final BiMap<Direction, EnumProperty<Type>> SIDE_MAP = ImmutableBiMap.<Direction, EnumProperty<Type>>builder().put(Direction.UP, UP_TYPE).put(Direction.DOWN, DOWN_TYPE).put(Direction.EAST, EAST_TYPE).put(Direction.WEST, WEST_TYPE).put(Direction.NORTH, NORTH_TYPE).put(Direction.SOUTH, SOUTH_TYPE).build();

    public DirectCursedBarkBlock() {
        super(BlockBehaviour.Properties.of().sound(SoundType.WOOD));
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(UP_TYPE, Type.NONE)
                .setValue(DOWN_TYPE, Type.NONE)
                .setValue(EAST_TYPE, Type.NONE)
                .setValue(WEST_TYPE, Type.NONE)
                .setValue(NORTH_TYPE, Type.NONE)
                .setValue(SOUTH_TYPE, Type.NONE)
        );
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        if (Helper.isVampire(entity) || (entity instanceof Player && ((Player) entity).getAbilities().invulnerable)) return;
        BlockPos targetPos = pos;
        for (Map.Entry<Direction, EnumProperty<Type>> entry : SIDE_MAP.entrySet()) {
            if (state.getValue(entry.getValue()) != Type.NONE) {
                targetPos = targetPos.relative(entry.getKey());
            }
        }
        moveEntityTo(level, entity, targetPos);
    }

    private boolean canAttachTo(@NotNull BlockGetter blockReader, @NotNull BlockPos pos, @NotNull Direction direction) {
        BlockState blockstate = blockReader.getBlockState(pos);
        return blockstate.getBlock() instanceof CursedSpruceBlock cursedSpruce && !cursedSpruce.isCured();
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader worldReader, @NotNull BlockPos blockPos) {
        for (Map.Entry<Direction, EnumProperty<Type>> entry : SIDE_MAP.entrySet()) {
            if (state.getValue(entry.getValue()) != Type.NONE && this.canAttachTo(worldReader, blockPos.relative(entry.getKey()), entry.getKey().getOpposite())) {
                return true;
            }
        }
        return false;
    }

    @NotNull
    @Override
    public BlockState updateShape(@NotNull BlockState blockState, @NotNull Direction direction, @NotNull BlockState otherState, @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos otherPos) {
        EnumProperty<Type> property = SIDE_MAP.get(direction);
        if (blockState.getValue(property) != Type.NONE) {
            if (!canAttachTo(level, otherPos, direction.getOpposite())) {
                BlockState state = blockState.setValue(property, Type.NONE);
                if (!anySideAvailable(state)) {
                    state = Blocks.AIR.defaultBlockState();
                }
                return state;
            }
        } else if (!anySideAvailable(blockState)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(blockState, direction, otherState, level, pos, otherPos);
    }

    private static boolean anySideAvailable(BlockState state) {
        return SIDE_MAP.values().stream().anyMatch(property -> state.getValue(property) != Type.NONE);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        Direction direction = context.getNearestLookingDirection();
        return this.defaultBlockState().setValue(SIDE_MAP.get(direction), Type.VERTICAL);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        builder.add(SIDE_MAP.values().toArray(new Property[0]));
    }

    public enum Type implements StringRepresentable {
        VERTICAL, HORIZONTAL, NONE;

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
