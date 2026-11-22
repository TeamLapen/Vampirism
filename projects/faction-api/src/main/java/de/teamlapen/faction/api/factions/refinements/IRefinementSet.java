package de.teamlapen.faction.api.factions.refinements;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IFactionSpecificTags;
import de.teamlapen.faction.api.tags.FactionRefinementSetTags;
import de.teamlapen.faction.api.world.items.AccessorySlotType;
import de.teamlapen.faction.api.world.items.IRefinementItem;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * A set of refinements that can be applied to a {@link IRefinementItem}
 * <p>
 * Refinement sets are loaded from datapacks. Registry key {@link FactionRegistries.Keys#REFINEMENT_SET}
 * <p>
 * Faction, rarity and allowed slots are defined through tags. See {@link FactionRefinementSetTags}
 */
public interface IRefinementSet {

    static Rarity getRarity(Holder<IRefinementSet> set) {
        Rarity[] values = Rarity.values();
        for (int i = values.length - 1; i >= 0; i--) {
            if (set.is(values[i].tag)) {
                return values[i];
            }
        }
        return Rarity.COMMON;
    }

    static boolean canBeAppliedTo(Holder<IRefinementSet> set, AccessorySlotType slot) {
        boolean restricted = false;
        for (AccessorySlotType type : AccessorySlotType.values()) {
            if (set.is(type.tag)) {
                if (type == slot) {
                    return true;
                }
                restricted = true;
            }
        }
        return !restricted;
    }

    /**
     * @return all accessory slot types the set can be applied to
     */
    static List<AccessorySlotType> getSlotTypes(Holder<IRefinementSet> set) {
        return Arrays.stream(AccessorySlotType.values()).filter(type -> canBeAppliedTo(set, type)).toList();
    }

    /**
     * @return whether the set belongs to the given faction, based on the faction specific refinement set tag
     */
    static boolean isForFaction(Holder<IRefinementSet> set, @Nullable Holder<? extends IFaction<?>> faction) {
        if (faction == null) return false;
        return IFactionSpecificTags.get().get(faction, FactionRegistries.Keys.REFINEMENT_SET).map(set::is).orElse(false);
    }

    /**
     * @return whether the set belongs to any of the given factions
     */
    static <T extends IFaction<?>> boolean isForAnyFaction(Holder<IRefinementSet> set, HolderSet<T> factions) {
        return factions.stream().anyMatch(faction -> isForFaction(set, faction));
    }

    int getColor();

    Component getName();

    HolderSet<IRefinement> getRefinements();

}
