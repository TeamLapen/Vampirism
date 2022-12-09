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

    public static final RegistryObject<LootItemFunctionType> ADD_BOOK_NBT = LOOT_FUNCTION_TYPES.register("add_book_nbt", () -> new LootItemFunctionType(new AddBookNbtFunction.Serializer()));
    public static final RegistryObject<LootItemFunctionType> SET_ITEM_BLOOD_CHARGE = LOOT_FUNCTION_TYPES.register("set_item_blood_charge", () -> new LootItemFunctionType(new SetItemBloodChargeFunction.Serializer()));
    public static final RegistryObject<LootItemFunctionType> ADD_REFINEMENT_SET = LOOT_FUNCTION_TYPES.register("add_refinement_set", () -> new LootItemFunctionType(new RefinementSetFunction.Serializer()));

    public static final RegistryObject<LootItemConditionType> WITH_STAKE = LOOT_CONDITION_TYPES.register("with_stake", () -> new LootItemConditionType(new StakeCondition.Serializer()));
    public static final RegistryObject<LootItemConditionType> ADJUSTABLE_LEVEL = LOOT_CONDITION_TYPES.register("adjustable_level", () -> new LootItemConditionType(new AdjustableLevelCondition.Serializer()));
    public static final RegistryObject<LootItemConditionType> IS_TENT_SPAWNER = LOOT_CONDITION_TYPES.register("is_tent_spawner", () -> new LootItemConditionType(new TentSpawnerCondition.Serializer()));
    public static final RegistryObject<LootItemConditionType> WITH_OIL_ITEM = LOOT_CONDITION_TYPES.register("with_oil_item", () -> new LootItemConditionType(new OilItemCondition.Serializer()));
    public static final RegistryObject<LootItemConditionType> FACTION = LOOT_CONDITION_TYPES.register("faction", () -> new LootItemConditionType(new FactionCondition.Serializer()));

    public static final RegistryObject<Codec<SmeltItemLootModifier>> SMELTING = GLOBAL_LOOT_MODIFIER.register("smelting", () -> SmeltItemLootModifier.CODEC);

    public static void register(IEventBus bus) {
        LOOT_FUNCTION_TYPES.register(bus);
        LOOT_CONDITION_TYPES.register(bus);
        GLOBAL_LOOT_MODIFIER.register(bus);
    }
}
