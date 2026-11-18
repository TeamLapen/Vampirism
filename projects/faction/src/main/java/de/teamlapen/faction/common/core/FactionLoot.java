package de.teamlapen.faction.common.core;

import com.mojang.serialization.MapCodec;
import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.data.loot.conditions.AdjustableLevelCondition;
import de.teamlapen.faction.data.loot.conditions.FactionCondition;
import de.teamlapen.faction.data.loot.functions.RefinementSetFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FactionLoot {
    public static final DeferredRegister<MapCodec<? extends LootItemFunction>> LOOT_FUNCTION_TYPES = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, REFERENCE.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends LootItemCondition>> LOOT_CONDITION_TYPES = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, REFERENCE.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends LootItemFunction>, MapCodec<RefinementSetFunction>> ADD_REFINEMENT_SET = LOOT_FUNCTION_TYPES.register("add_refinement_set", () -> RefinementSetFunction.CODEC);
    public static final DeferredHolder<MapCodec<? extends LootItemCondition>, MapCodec<AdjustableLevelCondition>> ADJUSTABLE_LEVEL = LOOT_CONDITION_TYPES.register("adjustable_level", () -> AdjustableLevelCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends LootItemCondition>, MapCodec<FactionCondition>> FACTION = LOOT_CONDITION_TYPES.register("faction", () -> FactionCondition.CODEC);

    static void register(IEventBus bus) {
        LOOT_FUNCTION_TYPES.register(bus);
        LOOT_CONDITION_TYPES.register(bus);
    }
}
