package de.teamlapen.vampirism.api.entity.player.refinement;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.TextComponent;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.Set;

public interface IRefinementSet extends IForgeRegistryEntry<IRefinementSet> {

    @Nonnull
    Set<IRefinement> getRefinements();

    @Nonnull
    TextComponent getName();

    @Nonnull
    String getTranslationKey();

    @Nonnull
    Rarity getRarity();

    @Nonnull
    IFaction<?> getFaction();

    int getColor();

}
