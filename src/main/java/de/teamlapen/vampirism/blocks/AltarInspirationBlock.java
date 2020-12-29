package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.FluidLib;
import de.teamlapen.vampirism.tileentity.AltarInspirationTileEntity;
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
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Altar of inspiration used for vampire levels 1-4
 */
public class AltarInspirationBlock extends VampirismBlockContainer {
    public final static String regName = "altar_inspiration";
    protected static final VoxelShape altarShape = makeShape();

    private static VoxelShape makeShape() {
        VoxelShape a = Block.makeCuboidShape(0, 0, 0, 16, 2, 16);
        VoxelShape b1 = Block.makeCuboidShape(0, 0, 0, 1, 6, 1);
        VoxelShape b2 = Block.makeCuboidShape(15, 0, 0, 16, 6, 1);
        VoxelShape b3 = Block.makeCuboidShape(0, 0, 15, 1, 6, 16);
        VoxelShape b4 = Block.makeCuboidShape(15, 0, 15, 16, 6, 16);
        VoxelShape c1 = Block.makeCuboidShape(6, 2, 6, 10, 3, 10);
        VoxelShape c2 = Block.makeCuboidShape(5, 3, 5, 11, 4, 11);
        VoxelShape c3 = Block.makeCuboidShape(4, 4, 4, 12, 5, 12);
        VoxelShape c4 = Block.makeCuboidShape(3, 5, 3, 13, 6, 13);
        VoxelShape c5 = Block.makeCuboidShape(2, 6, 2, 14, 7, 14);
        VoxelShape c6 = Block.makeCuboidShape(1, 7, 1, 15, 9, 15);
        VoxelShape c7 = Block.makeCuboidShape(2, 9, 2, 14, 10, 14);
        VoxelShape c8 = Block.makeCuboidShape(3, 10, 3, 13, 11, 13);
        VoxelShape c9 = Block.makeCuboidShape(4, 11, 4, 12, 12, 12);
        VoxelShape c10 = Block.makeCuboidShape(5, 12, 5, 11, 13, 11);
        VoxelShape c11 = Block.makeCuboidShape(6, 13, 6, 10, 14, 10);

        return VoxelShapes.or(a, b1, b2, b3, b4, c1, c2, c3, c4, c5, c5, c6, c7, c8, c9, c10, c11);
    }

    public AltarInspirationBlock() {
        super(regName, Properties.create(Material.IRON).hardnessAndResistance(2f).notSolid());
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new AltarInspirationTileEntity();
    }

    @Override
    public int getHarvestLevel(BlockState state) {
        return 1;
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState state) {
        return ToolType.PICKAXE;
    }

    @Nonnull
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return altarShape;
    }



    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ItemStack stack = player.getHeldItem(hand);
        if (!stack.isEmpty()) {
            LazyOptional<IFluidHandlerItem> opt = FluidLib.getFluidItemCap(stack);
            if (opt.isPresent()) {
                AltarInspirationTileEntity tileEntity = (AltarInspirationTileEntity) worldIn.getTileEntity(pos);
                if (!player.isSneaking() && tileEntity != null) {
                    FluidUtil.interactWithFluidHandler(player, hand, worldIn, pos, hit.getFace());
                }
                return ActionResultType.SUCCESS;
            }
        } else {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof AltarInspirationTileEntity) {
                ((AltarInspirationTileEntity) tileEntity).startRitual(player);
            }
        }

        return ActionResultType.SUCCESS;
    }

}
