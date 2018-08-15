package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.List;


public class ItemPureBlood extends VampirismItem {

    public static final int COUNT = 5;
    private final static String name = "pure_blood";

    public ItemPureBlood() {
        super(name);
        this.setHasSubtypes(true);
    }


    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.RED + UtilLib.translate("text.vampirism.purity") + ": " + (stack.getItemDamage() + 1) + "/" + COUNT);

    }


    public ITextComponent getDisplayName(ItemStack stack) {
        ITextComponent t = new TextComponentTranslation(getUnlocalizedName() + ".name");
        if (stack.getMetadata() != OreDictionary.WILDCARD_VALUE) {
            t.appendSibling(new TextComponentString(" ")).appendSibling(new TextComponentTranslation("text.vampirism.purity")).appendSibling(new TextComponentString(" " + (stack.getItemDamage() + 1)));
        }
        return t;
    }


    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            for (int i = 0; i < COUNT; i++) {
                items.add(new ItemStack(this, 1, i));
            }
        }
    }

}
