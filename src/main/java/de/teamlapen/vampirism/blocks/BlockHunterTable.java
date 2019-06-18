package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.inventory.HunterTableContainer;
import de.teamlapen.vampirism.network.ModGuiHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Table for hunter "education/leveling"
 * TODO create a unique texture for the top side
 */
public class BlockHunterTable extends VampirismBlock {
    public static final String name = "hunter_table";
    public static final DirectionProperty FACING = BlockHorizontal.HORIZONTAL_FACING;


    public static HunterTableContainer createInventoryContainer(EntityPlayer player, BlockPos pos) {
        return new HunterTableContainer(player, pos);
    }

    public BlockHunterTable() {
        super(name, Properties.create(Material.WOOD).hardnessAndResistance(0.5f));
        this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, EnumFacing.NORTH));
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Nullable
    @Override
    public IBlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getNearestLookingDirection());
    }

    @Override
    public IBlockState mirror(IBlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        player.openGui(VampirismMod.instance, ModGuiHandler.ID_HUNTER_TABLE, world, pos.getX(), pos.getY(), pos.getZ());//TODO open Gui
        return true;
    }

    @Override
    public IBlockState rotate(IBlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }


    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
        builder.add(FACING);
    }



}
