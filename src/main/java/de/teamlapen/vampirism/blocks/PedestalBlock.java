package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.items.VampirismVampireSword;
import de.teamlapen.vampirism.tileentity.PedestalTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class PedestalBlock extends VampirismBlockContainer {

    public final static String regName = "blood_pedestal";
    private static final VoxelShape pedestalShape = makeShape();

    private static void takeItemPlayer(PlayerEntity player, Hand hand, ItemStack stack) {
        player.setHeldItem(hand, stack);
        if (stack.getItem() instanceof VampirismVampireSword) {
            if (((VampirismVampireSword) stack.getItem()).isFullyCharged(stack)) {
                ((VampirismVampireSword) stack.getItem()).tryName(stack, player);
            }
        }
    }

    public PedestalBlock() {
        super(regName, Properties.create(Material.ROCK).hardnessAndResistance(3f));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new PedestalTileEntity();
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return pedestalShape;
    }

    @Override
    public int getHarvestLevel(BlockState p_getHarvestLevel_1_) {
        return 2;
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState p_getHarvestTool_1_) {
        return ToolType.PICKAXE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (world.isRemote) return true;
        PedestalTileEntity tile = getTileEntity(world, pos);
        if (tile == null) return false;
        ItemStack stack = player.getHeldItem(hand);
        if (stack.isEmpty() && !tile.extractItem(0, 1, true).isEmpty()) {
            ItemStack stack2 = tile.extractItem(0, 1, false);
            takeItemPlayer(player, hand, stack2);
        } else if (!stack.isEmpty()) {
            ItemStack stack2 = ItemStack.EMPTY;
            if (!tile.extractItem(0, 1, true).isEmpty()) {
                stack2 = tile.extractItem(0, 1, false);
            }
            if (tile.insertItem(0, stack, false).isEmpty()) {
                if (!stack.isEmpty()) takeItemPlayer(player, hand, stack2);
            } else {
                tile.insertItem(0, stack2, false);
            }
        }
        return true;
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!world.isRemote && state.getBlock() != newState.getBlock()) {
            PedestalTileEntity tile = getTileEntity(world, pos);
            if (tile != null && tile.hasStack()) {
                net.minecraft.inventory.InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), tile.removeStack());
            }
        }
        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @Nullable
    private PedestalTileEntity getTileEntity(IBlockReader world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof PedestalTileEntity) {
            return (PedestalTileEntity) tile;
        }
        return null;
    }

    private static VoxelShape makeShape() {
        VoxelShape a = Block.makeCuboidShape(1, 0, 1, 15, 1, 15);
        VoxelShape b = Block.makeCuboidShape(2, 1, 2, 14, 2, 14);
        VoxelShape c = Block.makeCuboidShape(5, 2, 5, 11, 3, 11);
        VoxelShape d = Block.makeCuboidShape(6, 3, 6, 10, 7, 10);
        VoxelShape e = Block.makeCuboidShape(5, 7, 5, 11, 8, 11);
        VoxelShape f = Block.makeCuboidShape(3, 8, 3, 13, 9, 13);
        VoxelShape g1 = Block.makeCuboidShape(4, 9, 4, 5, 11, 5);
        VoxelShape g2 = Block.makeCuboidShape(12, 9, 4, 11, 11, 5);
        VoxelShape g3 = Block.makeCuboidShape(4, 9, 12, 5, 11, 11);
        VoxelShape g4 = Block.makeCuboidShape(12, 9, 12, 11, 11, 11);

        return VoxelShapes.or(a, b, c, d, e, f, g1, g2, g3, g4);
    }
}
