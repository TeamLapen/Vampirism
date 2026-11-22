package de.teamlapen.faction.api.tags;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.refinements.IRefinementSet;
import de.teamlapen.faction.api.factions.refinements.Rarity;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.api.world.items.AccessorySlotType;
import net.minecraft.tags.TagKey;

/**
 * Tags that define properties of {@link IRefinementSet}s.
 * <p>
 * The faction of a refinement set is defined by the faction specific tag for {@link FactionRegistries.Keys#REFINEMENT_SET}
 * (see {@link de.teamlapen.faction.api.factions.IFactionSpecificTags})
 */
public class FactionRefinementSetTags {

    /**
     * Rarity tags. A refinement set without rarity tag is {@link de.teamlapen.faction.api.factions.refinements.Rarity#COMMON}. If a set is in multiple rarity tags the highest rarity is used.
     */
    public static final TagKey<IRefinementSet> COMMON = tag("rarity/common");
    public static final TagKey<IRefinementSet> UNCOMMON = tag("rarity/uncommon");
    public static final TagKey<IRefinementSet> RARE = tag("rarity/rare");
    public static final TagKey<IRefinementSet> EPIC = tag("rarity/epic");
    public static final TagKey<IRefinementSet> LEGENDARY = tag("rarity/legendary");

    /**
     * Slot tags. A refinement set that is in no slot tag can be applied to all slots, otherwise only to the slots whose tag contains the set.
     */
    public static final TagKey<IRefinementSet> AMULET = tag("slot/amulet");
    public static final TagKey<IRefinementSet> RING = tag("slot/ring");
    public static final TagKey<IRefinementSet> OBI_BELT = tag("slot/obi_belt");

    private static TagKey<IRefinementSet> tag(String name) {
        return TagKey.create(FactionRegistries.Keys.REFINEMENT_SET, FIdentifier.mod(name));
    }
}
