package de.teamlapen.vampirism.common.world.portal;

import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.world.blocks.PortalGatewayBlock;
import de.teamlapen.vampirism.common.world.blocks.VelmorraPortalBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.Optional;
import java.util.function.Predicate;

public class VelmorraPortalShape {

    private final Direction direction;
    private final BlockPos bottom;
    private boolean isActive;

    private VelmorraPortalShape(Direction direction, boolean isActive, BlockPos bottom) {
        this.direction = direction;
        this.isActive = isActive;
        this.bottom = bottom;
    }

    public void activate(ServerLevel level) {
        if (!this.isActive) {
            BlockState blockState = ModBlocks.VELMORRA_PORTAL.get().defaultBlockState().setValue(VelmorraPortalBlock.AXIS, this.direction.getAxis());
            executeInner(level, blockState);
            this.isActive = true;
        }
    }

    public void deactivate(ServerLevel level) {
        if (this.isActive) {
            executeInner(level, Blocks.AIR.defaultBlockState());
            this.isActive = false;
        }
    }

    private void executeInner(ServerLevel level, BlockState blockState) {
        level.setBlockAndUpdate(this.bottom.above(1).relative(this.direction, 1), blockState);
        level.setBlockAndUpdate(this.bottom.above(1).relative(this.direction, 2), blockState);
        level.setBlockAndUpdate(this.bottom.above(1).relative(this.direction, 3), blockState);
        level.setBlockAndUpdate(this.bottom.above(2).relative(this.direction, 1), blockState);
        level.setBlockAndUpdate(this.bottom.above(2).relative(this.direction, 2), blockState);
        level.setBlockAndUpdate(this.bottom.above(2).relative(this.direction, 3), blockState);
        level.setBlockAndUpdate(this.bottom.above(3).relative(this.direction, 1), blockState);
        level.setBlockAndUpdate(this.bottom.above(3).relative(this.direction, 2), blockState);
        level.setBlockAndUpdate(this.bottom.above(3).relative(this.direction, 3), blockState);
        level.setBlockAndUpdate(this.bottom.above(4).relative(this.direction, 1), blockState);
        level.setBlockAndUpdate(this.bottom.above(4).relative(this.direction, 2), blockState);
        level.setBlockAndUpdate(this.bottom.above(4).relative(this.direction, 3), blockState);
    }


    public static Optional<VelmorraPortalShape> findEmptyPortalShape(LevelAccessor level, BlockPos startPosition) {
        return findPortalShape(level, startPosition, shape -> !shape.isActive);
    }

    public static Optional<VelmorraPortalShape> findActivePortalShape(LevelAccessor level, BlockPos startPosition) {
        return findPortalShape(level, startPosition, shape -> !shape.isActive);
    }

    public static Optional<VelmorraPortalShape> findPortalShape(LevelAccessor level, BlockPos startPosition, Predicate<VelmorraPortalShape> predicate) {
        Optional<VelmorraPortalShape> optional = findAnyShape(level, startPosition).filter(predicate);
        return optional;
    }

    public static Optional<VelmorraPortalShape> findAnyShape(LevelAccessor level, BlockPos bottomLeft) {
        var pois = getStartPos(level, bottomLeft);
        if (pois.isEmpty()) {
            return Optional.empty();
        }

        BlockPos startPos;
        Direction direction;

        BlockPos second = pois.get();
        BlockState blockState = level.getBlockState(second);
        if (!blockState.hasProperty(PortalGatewayBlock.TYPE)) {
            return Optional.empty();
        } else {
            PortalGatewayBlock.Type value = blockState.getValue(PortalGatewayBlock.TYPE);
            direction = blockState.getValue(PortalGatewayBlock.FACING);
            startPos = switch (value) {
                case FIRST -> second.below(1);
                case SECOND -> second.below(2);
                case THIRD -> second.below(3);
                case FOURTH -> second.below(4);
                case FIFTH -> second.below(5).relative(direction.getCounterClockWise());
            };
        }


        direction = direction.getClockWise();

        var outer = checkOuter(level, startPos, direction);
        if (!outer) {
            return Optional.empty();
        }
        var innerCheck = checkInner(level, startPos, direction, false);

        if (!innerCheck) {
            return Optional.empty();
        }
        innerCheck = checkInner(level, startPos, direction, true);

        return Optional.of(new VelmorraPortalShape(direction, innerCheck, startPos));
    }

    private static Optional<BlockPos> getStartPos(BlockGetter level, BlockPos startPosition) {
        return BlockPos.betweenClosedStream(new AABB(startPosition).inflate(10)).filter(x -> level.getBlockState(x).getBlock() instanceof PortalGatewayBlock).findFirst();
    }

