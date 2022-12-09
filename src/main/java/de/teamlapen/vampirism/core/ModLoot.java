package de.teamlapen.vampirism.core;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.world.loot.SmeltItemLootModifier;
import de.teamlapen.vampirism.world.loot.conditions.*;
import de.teamlapen.vampirism.world.loot.functions.AddBookNbtFunction;
import de.teamlapen.vampirism.world.loot.functions.RefinementSetFunction;
import de.teamlapen.vampirism.world.loot.functions.SetItemBloodChargeFunction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class ModLoot {
    public static final DeferredRegister<LootItemFunctionType> LOOT_FUNCTION_TYPES = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, REFERENCE.MODID);
    public static final DeferredRegister<LootItemConditionType> LOOT_CONDITION_TYPES = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, REFERENCE.MODID);
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIER = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, REFERENCE.MODID);

    public static final LootItemFunctionType ADD_BOOK_NBT = new LootItemFunctionType(new AddBookNbtFunction.Serializer());
    public static final LootItemFunctionType SET_ITEM_BLOOD_CHARGE = new LootItemFunctionType(new SetItemBloodChargeFunction.Serializer());
    public static final LootItemFunctionType ADD_REFINEMENT_SET = new LootItemFunctionType(new RefinementSetFunction.Serializer());

    public static final LootItemConditionType WITH_STAKE = new LootItemConditionType(new StakeCondition.Serializer());
    public static final LootItemConditionType ADJUSTABLE_LEVEL = new LootItemConditionType(new AdjustableLevelCondition.Serializer());
    public static final LootItemConditionType IS_TENT_SPAWNER = new LootItemConditionType(new TentSpawnerCondition.Serializer());
    public static final LootItemConditionType WITH_OIL_ITEM = new LootItemConditionType(new OilItemCondition.Serializer());
    public static final LootItemConditionType FACTION = new LootItemConditionType(new FactionCondition.Serializer());

    public static final RegistryObject<Codec<SmeltItemLootModifier>> SMELTING = GLOBAL_LOOT_MODIFIER.register("smelting", () -> SmeltItemLootModifier.CODEC);

    public static void register(IEventBus bus) {
        LOOT_FUNCTION_TYPES.register(bus);
        LOOT_CONDITION_TYPES.register(bus);
        GLOBAL_LOOT_MODIFIER.register(bus);
    }

    public static <T> void registerLootFunctionTypes(Registry<LootItemFunctionType> vanillaRegistry) {
        Registry.register(vanillaRegistry, "vampirism:add_book_nbt", ADD_BOOK_NBT);
        Registry.register(vanillaRegistry, "vampirism:set_item_blood_charge", SET_ITEM_BLOOD_CHARGE);
        Registry.register(vanillaRegistry, "vampirism:add_refinement_set", ADD_REFINEMENT_SET);
    }

    public static <T> void registerLootConditionsTypes(Registry<LootItemConditionType> vanillaRegistry) {
        Registry.register(vanillaRegistry, "vampirism:with_stake", WITH_STAKE);
        Registry.register(vanillaRegistry, "vampirism:with_stake", ADJUSTABLE_LEVEL);
        Registry.register(vanillaRegistry, "vampirism:is_tent_spawner", IS_TENT_SPAWNER);
        Registry.register(vanillaRegistry, "vampirism:with_oil_item", WITH_OIL_ITEM);
        Registry.register(vanillaRegistry, "vampirism:faction", FACTION);
    }
}
