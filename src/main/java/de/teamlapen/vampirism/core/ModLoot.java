package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.world.loot.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;


//@ObjectHolder(REFERENCE.MODID) TODO 1.18 adapt if ForgeRegistries for LootFunctionType and LootConditionType have been implemented
public class ModLoot {

    public static LootItemFunctionType add_book_nbt;
    public static LootItemFunctionType set_item_blood_charge;
    public static LootItemFunctionType add_refinement_set;

    public static LootItemConditionType with_stake;
    public static LootItemConditionType adjustable_level;
    public static LootItemConditionType is_tent_spawner;

    static void registerLootFunctionType() {
        add_book_nbt = Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(REFERENCE.MODID, "add_book_nbt"), new LootItemFunctionType(new AddBookNbt.Serializer()));
        set_item_blood_charge = Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(REFERENCE.MODID, "set_item_blood_charge"), new LootItemFunctionType(new SetItemBloodCharge.Serializer()));
        add_refinement_set = Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(REFERENCE.MODID, "add_refinement_set"), new LootItemFunctionType(new RefinementSetFunction.Serializer()));
    }

    static void registerLootConditions() {
        with_stake = Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation(REFERENCE.MODID, "with_stake"), new LootItemConditionType(new StakeCondition.Serializer()));
        adjustable_level = Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation(REFERENCE.MODID, "adjustable_level"), new LootItemConditionType(new AdjustableLevelCondition.Serializer()));
        is_tent_spawner = Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation(REFERENCE.MODID, "is_tent_spawner"), new LootItemConditionType(new TentSpawnerCondition.Serializer()));
    }
}
