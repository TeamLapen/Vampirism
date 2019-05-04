package de.teamlapen.vampirism.api.items;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

/**
 * Simple interface for items that exist in different tiers.
 */
public interface IItemWithTier {

    @OnlyIn(Dist.CLIENT)
    default void addTierInformation(List<ITextComponent> tooltip) {
        TIER t = getTier();
        if (t != TIER.NORMAL) {
            tooltip.add(new TextComponentTranslation("text.vampirism.item.tier." + t.getName().toLowerCase()).applyTextStyle(TextFormatting.AQUA));
        }
    }


    enum TIER implements IStringSerializable {
        NORMAL, ENHANCED, ULTIMATE;


        @Override
        public String getName() {
            return name().toLowerCase();
        }
    }

    /**
     * @return The tier of the item stack
     */
    TIER getTier();

}
