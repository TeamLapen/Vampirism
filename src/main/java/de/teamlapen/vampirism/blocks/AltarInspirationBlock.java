package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.FluidLib;
import de.teamlapen.vampirism.tileentity.AltarInspirationTileEntity;
import de.teamlapen.vampirism.world.VampirismWorldData;
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
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Altar of inspiration used for vampire levels 1-4
 */
public class AltarInspirationBlock extends VampirismBlockContainer {
    public final static String regName = "altar_inspiration";
    protected static final VoxelShape altarShape = Block.makeCuboidShape(0, 0, 0, 16, 14, 16);

    public AltarInspirationBlock() {
        super(regName, Properties.create(Material.IRON).hardnessAndResistance(2f));
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
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
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
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ItemStack stack = player.getHeldItem(hand);
        if (!stack.isEmpty() && !worldIn.isRemote) {
            LazyOptional<IFluidHandlerItem> opt = FluidLib.getFluidItemCap(stack);
            if (opt.isPresent()) {
                AltarInspirationTileEntity tileEntity = (AltarInspirationTileEntity) worldIn.getTileEntity(pos);
                if (!player.isSneaking() && tileEntity != null) {
                    tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).map((handler) -> {
                        FluidActionResult result = FluidUtil.tryEmptyContainer(stack, handler, Integer.MAX_VALUE, player, true);
                        if (result.isSuccess()) {
                            player.setHeldItem(hand, result.getResult());
                            return true;
                        }
                        return false;
                    });

                }
                tileEntity.markDirty();
                return true;
            }
        }
        if (stack.isEmpty()) {
            AltarInspirationTileEntity tileEntity = (AltarInspirationTileEntity) worldIn.getTileEntity(pos);
            tileEntity.startRitual(player);
        }

        return true;
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onReplaced(state, worldIn, pos, newState, isMoving);
        if (worldIn instanceof ServerWorld) {
            VampirismWorldData.get((ServerWorld) worldIn).onAltarInspirationDestroyed(pos);
        }
    }

}
