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
import net.minecraft.util.ActionResultType;
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
    private static final VoxelShape pedestalShape = makeShape();

    private static void takeItemPlayer(PlayerEntity player, Hand hand, ItemStack stack) {
        player.setItemInHand(hand, stack);
        if (stack.getItem() instanceof VampirismVampireSword) {
            if (((VampirismVampireSword) stack.getItem()).isFullyCharged(stack)) {
                ((VampirismVampireSword) stack.getItem()).tryName(stack, player);
            }
        }
    }

    private static VoxelShape makeShape() {
        VoxelShape a = Block.box(1, 0, 1, 15, 1, 15);
        VoxelShape b = Block.box(2, 1, 2, 14, 2, 14);
        VoxelShape c = Block.box(5, 2, 5, 11, 3, 11);
        VoxelShape d = Block.box(6, 3, 6, 10, 7, 10);
        VoxelShape e = Block.box(5, 7, 5, 11, 8, 11);
        VoxelShape f = Block.box(3, 8, 3, 13, 9, 13);
        VoxelShape g1 = Block.box(4, 9, 4, 5, 11, 5);
        VoxelShape g2 = Block.box(12, 9, 4, 11, 11, 5);
        VoxelShape g3 = Block.box(4, 9, 12, 5, 11, 11);
        VoxelShape g4 = Block.box(12, 9, 12, 11, 11, 11);

        return VoxelShapes.or(a, b, c, d, e, f, g1, g2, g3, g4);
    }

    public PedestalBlock() {
        super(Properties.of(Material.STONE).strength(3f).noOcclusion());
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
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

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader worldIn) {
        return new PedestalTileEntity();
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return pedestalShape;
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        PedestalTileEntity tile = getTileEntity(world, pos);
        if (tile == null) return ActionResultType.SUCCESS;
        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty() && !tile.extractItem(0, 1, true).isEmpty()) {
            ItemStack stack2 = tile.extractItem(0, 1, false);
            takeItemPlayer(player, hand, stack2);
            return ActionResultType.SUCCESS;

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
            return ActionResultType.SUCCESS;
        }
        return super.use(state, world, pos, player, hand, hit);
    }

    @Override
    protected void clearContainer(BlockState state, World worldIn, BlockPos pos) {
        PedestalTileEntity tile = getTileEntity(worldIn, pos);
        if (tile != null && tile.hasStack()) {
            dropItem(worldIn, pos, tile.removeStack());
        }
    }

    @Nullable
    private PedestalTileEntity getTileEntity(IBlockReader world, BlockPos pos) {
        TileEntity tile = world.getBlockEntity(pos);
        if (tile instanceof PedestalTileEntity) {
            return (PedestalTileEntity) tile;
        }
        return null;
    }
}
