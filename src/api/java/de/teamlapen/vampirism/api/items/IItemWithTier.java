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
            TextFormatting format = t == TIER.ENHANCED ? TextFormatting.YELLOW : TextFormatting.AQUA;
            tooltip.add(new TranslationTextComponent("item.vampirism.item.tier." + t.getSerializedName().toLowerCase()).withStyle(format));
        }
    }

    /**
     * @return The tier of the item stack
     */
    TIER getVampirismTier();

    enum TIER implements IStringSerializable {
        NORMAL("normal"), ENHANCED("enhanced"), ULTIMATE("ultimate");

        private final String name;

        TIER(String name) {
            this.name = name;
        }

        public String getName() {
            return this.getSerializedName();
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

}
