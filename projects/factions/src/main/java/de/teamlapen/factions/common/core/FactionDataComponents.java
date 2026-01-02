package de.teamlapen.factions.common.core;

import de.teamlapen.factions.api.util.REFERENCE;
import de.teamlapen.factions.common.components.EffectiveRefinementSet;
import de.teamlapen.factions.common.components.FactionRestriction;
import de.teamlapen.factions.common.components.FactionSlayer;
import de.teamlapen.factions.common.util.BlockDescription;
import de.teamlapen.factions.common.util.ShiftDescription;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FactionDataComponents {

    public static final DeferredRegister.DataComponents ITEM_DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, REFERENCE.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FactionRestriction>> FACTION_RESTRICTION = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.factions.api.FactionDataComponents.Keys.FACTION_RESTRICTION.getPath(), builder -> builder.persistent(FactionRestriction.CODEC).networkSynchronized(FactionRestriction.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FactionSlayer>>  FACTION_SLAYER = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.factions.api.FactionDataComponents.Keys.FACTION_SLAYER.getPath(), builder -> builder.persistent(FactionSlayer.CODEC).networkSynchronized(FactionSlayer.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> IS_FACTION_BANNER = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.factions.api.FactionDataComponents.Keys.IS_FACTION_BANNER.getPath(), builder -> builder.persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EffectiveRefinementSet>> REFINEMENT_SET = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.factions.api.FactionDataComponents.Keys.REFINEMENT_SET.getPath(), builder -> builder.persistent(EffectiveRefinementSet.CODEC).networkSynchronized(EffectiveRefinementSet.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ShiftDescription>> SHIFT_DESCRIPTION = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.factions.api.FactionDataComponents.Keys.SHIFT_DESCRIPTION.getPath(), builder -> builder.persistent(ShiftDescription.CODEC).networkSynchronized(ShiftDescription.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BlockDescription>> BLOCK_DESCRIPTION = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.factions.api.FactionDataComponents.Keys.BLOCK_DESCRIPTION.getPath(), builder -> builder.persistent(BlockDescription.CODEC).networkSynchronized(BlockDescription.STREAM_CODEC));



    static void register(IEventBus eventBus) {
        ITEM_DATA_COMPONENTS.register(eventBus);
    }
}
