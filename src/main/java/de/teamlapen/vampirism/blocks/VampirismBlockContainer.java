package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Vampirism default block container with set creative tab, registry name and unloc name
 */
public abstract class VampirismBlockContainer extends BlockContainer {
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    private boolean hasFacing = false;

    public VampirismBlockContainer(String regName, Material materialIn) {
        super(materialIn);
        setCreativeTab(VampirismMod.creativeTab);
        setRegistryName(REFERENCE.MODID, regName);
        this.setUnlocalizedName(REFERENCE.MODID + "." + regName);
    }

    @Override
    public final boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        return onBlockActivated(worldIn, pos, state, playerIn, hand, side, hitX, hitY, hitZ);
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        if (hasFacing) {

            return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));

        }
        return state;
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        if (hasFacing) {
            return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
        }
        return state;
    }

    /**
     * For compat with 1.11
     */
    protected boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, playerIn.getHeldItem(hand), facing, hitX, hitY, hitZ);
    }

    /**
     * Call this if the block is using {@link VampirismBlockContainer#FACING} in it's block state
     * This will e.g. make the block rotate with {@link Block#withRotation(IBlockState, Rotation)}
     */
    protected void setHasFacing() {
        hasFacing = true;
    }

}
