package de.teamlapen.vampirism.api.entity.player.refinement;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Set;

public interface IRefinementSet extends IForgeRegistryEntry<IRefinementSet> {

    @Nonnull
    Set<IRefinement> getRefinements();

    @Nonnull
    ITextComponent getName();

    @Nonnull
    ITextComponent getDescription();

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

}
