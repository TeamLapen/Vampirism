package de.teamlapen.vampirism.common.blocks;

import com.mojang.datafixers.util.Pair;
import de.teamlapen.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.common.blocks.base.BaseHorizontalBlock;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.entity.factions.FactionPlayerHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class AltarCleansingBlock extends BaseHorizontalBlock {

    private static final Pair<VoxelShape, VoxelShape> SHAPES = UtilLib.getShapesRotatedSymmetrically(Shapes.or(
            Block.box(1, 0, 5, 15, 1, 12),
            Block.box(7, 1, 7, 9, 12, 11),
            Block.box(1, 9, 3, 15, 14, 13)
    ));

    public AltarCleansingBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(FACING) == Direction.NORTH || state.getValue(FACING) == Direction.SOUTH ? SHAPES.getFirst() : SHAPES.getSecond();
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (!world.isClientSide() || !player.isAlive()) return InteractionResult.PASS;
        if (FactionPlayerHandler.get(player).isInFaction(ModFactions.VAMPIRE)) {
            VampirismMod.proxy.displayRevertBackScreen();
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
