package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.ModGuiHandler;
import de.teamlapen.vampirism.tileentity.TileGrinder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockGrinder extends VampirismBlockContainer {
    private final static String regName = "blood_grinder";
    public static final DirectionProperty FACING = BlockHorizontal.HORIZONTAL_FACING;


    public BlockGrinder() {
        super(regName, Properties.create(Material.IRON).hardnessAndResistance(5).sound(SoundType.METAL));
        this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, EnumFacing.NORTH));

    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileGrinder();
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
        if (world.isRemote) return true;
        player.openGui(VampirismMod.instance, ModGuiHandler.ID_BLOOD_GRINDER, world, pos.getX(), pos.getY(), pos.getZ());//TODO open Gui
        return true;
    }

    @Override
    public IBlockState rotate(IBlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face == EnumFacing.DOWN ? BlockFaceShape.CENTER_BIG : BlockFaceShape.UNDEFINED;
    }



    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
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
