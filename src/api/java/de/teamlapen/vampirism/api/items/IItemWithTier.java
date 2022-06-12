package de.teamlapen.vampirism.api.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Simple interface for tileInventory that exist in different tiers.
 */
public interface IItemWithTier extends ItemLike {

    @OnlyIn(Dist.CLIENT)
    default void addTierInformation(List<Component> tooltip) {
        TIER t = getVampirismTier();
        if (t != TIER.NORMAL) {
            ChatFormatting format = t == TIER.ENHANCED ? ChatFormatting.YELLOW : ChatFormatting.AQUA;
            tooltip.add(new TranslatableComponent("item.vampirism.item.tier." + t.getSerializedName().toLowerCase()).withStyle(format));
        }
    }

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

        @Nonnull
        @Override
        public String getSerializedName() {
            return name;
        }
    }

}
