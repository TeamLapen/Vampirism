package de.teamlapen.vampirism.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;

import java.util.List;


public class ItemPureBlood extends VampirismItem {

    public static final int COUNT = 5;
    private final static String name = "pureBlood";

    public ItemPureBlood() {
        super(name);
        this.setHasSubtypes(true);
    }

    public IChatComponent getDisplayName(ItemStack stack) {
        return new ChatComponentTranslation(getUnlocalizedName() + ".name").appendSibling(new ChatComponentText(" ")).appendSibling(new ChatComponentTranslation("text.vampirism.purity")).appendSibling(new ChatComponentText(" " + (stack.getItemDamage() + 1)));
    }


    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
        list.add(EnumChatFormatting.RED + StatCollector.translateToLocal("text.vampirism.purity") + ": " + (itemStack.getItemDamage() + 1) + "/" + COUNT);
    }


    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < COUNT; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }
}
