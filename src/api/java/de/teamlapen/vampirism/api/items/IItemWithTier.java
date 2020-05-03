package de.teamlapen.vampirism.api.items;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

/**
 * Simple interface for tileInventory that exist in different tiers.
 */
public interface IItemWithTier {

    @OnlyIn(Dist.CLIENT)
    default void addTierInformation(List<ITextComponent> tooltip) {
        TIER t = getVampirismTier();
        if (t != TIER.NORMAL) {
            tooltip.add(new TranslationTextComponent("item.vampirism.item.tier." + t.getName().toLowerCase()).applyTextStyle(TextFormatting.AQUA));
        }
    }

    /**
     * @return The tier of the item stack
     */
    TIER getVampirismTier();

    /**
     * The registry name all tier items are derived from (basename+"_"+tier)
     */
    String getBaseRegName();

    enum TIER implements IStringSerializable {
        NORMAL, ENHANCED, ULTIMATE;


        @Override
        public String getName() {
            return name().toLowerCase();
        }
    }

}
