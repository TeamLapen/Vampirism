package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Base class for most of Vampirism's items
 */
public class VampirismItem extends Item {
    protected final String regName;

    /**
     * Set's the registry name and the unlocalized name
     *
     * @param regName
     */
    public VampirismItem(String regName) {
        this.regName = regName;
        setCreativeTab(VampirismMod.creativeTab);
        setRegistryName(REFERENCE.MODID, regName);
        this.setUnlocalizedName(REFERENCE.MODID + "." + regName);
    }

    /**
     * For compat with 1.11 and below
     */
    @SideOnly(Side.CLIENT)
    @Override
    public final void addInformation(ItemStack stack, @Nullable World playerIn, List<String> tooltip, ITooltipFlag advanced) {
        this.addInformation(stack, Minecraft.getMinecraft().player, tooltip, advanced.isAdvanced());
    }

    public String getLocalizedName() {
        return UtilLib.translate(getUnlocalizedName() + ".name");
    }

    /**
     * @return The name this item is registered in the GameRegistry
     */
    public String getRegisteredName() {
        return regName;
    }

    /**
     * For compat with 1.11 and below
     */
    @Override
    public final void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if(isInCreativeTab(tab)){
            this.getSubItems(this, tab, items);
        }
    }

    /**
     * For compat with 1.11 and below
     */
    @SideOnly(Side.CLIENT)
    protected void addInformation(ItemStack stack, @Nullable EntityPlayer playerIn, List<String> tooltip, boolean advanced) {

    }

    /**
     * Only called if this item is in the given tab
     * For compat with 1.11 and below
     */
    protected void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {

    }
}
