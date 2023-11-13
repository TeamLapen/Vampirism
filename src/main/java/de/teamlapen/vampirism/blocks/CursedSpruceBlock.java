package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.api.blocks.HolyWaterEffectConsumer;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CursedSpruceBlock extends StrippableLogBlock implements HolyWaterEffectConsumer {

    private final Supplier<? extends CursedSpruceBlock> curedBlockSupplier;

    public CursedSpruceBlock(@NotNull Supplier<? extends LogBlock> strippedBlock, Supplier<? extends CursedSpruceBlock> curedBlockSupplier) {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.CRIMSON_HYPHAE).strength(2.0F).sound(SoundType.WOOD).randomTicks().ignitedByLava(), strippedBlock);
        this.curedBlockSupplier = curedBlockSupplier;
    }

    public CursedSpruceBlock(@NotNull Supplier<? extends LogBlock> strippedBlock) {
        this(strippedBlock, null);
    }

    public boolean isCured() {
        return this.curedBlockSupplier == null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onHolyWaterEffect(Level level, BlockState state, BlockPos pos, ItemStack holyWaterStack, IItemWithTier.TIER tier) {
        if (this.curedBlockSupplier != null) {
            BlockState newState = this.curedBlockSupplier.get().defaultBlockState();
            state.getValues().keySet().forEach((@SuppressWarnings("rawtypes") Property property) -> {
                newState.setValue(property, state.getValue(property));
            });
            level.setBlockAndUpdate(pos, newState);
        }
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        DirectCursedBarkBlock.Type type = null;
        List<Direction> directions = Arrays.stream(Direction.values()).collect(Collectors.toList());
        Direction direction = null;
        if (state.getBlock() == ModBlocks.CURSED_SPRUCE_LOG.get()) {
            switch (state.getValue(RotatedPillarBlock.AXIS)) {
                case X -> {
                    directions.remove(Direction.WEST);
                    directions.remove(Direction.EAST);
                    direction = directions.get(random.nextInt(directions.size()));
                    type = DirectCursedBarkBlock.Type.HORIZONTAL;
                }
                case Y -> {
                    directions.remove(Direction.UP);
                    directions.remove(Direction.DOWN);
                    direction = directions.get(random.nextInt(directions.size()));
                    type = DirectCursedBarkBlock.Type.VERTICAL;
                }
                case Z -> {
                    directions.remove(Direction.NORTH);
                    directions.remove(Direction.SOUTH);
                    direction = directions.get(random.nextInt(directions.size()));
                    if (direction.getAxis() == Direction.Axis.X) {
                        type = DirectCursedBarkBlock.Type.HORIZONTAL;
                    } else {
                        type = DirectCursedBarkBlock.Type.VERTICAL;
                    }
                }
            }
        } else if(state.getBlock() == ModBlocks.CURSED_SPRUCE_WOOD.get()) {
            direction = directions.get(random.nextInt(directions.size()));
            switch (state.getValue(RotatedPillarBlock.AXIS)) {
                case X -> {
                    if (direction.getAxis() == Direction.Axis.X) {
                        type = DirectCursedBarkBlock.Type.VERTICAL;
                    } else {
                        type = DirectCursedBarkBlock.Type.HORIZONTAL;
                    }
                }
                case Y -> type = DirectCursedBarkBlock.Type.VERTICAL;
                case Z -> {
                    if (direction.getAxis() == Direction.Axis.X) {
                        type = DirectCursedBarkBlock.Type.HORIZONTAL;
                    } else {
                        type = DirectCursedBarkBlock.Type.VERTICAL;
                    }
                }
            }
        } else {
            return;
        }
        BlockPos pos1 = pos.relative(direction);

        BlockState state1 = level.getBlockState(pos1);
        if (state1.isAir() || state1.is(ModBlocks.DIAGONAL_CURSED_BARK.get())) {
            state1 = ModBlocks.DIRECT_CURSED_BARK.get().defaultBlockState();
            level.setBlockAndUpdate(pos1, state1.setValue(DirectCursedBarkBlock.SIDE_MAP.get(direction.getOpposite()), type));
        } else if(state1.is(ModBlocks.DIRECT_CURSED_BARK.get())) {

        } else {
            return;
        }
        if (state1.is(ModBlocks.DIRECT_CURSED_BARK.get())) {
            directions = Arrays.stream(Direction.values()).collect(Collectors.toList());
            directions.remove(direction);
            directions.remove(direction.getOpposite());
            for (Direction direction1 : directions) {
                BlockState state2 = level.getBlockState(pos.relative(direction1));
                if (state2.is(ModBlocks.DIRECT_CURSED_BARK.get())) {
                    if (state2.getValue(DirectCursedBarkBlock.SIDE_MAP.get(direction1.getOpposite())) != DirectCursedBarkBlock.Type.NONE) {
                        BlockState diagonalState = level.getBlockState(pos1.relative(direction1));
                        if (diagonalState.isAir()) {
                            diagonalState = ModBlocks.DIAGONAL_CURSED_BARK.get().defaultBlockState();
                        }
                        if (diagonalState.is(ModBlocks.DIAGONAL_CURSED_BARK.get())) {
                            level.setBlockAndUpdate(pos1.relative(direction1), diagonalState.setValue(DiagonalCursedBarkBlock.PROPERTY_TABLE.get(direction.getOpposite(), direction1.getOpposite()), true));
                        }
                    }
                }
            }
        }
    }
}
