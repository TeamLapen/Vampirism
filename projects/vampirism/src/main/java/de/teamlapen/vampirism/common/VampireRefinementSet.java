package de.teamlapen.vampirism.common;

import de.teamlapen.factions.api.refinements.IRefinement;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.common.RefinementSet;
import de.teamlapen.vampirism.common.tags.ModFactionTags;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class VampireRefinementSet extends RefinementSet {
    public VampireRefinementSet(Rarity rarity, int color, Set<Holder<IRefinement>> refinements) {
        super(rarity, color, refinements);
    }

    @SafeVarargs
    public VampireRefinementSet(Rarity rarity, int color, Holder<IRefinement>... refinements) {
        super(rarity, color, refinements);
    }

    @NotNull
    @Override
    public TagKey<IFaction<?>> getFaction() {
        return ModFactionTags.IS_VAMPIRE;
    }
}
