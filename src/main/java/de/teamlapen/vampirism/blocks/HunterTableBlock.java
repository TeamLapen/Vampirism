package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.inventory.container.HunterTableContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Table for hunter "education/leveling"
 * TODO create a unique texture for the top side
 */
public class HunterTableBlock extends VampirismBlock {
    public static final String name = "hunter_table";
    public static final ITextComponent containerName = new TranslationTextComponent("container.hunter_table");
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    private static final VoxelShape shape = makeShape();//TODO 1.14 make shape for all directions

    @Nullable
    @Override
    public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
        return new SimpleNamedContainerProvider((id, playerInventory, playerEntity) -> {
            return new HunterTableContainer(id, playerInventory, IWorldPosCallable.of(worldIn, pos));
        }, containerName);
    }

    public HunterTableBlock() {
        super(name, Properties.create(Material.WOOD).hardnessAndResistance(0.5f));
        this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.NORTH));
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing());
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        player.openContainer(state.getContainer(world, pos));
        return true;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return shape;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    private static VoxelShape makeShape() {
        VoxelShape a = Block.makeCuboidShape(0, 0, 0, 2, 10, 2);
        VoxelShape b = Block.makeCuboidShape(14, 0, 0, 16, 10, 2);
        VoxelShape c = Block.makeCuboidShape(0, 0, 14, 2, 10, 16);
        VoxelShape d = Block.makeCuboidShape(14, 0, 14, 16, 10, 16);

        VoxelShape e = Block.makeCuboidShape(1, 8, 1, 15, 10, 15);
        VoxelShape f = Block.makeCuboidShape(8.5, 10, 3.5, 13.5, 11, 10);

        VoxelShape d1 = VoxelShapes.or(a, b);
        VoxelShape d2 = VoxelShapes.or(c, d);

        VoxelShape d3 = VoxelShapes.or(d1, d2);
        VoxelShape f1 = VoxelShapes.or(e, f);

        VoxelShape g = VoxelShapes.or(d3, f1);
        return g;
    }
}
