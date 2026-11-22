package de.teamlapen.vampirism.common.tags;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.refinements.IRefinementSet;
import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.tags.TagKey;

public class ModRefinementSetTags {
    public static final TagKey<IRefinementSet> IS_VAMPIRE = tag("has_faction/vampire");

    private static TagKey<IRefinementSet> tag(String name) {
        return TagKey.create(FactionRegistries.Keys.REFINEMENT_SET, VIdentifier.mod(name));
    }
}
