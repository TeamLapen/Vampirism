package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Vampirism default block with set creative tab, registry name and unloc name
 */
public class VampirismBlock extends Block {
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    private final String registeredName;
    private boolean hasFacing = false;

    public VampirismBlock(String regName, Material materialIn) {
        super(materialIn);
        setCreativeTab(VampirismMod.creativeTab);
        setRegistryName(REFERENCE.MODID, regName);
        this.setUnlocalizedName(REFERENCE.MODID + "." + regName);
        this.registeredName = regName;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public final void addInformation(ItemStack stack, @Nullable World playerIn, List<String> tooltip, ITooltipFlag advanced) {
        this.addInformation(stack, Minecraft.getMinecraft().player, tooltip, advanced.isAdvanced());
    }

    /**
     * @return The name this block is registered in the GameRegistry
     */
    public String getRegisteredName() {
        return registeredName;
    }

    /**
     * For compat with 1.11 and below
     */
    @Override
    public final void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> tab) {
        this.getSubBlocks(Item.getItemFromBlock(this), itemIn, tab);
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
     * For compat with 1.11 and below
     */
    @SideOnly(Side.CLIENT)
    protected void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {

    }

    /**
     * For compat with 1.11 and below
     */
    protected void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
        subItems.add(new ItemStack(this));
    }

    /**
     * Call this if the block is using {@link VampirismBlock#FACING} in it's block state
     * This will e.g. make the block rotate with {@link Block#withRotation(IBlockState, Rotation)}
     */
    protected void setHasFacing() {
        hasFacing = true;
    }

}
