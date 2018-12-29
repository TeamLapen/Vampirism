package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.player.hunter.HunterLevelingConf;
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

import javax.annotation.Nullable;
import java.util.List;

/**
 * Item used in the hunter leveling process. Is create in an hunter table.
 */
public class ItemHunterIntel extends VampirismItem {


    private final static String name = "hunter_intel";

    public ItemHunterIntel() {
        super(name);
        this.hasSubtypes = true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.RED + UtilLib.translate("text.vampirism.for_level") + ": " + HunterLevelingConf.instance().getLevelForHunterIntelMeta(stack.getMetadata()));
    }


    public ITextComponent getDisplayName(ItemStack stack) {
        return new TextComponentTranslation(getUnlocalizedName() + ".name").appendSibling(new TextComponentString(" ")).appendSibling(new TextComponentTranslation("text.vampirism.for_level")).appendSibling(new TextComponentString(" " + HunterLevelingConf.instance().getLevelForHunterIntelMeta(stack.getMetadata())));
    }


    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            for (int i = 0; i < HunterLevelingConf.instance().HUNTER_INTEL_COUNT; i++) {
                items.add(new ItemStack(this, 1, i));
            }
        }
    }


    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
