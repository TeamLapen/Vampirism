package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Placed in some churches
 */
public class AltarCleansingBlock extends VampirismHorizontalBlock {
    private static final VoxelShape SHAPEX = makeShape();
    private static final VoxelShape SHAPEZ = UtilLib.rotateShape(SHAPEX, UtilLib.RotationAmount.NINETY);

    private static VoxelShape makeShape() {
        VoxelShape a = Block.box(1, 0, 5, 15, 1, 12);
        VoxelShape b = Block.box(7, 1, 7, 9, 12, 11);
        VoxelShape c = Block.box(1, 9, 3, 15, 14, 13);
        VoxelShape r = Shapes.or(a, b);
        return Shapes.or(r, c);
    }


    public AltarCleansingBlock() {
        super(Properties.of(Material.WOOD).strength(0.5f).noOcclusion());
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }


    @NotNull
    @Override
    public VoxelShape getShape(BlockState blockState, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        Direction dir = blockState.getValue(FACING);
        if (dir == Direction.NORTH || dir == Direction.SOUTH) return SHAPEX;
        return SHAPEZ;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @NotNull
    @Override
    public String getDescriptionId() {
        return "block.vampirism.church_altar";
    }


    @NotNull
    @Override
    public InteractionResult use(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (!player.isAlive()) return InteractionResult.PASS;
        return FactionPlayerHandler.getOpt(player).map(handler -> {
            if (handler.isInFaction(VReference.VAMPIRE_FACTION)) {
                VampirismMod.proxy.displayRevertBackScreen();
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }).orElse(InteractionResult.PASS);
    }
}
