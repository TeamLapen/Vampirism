package de.teamlapen.vampirism.common;

import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.refinements.IRefinement;
import de.teamlapen.faction.common.RefinementSet;
import de.teamlapen.vampirism.api.VampirismTags;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;

import java.util.Set;

public class VampireRefinementSet extends RefinementSet {
    public VampireRefinementSet(Rarity rarity, int color, Set<Holder<IRefinement>> refinements) {
        super(rarity, color, refinements);
    }

    @SafeVarargs
    public VampireRefinementSet(Rarity rarity, int color, Holder<IRefinement>... refinements) {
        super(rarity, color, refinements);
    }

    @Override
    public TagKey<IFaction<?>> getFaction() {
        return VampirismTags.Factions.IS_VAMPIRE;
    }
}
