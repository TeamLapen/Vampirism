package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.tileentity.AlchemicalCauldronTileEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class AlchemicalCauldronBlock extends AbstractFurnaceBlock {
    public static final String regName = "alchemical_cauldron";
    /**
     * 0: No liquid,
     * 1: Liquid,
     * 2: Boiling liquid
     */
    public static final IntegerProperty LIQUID = IntegerProperty.create("liquid", 0, 2);
    protected static final VoxelShape cauldronShape = Block.makeCuboidShape(1, 0, 1, 15, 14, 15);

    public AlchemicalCauldronBlock() {
        super(Block.Properties.create(Material.IRON).hardnessAndResistance(4f));
        this.setDefaultState(this.stateContainer.getBaseState().with(LIQUID, 0).with(FACING, Direction.NORTH).with(LIT, false));
        this.setRegistryName(REFERENCE.MODID, regName);
    }

    @Override
    protected void interactWith(World world, BlockPos blockPos, PlayerEntity playerEntity) {
        TileEntity tile = world.getTileEntity(blockPos);
        if (tile instanceof AlchemicalCauldronTileEntity) {
            playerEntity.openContainer((INamedContainerProvider) tile);
            playerEntity.addStat(ModStats.interact_alchemical_cauldron);
        }
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new AlchemicalCauldronTileEntity();
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return cauldronShape;
    }

    @Override
    public boolean isNormalCube(BlockState p_220081_1_, IBlockReader p_220081_2_, BlockPos p_220081_3_) {
        return false;
    }

    @Override
    public BlockState getExtendedState(BlockState state, IBlockReader world, BlockPos pos) {
        return null;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos blockPos, BlockState blockState, LivingEntity entity, ItemStack stack) {
        super.onBlockPlacedBy(world, blockPos, blockState, entity, stack);
        TileEntity tile = world.getTileEntity(blockPos);
        if (entity instanceof PlayerEntity && tile instanceof AlchemicalCauldronTileEntity) {
            ((AlchemicalCauldronTileEntity) tile).setOwnerID((PlayerEntity) entity);
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(LIT, FACING, LIQUID);
    }
}
