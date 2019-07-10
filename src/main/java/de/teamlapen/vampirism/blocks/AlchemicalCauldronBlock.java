package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.tileentity.TileAlchemicalCauldron;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;


public class AlchemicalCauldronBlock extends VampirismBlockContainer {

    public final static String regName = "alchemical_cauldron";
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

    /**
     * 0: No liquid,
     * 1: Liquid,
     * 2: Boiling liquid
     */
    public static final IntegerProperty LIQUID = IntegerProperty.create("liquid", 0, 2);
    public static final BooleanProperty BURNING = BooleanProperty.create("burning");
    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.07, 0, 0.07, 0.93, 0.87, 0.93);
    protected static final VoxelShape cauldronShape = Block.makeCuboidShape(1, 0, 1, 15, 14, 15);

    public AlchemicalCauldronBlock() {
        super(regName, Block.Properties.create(Material.IRON).hardnessAndResistance(4f));
        this.setDefaultState(this.stateContainer.getBaseState().with(LIQUID, 0).with(FACING, Direction.NORTH).with(BURNING, false));
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileAlchemicalCauldron();
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getNearestLookingDirection());
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return cauldronShape;
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    /*
    TODO 1.13 make sure tile entity updates block state
    @Override
    public IBlockState getActualState(IBlockState state, IBlockReader worldIn, BlockPos pos) {
        TileAlchemicalCauldron t = getTile(worldIn, pos);
        if (t != null) {
            state = state.withProperty(LIQUID, !t.isFilled() ? 0 : t.isCooking() ? 2 : 1).withProperty(BURNING, t.isBurning());
        }
        return state;
    }
     */

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit) {
        if (!worldIn.isRemote) {
            TileAlchemicalCauldron tile = getTile(worldIn, pos);
            if (tile != null) {
                if (tile.canUse(playerIn)) {
                    //playerIn.openGui(VampirismMod.instance, ModGuiHandler.ID_ALCHEMICAL_CAULDRON, worldIn, pos.getX(), pos.getY(), pos.getZ());//TODO 1.14
                }
            }
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        TileAlchemicalCauldron tile = getTile(worldIn, pos);
        if (tile != null && placer instanceof PlayerEntity) {
            tile.setOwner((PlayerEntity) placer);
        }
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIQUID, BURNING);
    }

    @Nullable
    private TileAlchemicalCauldron getTile(IBlockReader world, BlockPos pos) {
        TileEntity t = world.getTileEntity(pos);
        if (t instanceof TileAlchemicalCauldron) return (TileAlchemicalCauldron) t;
        return null;
    }

}
