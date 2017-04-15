package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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

    public String getLocalizedName() {
        return UtilLib.translate(getUnlocalizedName() + ".name");
    }

    @Override
    public final ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        return onItemRightClick(worldIn, playerIn, hand);
    }

    @Override
    public final EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return onItemUse(playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }

    protected ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        return super.onItemRightClick(playerIn.getHeldItem(handIn), worldIn, playerIn, handIn);
    }

    protected EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return super.onItemUse(player.getHeldItem(hand), player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }
}
