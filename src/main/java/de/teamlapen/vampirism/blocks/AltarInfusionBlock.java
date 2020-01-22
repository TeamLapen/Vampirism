package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.tileentity.AltarInfusionTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Altar of infusion
 */
public class AltarInfusionBlock extends VampirismBlockContainer {
    protected static final VoxelShape altarBase = makeShape();
    private final static String name = "altar_infusion";

    private static VoxelShape makeShape() {
        //base
        VoxelShape a = Block.makeCuboidShape(5, 0, 5, 11, 4, 11);
        VoxelShape b = Block.makeCuboidShape(2, 4, 2, 14, 5, 14);
        VoxelShape c = Block.makeCuboidShape(1, 5, 1, 15, 6, 15);
        //side
        VoxelShape d1 = Block.makeCuboidShape(1, 6, 1, 3, 7, 15);
        VoxelShape d2 = Block.makeCuboidShape(1, 6, 1, 15, 7, 3);
        VoxelShape d3 = Block.makeCuboidShape(15, 6, 15, 15, 7, 3);
        VoxelShape d4 = Block.makeCuboidShape(15, 6, 15, 3, 7, 15);
        //pillar
        VoxelShape e1 = Block.makeCuboidShape(1, 6, 1, 3, 12, 3);
        VoxelShape e2 = Block.makeCuboidShape(13, 6, 1, 15, 12, 3);
        VoxelShape e3 = Block.makeCuboidShape(13, 6, 13, 15, 12, 15);
        VoxelShape e4 = Block.makeCuboidShape(1, 6, 13, 3, 12, 15);
        //pillar top
        VoxelShape f1 = Block.makeCuboidShape(1, 12, 1, 2, 13, 2);
        VoxelShape f2 = Block.makeCuboidShape(14, 12, 1, 15, 13, 2);
        VoxelShape f3 = Block.makeCuboidShape(1, 12, 14, 2, 13, 15);
        VoxelShape f4 = Block.makeCuboidShape(14, 12, 14, 15, 13, 15);
        //middle base
        VoxelShape g = Block.makeCuboidShape(5, 6, 5, 11, 7, 11);
        //blood
        VoxelShape h1 = Block.makeCuboidShape(5, 9, 5, 11, 11, 11);
        VoxelShape h2 = Block.makeCuboidShape(7, 7, 7, 9, 13, 9);
        VoxelShape h3 = Block.makeCuboidShape(6, 8, 6, 10, 12, 10);

        return VoxelShapes.or(a, b, c, d1, d2, d3, d4, e1, e2, e3, e4, f1, f2, f3, f4, g, h1, h2, h3);
    }

    public AltarInfusionBlock() {
        super(name, Properties.create(Material.ROCK).hardnessAndResistance(5));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader world) {
        return new AltarInfusionTileEntity();
    }

    @Override
    public int getHarvestLevel(BlockState state) {
        return 2;
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState state) {
        return ToolType.PICKAXE;
    }


    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return altarBase;
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ItemStack heldItem = player.getHeldItem(hand);
        AltarInfusionTileEntity te = (AltarInfusionTileEntity) worldIn.getTileEntity(pos);
        //If empty hand and can start -> StartAdvanced
        if (worldIn.isRemote || te == null) return ActionResultType.SUCCESS;
        AltarInfusionTileEntity.Result result = te.canActivate(player, true);
        if (heldItem.isEmpty()) {
            if (result == AltarInfusionTileEntity.Result.OK) {
                te.startRitual(player);
                return ActionResultType.SUCCESS;
            }

        }
        //If non empty hand or missing tileInventory -> open GUI
        if (!heldItem.isEmpty() || result == AltarInfusionTileEntity.Result.INVMISSING) {
            if (te.getCurrentPhase() != AltarInfusionTileEntity.PHASE.NOT_RUNNING) {
                player.sendMessage(new TranslationTextComponent("text.vampirism.altar_infusion.ritual_still_running"));
                return ActionResultType.SUCCESS;
            }
            player.openContainer(te);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            dropItems(worldIn, pos);

            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    private void dropItems(World world, BlockPos pos) {
        Random rand = new Random();

        TileEntity tileEntity = world.getTileEntity(pos);
        if (!(tileEntity instanceof IInventory)) {
            return;
        }
        IInventory inventory = (IInventory) tileEntity;

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack item = inventory.getStackInSlot(i);

            if (!item.isEmpty()) {
                float rx = rand.nextFloat() * 0.8F + 0.1F;
                float ry = rand.nextFloat() * 0.8F + 0.1F;
                float rz = rand.nextFloat() * 0.8F + 0.1F;

                ItemEntity entityItem = new ItemEntity(world, pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz, item.copy());

                if (item.hasTag()) {
                    entityItem.getItem().setTag(item.getTag().copy());
                }

                float factor = 0.05F;
                entityItem.setMotion(rand.nextGaussian() * factor, rand.nextGaussian() * factor + 0.2F, rand.nextGaussian() * factor);
                world.addEntity(entityItem);
                inventory.setInventorySlotContents(i, ItemStack.EMPTY);
            }
        }
    }
}