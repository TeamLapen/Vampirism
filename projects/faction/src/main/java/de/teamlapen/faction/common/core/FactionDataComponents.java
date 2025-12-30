package de.teamlapen.faction.common.core;

import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.common.components.EffectiveRefinementSet;
import de.teamlapen.faction.common.components.FactionRestriction;
import de.teamlapen.faction.common.components.FactionSlayer;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FactionDataComponents {

    public static final DeferredRegister.DataComponents ITEM_DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, REFERENCE.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FactionRestriction>> FACTION_RESTRICTION = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.faction.api.FactionDataComponents.Keys.FACTION_RESTRICTION.getPath(), builder -> builder.persistent(FactionRestriction.CODEC).networkSynchronized(FactionRestriction.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FactionSlayer>>  FACTION_SLAYER = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.faction.api.FactionDataComponents.Keys.FACTION_SLAYER.getPath(), builder -> builder.persistent(FactionSlayer.CODEC).networkSynchronized(FactionSlayer.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> IS_FACTION_BANNER = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.faction.api.FactionDataComponents.Keys.IS_FACTION_BANNER.getPath(), builder -> builder.persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EffectiveRefinementSet>> REFINEMENT_SET = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.faction.api.FactionDataComponents.Keys.REFINEMENT_SET.getPath(), builder -> builder.persistent(EffectiveRefinementSet.CODEC).networkSynchronized(EffectiveRefinementSet.STREAM_CODEC));



    static void register(IEventBus eventBus) {
        ITEM_DATA_COMPONENTS.register(eventBus);
    }
}
