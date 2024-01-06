package de.teamlapen.vampirism.core;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.world.loot.SmeltItemLootModifier;
import de.teamlapen.vampirism.world.loot.conditions.*;
import de.teamlapen.vampirism.world.loot.functions.AddBookNbtFunction;
import de.teamlapen.vampirism.world.loot.functions.RefinementSetFunction;
import de.teamlapen.vampirism.world.loot.functions.SetItemBloodChargeFunction;
import de.teamlapen.vampirism.world.loot.functions.SetOilFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;


public class ModLoot {
    public static final DeferredRegister<LootItemFunctionType> LOOT_FUNCTION_TYPES = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, REFERENCE.MODID);
    public static final DeferredRegister<LootItemConditionType> LOOT_CONDITION_TYPES = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, REFERENCE.MODID);
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIER = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, REFERENCE.MODID);

    public static final DeferredHolder<LootItemFunctionType, LootItemFunctionType> ADD_BOOK_NBT = LOOT_FUNCTION_TYPES.register("add_book_nbt", () -> new LootItemFunctionType(AddBookNbtFunction.CODEC));
    public static final DeferredHolder<LootItemFunctionType, LootItemFunctionType> SET_ITEM_BLOOD_CHARGE = LOOT_FUNCTION_TYPES.register("set_item_blood_charge", () -> new LootItemFunctionType(SetItemBloodChargeFunction.CODEC));
    public static final DeferredHolder<LootItemFunctionType, LootItemFunctionType> ADD_REFINEMENT_SET = LOOT_FUNCTION_TYPES.register("add_refinement_set", () -> new LootItemFunctionType(RefinementSetFunction.CODEC));
    public static final DeferredHolder<LootItemFunctionType, LootItemFunctionType> SET_OIL = LOOT_FUNCTION_TYPES.register("set_oil", () -> new LootItemFunctionType(SetOilFunction.CODEC));

    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> WITH_STAKE = LOOT_CONDITION_TYPES.register("with_stake", () -> new LootItemConditionType(StakeCondition.CODEC));
    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> ADJUSTABLE_LEVEL = LOOT_CONDITION_TYPES.register("adjustable_level", () -> new LootItemConditionType(AdjustableLevelCondition.CODEC));
    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> IS_TENT_SPAWNER = LOOT_CONDITION_TYPES.register("is_tent_spawner", () -> new LootItemConditionType(TentSpawnerCondition.CODEC));
    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> WITH_OIL_ITEM = LOOT_CONDITION_TYPES.register("with_oil_item", () -> new LootItemConditionType(OilItemCondition.CODEC));
    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> FACTION = LOOT_CONDITION_TYPES.register("faction", () -> new LootItemConditionType(FactionCondition.CODEC));

    public static final DeferredHolder<Codec<? extends IGlobalLootModifier>, Codec<SmeltItemLootModifier>> SMELTING = GLOBAL_LOOT_MODIFIER.register("smelting", () -> SmeltItemLootModifier.CODEC);

    public static void register(IEventBus bus) {
        LOOT_FUNCTION_TYPES.register(bus);
        LOOT_CONDITION_TYPES.register(bus);
        GLOBAL_LOOT_MODIFIER.register(bus);
    }
}