    private static boolean checkOuter(BlockGetter level, BlockPos bottomLeft, Direction direction) {
        boolean a1 = checkBlock(level, bottomLeft, ModBlocks.DARK_STONE_BRICKS.get());
        boolean a2 = checkBlock(level, bottomLeft.relative(direction), ModBlocks.DARK_STONE_BRICKS.get());
        boolean a3 = checkBlock(level, bottomLeft.relative(direction, 2), ModBlocks.DARK_STONE_BRICKS.get());
        boolean a4 = checkBlock(level, bottomLeft.relative(direction, 3), ModBlocks.DARK_STONE_BRICKS.get());
        boolean a5 = checkBlock(level, bottomLeft.relative(direction, 4), ModBlocks.DARK_STONE_BRICKS.get());
        boolean a6 = checkBlock(level, bottomLeft.above(1), PortalGatewayBlock.Type.FIRST);
        boolean a7 = checkBlock(level, bottomLeft.above(2), PortalGatewayBlock.Type.SECOND);
        boolean a8 = checkBlock(level, bottomLeft.above(3), PortalGatewayBlock.Type.THIRD);
        boolean a9 = checkBlock(level, bottomLeft.above(4), PortalGatewayBlock.Type.FOURTH);
        boolean a10 = checkBlock(level, bottomLeft.above(5).relative(direction), PortalGatewayBlock.Type.FIFTH);
        boolean a11 = checkBlock(level, bottomLeft.above(5).relative(direction, 3), PortalGatewayBlock.Type.FIFTH);
        boolean a12 = checkBlock(level, bottomLeft.above(4).relative(direction, 4), PortalGatewayBlock.Type.FOURTH);
        boolean a13 = checkBlock(level, bottomLeft.above(3).relative(direction, 4), PortalGatewayBlock.Type.THIRD);
        boolean a14 = checkBlock(level, bottomLeft.above(2).relative(direction, 4), PortalGatewayBlock.Type.SECOND);
        boolean a15 = checkBlock(level, bottomLeft.above(1).relative(direction, 4), PortalGatewayBlock.Type.FIRST);

        return a1 && a2 && a3 && a4 && a5 && a6 && a7 && a8 && a9 && a10 && a11 && a12 && a13 && a14 && a15;
    }

    private static boolean checkInner(BlockGetter level, BlockPos bottomLeft, Direction direction, boolean active) {
        Block[] blocksToCheck = active ? new Block[]{ModBlocks.VELMORRA_PORTAL.get()} : new Block[]{ModBlocks.VELMORRA_PORTAL.get(), Blocks.AIR};
        boolean a1 = checkBlock(level, bottomLeft.above(1).relative(direction, 1), blocksToCheck);
        boolean a2 = checkBlock(level, bottomLeft.above(1).relative(direction, 2), blocksToCheck);
        boolean a3 = checkBlock(level, bottomLeft.above(1).relative(direction, 3), blocksToCheck);
        boolean a4 = checkBlock(level, bottomLeft.above(2).relative(direction, 1), blocksToCheck);
        boolean a5 = checkBlock(level, bottomLeft.above(2).relative(direction, 2), blocksToCheck);
        boolean a6 = checkBlock(level, bottomLeft.above(2).relative(direction, 3), blocksToCheck);
        boolean a7 = checkBlock(level, bottomLeft.above(3).relative(direction, 1), blocksToCheck);
        boolean a8 = checkBlock(level, bottomLeft.above(3).relative(direction, 2), blocksToCheck);
        boolean a9 = checkBlock(level, bottomLeft.above(3).relative(direction, 3), blocksToCheck);
        boolean a10 = checkBlock(level, bottomLeft.above(4).relative(direction, 1), blocksToCheck);
        boolean a11 = checkBlock(level, bottomLeft.above(4).relative(direction, 2), blocksToCheck);
        boolean a12 = checkBlock(level, bottomLeft.above(4).relative(direction, 3), blocksToCheck);

        return a1 && a2 && a3 && a4 && a5 && a6 && a7 && a8 && a9 && a10 && a11 && a12;
    }

    private static boolean checkBlock(BlockGetter level, BlockPos pos, Block... block) {
        BlockState blockState = level.getBlockState(pos);
        for (Block blocks : block) {
            if (blockState.is(blocks)) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkBlock(BlockGetter level, BlockPos pos, PortalGatewayBlock.Type type) {
        BlockState blockState = level.getBlockState(pos);
        return blockState.is(ModBlocks.VELMORRA_PORTAL_ARCH) && blockState.getValue(PortalGatewayBlock.TYPE) == type;
    }

}
