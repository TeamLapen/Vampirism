package de.teamlapen.vampirism.api.entity.player.refinement;

import net.minecraft.item.Rarity;
import net.minecraft.util.text.TextComponent;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Set;

public interface IRefinementSet extends IForgeRegistryEntry<IRefinementSet> {

    Set<IRefinement> getRefinements();

    TextComponent getName();

    String getTranslationKey();

    Rarity getRarity();

}
