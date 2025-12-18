package de.teamlapen.vampirism.api.world.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Simple interface for tileInventory that exist in different tiers.
 */
public interface IItemWithTier extends ItemLike {

    default void addTierInformation(@NotNull Consumer<Component> tooltip) {
        Tier tier = getVampirismTier();
        if (tier != Tier.NORMAL) {
            ChatFormatting format = tier == Tier.ENHANCED ? ChatFormatting.YELLOW : ChatFormatting.AQUA;
            tooltip.accept(Component.translatable("item.vampirism.item.tier." + tier.getSerializedName().toLowerCase()).withStyle(format));
        }
    }

    /**
     * @return The tier of the item stack
     */
    Tier getVampirismTier();

    enum Tier implements StringRepresentable {
        NORMAL("normal"),
        ENHANCED("enhanced"),
        ULTIMATE("ultimate");

        private final String name;

        Tier(String name) {
            this.name = name;
        }

        public @NotNull String getName() {
            return this.getSerializedName();
        }

        @NotNull
        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
