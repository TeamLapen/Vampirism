package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.player.hunter.HunterLevelingConf;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Item used in the hunter leveling process. Is create in an hunter table.
 */
public class ItemHunterIntel extends VampirismItem {


    private final static Logger LOGGER = LogManager.getLogger();
    private final static String name = "hunter_intel";
    private final int level;

    public ItemHunterIntel(int level) {
        super(name + "_" + level, new Properties());
        this.level = level;
        setTranslation_key(name);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TextComponentTranslation("text.vampirism.for_level").appendText(": " + HunterLevelingConf.instance().getHunterIntelMetaForLevel(level)).applyTextStyle(TextFormatting.RED));
    }


    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        return new TextComponentTranslation(getTranslationKey() + ".name").appendSibling(new TextComponentString(" ")).appendSibling(new TextComponentTranslation("text.vampirism.for_level")).appendSibling(new TextComponentString(" " + HunterLevelingConf.instance().getLevelForHunterIntelMeta(level)));
    }




    @OnlyIn(Dist.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    /**
     * @return Level of this hunter intel 0 - {@link HunterLevelingConf#HUNTER_INTEL_COUNT}-1
     */
    public int getLevel() {
        return level;
    }

    public static ItemHunterIntel getIntelForLevel(int level) {
        switch (level) {
            case 0:
                return ModItems.hunter_intel_0;
            case 1:
                return ModItems.hunter_intel_1;
            case 2:
                return ModItems.hunter_intel_2;
            case 3:
                return ModItems.hunter_intel_3;
            case 4:
                return ModItems.hunter_intel_4;
            case 5:
                return ModItems.hunter_intel_5;
            case 6:
                return ModItems.hunter_intel_6;
            case 7:
                return ModItems.hunter_intel_7;
            case 8:
                return ModItems.hunter_intel_8;
            default:
                LOGGER.warn("HunterIntel of level {} does not exist", level);
                return ModItems.hunter_intel_8;

        }
    }
}
