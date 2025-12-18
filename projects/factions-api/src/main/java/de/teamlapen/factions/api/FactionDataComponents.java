package de.teamlapen.factions.api;

import de.teamlapen.factions.api.util.FResourceLocation;
import de.teamlapen.factions.api.world.items.components.IEffectiveRefinementSet;
import de.teamlapen.factions.api.world.items.components.IFactionRestriction;
import de.teamlapen.factions.api.world.items.components.IFactionSlayer;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.neoforged.neoforge.registries.DeferredHolder;

import static de.teamlapen.factions.api.registries.ApiRegistryProvider.retrieveDataComponent;

@SuppressWarnings("unused")
public class FactionDataComponents {

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<IEffectiveRefinementSet>> REFINEMENT_SET = retrieveDataComponent(Keys.REFINEMENT_SET);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<IFactionSlayer>> FACTION_SLAYER = retrieveDataComponent(Keys.FACTION_SLAYER);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> IS_FACTION_BANNER = retrieveDataComponent(Keys.IS_FACTION_BANNER);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<IFactionRestriction>> FACTION_RESTRICTION = retrieveDataComponent(Keys.FACTION_RESTRICTION);

    public static class Keys {

        public static final ResourceLocation REFINEMENT_SET = FResourceLocation.mod("refinement_set");
        public static final ResourceLocation IS_FACTION_BANNER = FResourceLocation.mod("is_faction_banner");
        public static final ResourceLocation FACTION_RESTRICTION = FResourceLocation.mod("faction_restriction");
        public static final ResourceLocation FACTION_SLAYER = FResourceLocation.mod("faction_slayer");

    }
}
