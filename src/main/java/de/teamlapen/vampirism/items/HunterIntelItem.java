package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.player.hunter.HunterLevelingConf;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.world.item.Item.Properties;

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
    private Component tooltip;

    public HunterIntelItem(int level) {
        super(name + "_" + level, new Properties().tab(VampirismMod.creativeTab));
        this.level = level;
        setTranslation_key(name);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level worldIn, @Nonnull List<Component> tooltips, @Nonnull TooltipFlag flagIn) {
        if (this.tooltip == null) {
            this.tooltip = new TranslatableComponent("text.vampirism.for_up_to_level").append(new TextComponent(": " + (level + 5))).withStyle(ChatFormatting.RED);
        }
        tooltips.add(this.tooltip);
    }

    public Component getCustomName() {
        return new TranslatableComponent(this.getOrCreateDescriptionId()).append(new TextComponent(" ")).append(new TranslatableComponent("text.vampirism.for_up_to_level").append(new TextComponent(" " + (level + 5))));
    }

    /**
     * @return Level of this hunter intel 0 - {@link HunterLevelingConf#HUNTER_INTEL_COUNT}-1
     */
    public int getLevel() {
        return level;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isFoil(@Nonnull ItemStack stack) {
        return true;
    }
}
