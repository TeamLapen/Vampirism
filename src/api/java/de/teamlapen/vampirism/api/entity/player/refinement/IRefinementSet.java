package de.teamlapen.vampirism.api.entity.player.refinement;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Set;

public interface IRefinementSet extends IForgeRegistryEntry<IRefinementSet> {

    @Nonnull
    Set<IRefinement> getRefinements();

    @Nonnull
    ITextComponent getName();

    /**
     *
     * @return The accessory type this can be on, or empty if all
     */
    Optional<IRefinementItem.AccessorySlotType> getSlotType();

    @Nonnull
    Rarity getRarity();

    @Nonnull
    IFaction<?> getFaction();

    int getColor();

    enum Rarity {
        COMMON(4, TextFormatting.WHITE),
        UNCOMMON(3, TextFormatting.GREEN),
        RARE(3, TextFormatting.BLUE),
        EPIC(2, TextFormatting.DARK_PURPLE),
        LEGENDARY(1, TextFormatting.GOLD);

        public final int weight;
        public final TextFormatting color;

        Rarity(int weight, TextFormatting color) {
            this.weight = weight;
            this.color = color;
        }
    }
}
