package de.teamlapen.faction.api;

import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.api.world.items.components.IEffectiveRefinementSet;
import de.teamlapen.faction.api.world.items.components.IFactionRestriction;
import de.teamlapen.faction.api.world.items.components.IFactionSlayer;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.neoforged.neoforge.registries.DeferredHolder;

import static de.teamlapen.faction.api.registries.ApiRegistryProvider.retrieveDataComponent;

@SuppressWarnings("unused")
public class FactionDataComponents {

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<IEffectiveRefinementSet>> REFINEMENT_SET = retrieveDataComponent(Keys.REFINEMENT_SET);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<IFactionSlayer>> FACTION_SLAYER = retrieveDataComponent(Keys.FACTION_SLAYER);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> IS_FACTION_BANNER = retrieveDataComponent(Keys.IS_FACTION_BANNER);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<IFactionRestriction>> FACTION_RESTRICTION = retrieveDataComponent(Keys.FACTION_RESTRICTION);

    public static class Keys {

        public static final Identifier REFINEMENT_SET = FIdentifier.mod("refinement_set");
        public static final Identifier IS_FACTION_BANNER = FIdentifier.mod("is_faction_banner");
        public static final Identifier FACTION_RESTRICTION = FIdentifier.mod("faction_restriction");
        public static final Identifier FACTION_SLAYER = FIdentifier.mod("faction_slayer");
        public static final Identifier SHIFT_DESCRIPTION = FIdentifier.mod("shift_description");
        public static final Identifier BLOCK_DESCRIPTION = FIdentifier.mod("block_description");
        public static final Identifier FACTION_FOOD = FIdentifier.mod("faction_food");
    }
}
