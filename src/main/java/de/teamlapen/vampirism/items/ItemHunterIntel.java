package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.player.hunter.HunterLevelingConf;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
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

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
        list.add(TextFormatting.RED + I18n.translateToLocal("text.vampirism.for_level") + ": " + HunterLevelingConf.instance().getLevelForHunterIntelMeta(itemStack.getMetadata()));
    }

    public ITextComponent getDisplayName(ItemStack stack) {
        return new TextComponentTranslation(getUnlocalizedName() + ".name").appendSibling(new TextComponentString(" ")).appendSibling(new TextComponentTranslation("text.vampirism.for_level")).appendSibling(new TextComponentString(" " + HunterLevelingConf.instance().getLevelForHunterIntelMeta(stack.getMetadata())));
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < HunterLevelingConf.instance().HUNTER_INTEL_COUNT; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
