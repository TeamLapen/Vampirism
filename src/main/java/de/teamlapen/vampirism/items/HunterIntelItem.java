package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.player.hunter.HunterLevelingConf;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
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
public class HunterIntelItem extends VampirismItem {


    private final static Logger LOGGER = LogManager.getLogger();
    private final static String name = "hunter_intel";

    public static HunterIntelItem getIntelForExactlyLevel(int level) {
        return getIntelForLevel(level - 5);
    }

    public static HunterIntelItem getIntelForLevel(int level) {
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
            case 9:
                return ModItems.hunter_intel_9;
            default:
                LOGGER.warn("HunterIntel of level {} does not exist", level);
                return ModItems.hunter_intel_9;

        }
    }
    private final int level;

    public HunterIntelItem(int level) {
        super(name + "_" + level, new Properties().group(VampirismMod.creativeTab));
        this.level = level;
        setTranslation_key(name);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("item.vampirism.hunter_intel.for_level").appendText(": " + (level + 5)).applyTextStyle(TextFormatting.RED));
    }

    public ITextComponent getCustomName() {
        return new TranslationTextComponent(this.getDefaultTranslationKey()).appendText(" ").appendSibling(new TranslationTextComponent("item.vampirism.hunter_intel.for_level").appendText(" " + (level + 5)));
    }

    /**
     * @return Level of this hunter intel 0 - {@link HunterLevelingConf#HUNTER_INTEL_COUNT}-1
     */
    public int getLevel() {
        return level;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
