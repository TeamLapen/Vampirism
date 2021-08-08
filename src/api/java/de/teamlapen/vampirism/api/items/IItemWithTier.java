package de.teamlapen.vampirism.api.items;

import net.minecraft.util.StringRepresentable;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

/**
 * Simple interface for tileInventory that exist in different tiers.
 */
public interface IItemWithTier {

    @OnlyIn(Dist.CLIENT)
    default void addTierInformation(List<Component> tooltip) {
        TIER t = getVampirismTier();
        if (t != TIER.NORMAL) {
            ChatFormatting format = t == TIER.ENHANCED ? ChatFormatting.YELLOW : ChatFormatting.AQUA;
            tooltip.add(new TranslatableComponent("item.vampirism.item.tier." + t.getSerializedName().toLowerCase()).withStyle(format));
        }
    }

    /**
     * The registry name all tier items are derived from (basename+"_"+tier)
     */
    String getBaseRegName();

    /**
     * @return The tier of the item stack
     */
    TIER getVampirismTier();

    enum TIER implements StringRepresentable {
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
