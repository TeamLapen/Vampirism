package de.teamlapen.vampirism.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;


public class ItemPureBlood extends VampirismItem {

    public static final int COUNT = 5;
    private final static String name = "pure_blood";

    public ItemPureBlood() {
        super(name);
        this.setHasSubtypes(true);
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
        list.add(TextFormatting.RED + I18n.translateToLocal("text.vampirism.purity") + ": " + (itemStack.getItemDamage() + 1) + "/" + COUNT);
    }

    public ITextComponent getDisplayName(ItemStack stack) {
        return new TextComponentTranslation(getUnlocalizedName() + ".name").appendSibling(new TextComponentString(" ")).appendSibling(new TextComponentTranslation("text.vampirism.purity")).appendSibling(new TextComponentString(" " + (stack.getItemDamage() + 1)));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> list) {
        for (int i = 0; i < COUNT; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }
}
