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
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
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
    private final static String name = "altar_infusion";
    protected static final VoxelShape altarUpperHalf = Block.makeCuboidShape(1, 4, 1, 15, 14, 15);
    protected static final VoxelShape altarBase = Block.makeCuboidShape(5, 0, 5, 11, 4, 11);
    protected static final VoxelShape altarShape = VoxelShapes.combine(altarUpperHalf, altarBase, IBooleanFunction.AND);

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
        return altarShape;
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
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ItemStack heldItem = player.getHeldItem(hand);
        AltarInfusionTileEntity te = (AltarInfusionTileEntity) worldIn.getTileEntity(pos);
        //If empty hand and can start -> StartAdvanced
        if (worldIn.isRemote || te == null) return true;
        int result = te.canActivate(player, true);
        if (heldItem.isEmpty()) {
            if (result == 1) {
                te.startRitual(player);
                return true;
            }

        }
        //If non empty hand or missing items -> open GUI
        if (!heldItem.isEmpty() || result == -4) {
            if (te.getCurrentPhase() != AltarInfusionTileEntity.PHASE.NOT_RUNNING) {
                player.sendMessage(new TranslationTextComponent("text.vampirism.ritual_still_running"));
                return true;
            }
            player.openContainer(te);
            return true;
        }
        return true;
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