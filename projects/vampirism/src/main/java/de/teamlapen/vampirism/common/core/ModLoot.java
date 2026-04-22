package de.teamlapen.vampirism.common.core;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.common.world.items.loot.BiomeMapFunction;
import de.teamlapen.vampirism.data.loot.conditions.*;
import de.teamlapen.vampirism.data.loot.functions.*;
import de.teamlapen.vampirism.data.loot.modifiers.SmeltItemLootModifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;


public class ModLoot {
    public static final DeferredRegister<MapCodec<? extends LootItemFunction>> LOOT_FUNCTION_TYPES = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, REFERENCE.MODID);
    public static final DeferredRegister<MapCodec<? extends LootItemCondition>> LOOT_CONDITION_TYPES = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, REFERENCE.MODID);
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIER = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, REFERENCE.MODID);

    public static final DeferredHolder<MapCodec<? extends LootItemFunction>, MapCodec<SetVampireBookFunction>> SET_VAMPIRE_BOOK = LOOT_FUNCTION_TYPES.register("set_vampire_book", () -> SetVampireBookFunction.CODEC);
    public static final DeferredHolder<MapCodec<? extends LootItemFunction>, MapCodec<SetItemBloodChargeFunction>> SET_ITEM_BLOOD_CHARGE = LOOT_FUNCTION_TYPES.register("set_item_blood_charge", () -> SetItemBloodChargeFunction.CODEC);
    public static final DeferredHolder<MapCodec<? extends LootItemFunction>, MapCodec<RefinementSetFunction>> ADD_REFINEMENT_SET = LOOT_FUNCTION_TYPES.register("add_refinement_set", () -> RefinementSetFunction.CODEC);
    public static final DeferredHolder<MapCodec<? extends LootItemFunction>, MapCodec<SetOilFunction>> SET_OIL = LOOT_FUNCTION_TYPES.register("set_oil", () -> SetOilFunction.CODEC);
    public static final DeferredHolder<MapCodec<? extends LootItemFunction>, MapCodec<SetBloodFunction>> SET_BLOOD = LOOT_FUNCTION_TYPES.register("set_blood", () -> SetBloodFunction.CODEC);
    public static final DeferredHolder<MapCodec<? extends LootItemFunction>, MapCodec<BiomeMapFunction>> BIOME_MAP = LOOT_FUNCTION_TYPES.register("biome_map", () -> BiomeMapFunction.MAP_CODEC);

    public static final DeferredHolder<MapCodec<? extends LootItemCondition>, MapCodec<StakeCondition>> WITH_STAKE = LOOT_CONDITION_TYPES.register("with_stake", () -> StakeCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends LootItemCondition>, MapCodec<AdjustableLevelCondition>> ADJUSTABLE_LEVEL = LOOT_CONDITION_TYPES.register("adjustable_level", () -> AdjustableLevelCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends LootItemCondition>, MapCodec<TentSpawnerCondition>> IS_TENT_SPAWNER = LOOT_CONDITION_TYPES.register("is_tent_spawner", () -> TentSpawnerCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends LootItemCondition>, MapCodec<OilItemCondition>> WITH_OIL_ITEM = LOOT_CONDITION_TYPES.register("with_oil_item", () -> OilItemCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends LootItemCondition>, MapCodec<FactionCondition>> FACTION = LOOT_CONDITION_TYPES.register("faction", () -> FactionCondition.CODEC);

    /**
     * Global loot modifier {@see src/main/resource/data/vampirism/loot_modifiers/smelting.json
     */
    @SuppressWarnings("unused")
    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<SmeltItemLootModifier>> SMELTING = GLOBAL_LOOT_MODIFIER.register("smelting", () -> SmeltItemLootModifier.CODEC);

    static void register(IEventBus bus) {
        LOOT_FUNCTION_TYPES.register(bus);
        LOOT_CONDITION_TYPES.register(bus);
        GLOBAL_LOOT_MODIFIER.register(bus);
    }
}
