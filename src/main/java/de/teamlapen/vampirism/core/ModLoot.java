package de.teamlapen.vampirism.core;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.world.loot.*;
import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class ModLoot {
    public static final DeferredRegister<LootItemFunctionType> LOOT_FUNCTION_TYPES = DeferredRegister.create(Registry.LOOT_FUNCTION_REGISTRY, REFERENCE.MODID);
    public static final DeferredRegister<LootItemConditionType> LOOT_CONDITION_TYPES = DeferredRegister.create(Registry.LOOT_ITEM_REGISTRY, REFERENCE.MODID);
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIER = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, REFERENCE.MODID);

    public static final RegistryObject<LootItemFunctionType> add_book_nbt = LOOT_FUNCTION_TYPES.register("add_book_nbt", () -> new LootItemFunctionType(new AddBookNbt.Serializer()));
    public static final RegistryObject<LootItemFunctionType> set_item_blood_charge = LOOT_FUNCTION_TYPES.register("set_item_blood_charge", () -> new LootItemFunctionType(new SetItemBloodCharge.Serializer()));
    public static final RegistryObject<LootItemFunctionType> add_refinement_set = LOOT_FUNCTION_TYPES.register("add_refinement_set", () -> new LootItemFunctionType(new RefinementSetFunction.Serializer()));

    public static final RegistryObject<LootItemConditionType> with_stake = LOOT_CONDITION_TYPES.register("with_stake", () -> new LootItemConditionType(new StakeCondition.Serializer()));
    public static final RegistryObject<LootItemConditionType> adjustable_level = LOOT_CONDITION_TYPES.register("adjustable_level", () -> new LootItemConditionType(new AdjustableLevelCondition.Serializer()));
    public static final RegistryObject<LootItemConditionType> is_tent_spawner = LOOT_CONDITION_TYPES.register("is_tent_spawner", () -> new LootItemConditionType(new TentSpawnerCondition.Serializer()));
    public static final RegistryObject<LootItemConditionType> with_oil_item = LOOT_CONDITION_TYPES.register("with_oil_item", () -> new LootItemConditionType(new OilItemCondition.Serializer()));

    public static final RegistryObject<Codec<SmeltItemLootModifier>> smelting = GLOBAL_LOOT_MODIFIER.register("smelting", () -> SmeltItemLootModifier.CODEC);


    public static void register(IEventBus bus) {
        LOOT_FUNCTION_TYPES.register(bus);
        LOOT_CONDITION_TYPES.register(bus);
        GLOBAL_LOOT_MODIFIER.register(bus);
    }
}
