package de.teamlapen.vampirism.api.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Simple interface for tileInventory that exist in different tiers.
 */
public interface IItemWithTier extends ItemLike {

    @OnlyIn(Dist.CLIENT)
    default void addTierInformation(@NotNull List<Component> tooltip) {
        TIER t = getVampirismTier();
        if (t != TIER.NORMAL) {
            ChatFormatting format = t == TIER.ENHANCED ? ChatFormatting.YELLOW : ChatFormatting.AQUA;
            tooltip.add(Component.translatable("item.vampirism.item.tier." + t.getSerializedName().toLowerCase()).withStyle(format));
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
