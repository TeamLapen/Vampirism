package de.teamlapen.faction.api.factions.refinements;

import com.mojang.serialization.Codec;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.world.items.IRefinementItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;
import java.util.Optional;

/**
 * A set of refinements that can be applied to a {@link IRefinementItem}
 * <p>
 * Refinement sets are loaded from datapacks. Registry key {@link de.teamlapen.faction.api.FactionRegistries.Keys#REFINEMENT_SET}
 */
public interface IRefinementSet {

    int getColor();

    TagKey<IFaction<?>> getFaction();

    Component getName();

    Rarity getRarity();

    HolderSet<IRefinement> getRefinements();

    /**
     * @return The accessory type this can be on, or empty if all
     */
    Optional<IRefinementItem.AccessorySlotType> getSlotType();

    enum Rarity implements StringRepresentable {
        COMMON(4, ChatFormatting.WHITE),
        UNCOMMON(3, ChatFormatting.GREEN),
        RARE(3, ChatFormatting.BLUE),
        EPIC(2, ChatFormatting.DARK_PURPLE),
        LEGENDARY(1, ChatFormatting.GOLD);

        public static final Codec<Rarity> CODEC = StringRepresentable.fromEnum(Rarity::values);

        public final int weight;
        public final ChatFormatting color;

        Rarity(int weight, ChatFormatting color) {
            this.weight = weight;
            this.color = color;
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
