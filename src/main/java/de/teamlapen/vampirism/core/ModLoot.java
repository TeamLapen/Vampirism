package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.world.loot.*;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;


//@ObjectHolder(REFERENCE.MODID) TODO 1.17 adapt if ForgeRegistries for LootFunctionType and LootConditionType have been implemented
public class ModLoot {

    public static LootFunctionType add_book_nbt;
    public static LootFunctionType set_item_blood_charge;
    public static LootFunctionType set_meta_from_level;
    public static LootFunctionType add_refinement_set;

    public static LootConditionType with_stake;
    public static LootConditionType adjustable_level;
    public static LootConditionType is_tent_spawner;

    static void registerLootFunctionType() {
        add_book_nbt = Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(REFERENCE.MODID, "add_book_nbt"), new LootFunctionType(new AddBookNbt.Serializer()));
        set_item_blood_charge = Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(REFERENCE.MODID, "set_item_blood_charge"), new LootFunctionType(new SetItemBloodCharge.Serializer()));
        set_meta_from_level = Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(REFERENCE.MODID, "set_meta_from_level"), new LootFunctionType(new SetMetaBasedOnLevel.Serializer()));
        add_refinement_set = Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(REFERENCE.MODID, "add_refinement_set"), new LootFunctionType(new RefinementSetFunction.Serializer()));
    }

    static void registerLootConditions() {
        with_stake = Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation(REFERENCE.MODID, "with_stake"), new LootConditionType(new StakeCondition.Serializer()));
        adjustable_level = Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation(REFERENCE.MODID, "adjustable_level"), new LootConditionType(new AdjustableLevelCondition.Serializer()));
        is_tent_spawner = Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation(REFERENCE.MODID, "is_tent_spawner"), new LootConditionType(new TentSpawnerCondition.Serializer()));
    }
}
