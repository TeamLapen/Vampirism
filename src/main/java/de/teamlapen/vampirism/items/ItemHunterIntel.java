package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.entity.player.hunter.HunterLevelingConf;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Item used in the hunter leveling process. Is create in an hunter table.
 */
public class ItemHunterIntel extends VampirismItem {


    private final static String name = "hunterIntel";

    public ItemHunterIntel() {
        super(name);
        this.hasSubtypes = true;
    }

    public IChatComponent getDisplayName(ItemStack stack) {
        return new ChatComponentTranslation(getUnlocalizedName() + ".name").appendSibling(new ChatComponentText(" ")).appendSibling(new ChatComponentTranslation("text.vampirism.for_level")).appendSibling(new ChatComponentText(" " + HunterLevelingConf.instance().getLevelForHunterIntelMeta(stack.getMetadata())));
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
        list.add(EnumChatFormatting.RED + StatCollector.translateToLocal("text.vampirism.for_level") + ": " + HunterLevelingConf.instance().getLevelForHunterIntelMeta(itemStack.getMetadata()));
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < HunterLevelingConf.instance().HUNTER_INTEL_COUNT; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }
}
