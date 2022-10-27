package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.world.loot.*;
import de.teamlapen.vampirism.world.loot.conditions.FactionCondition;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.registries.IForgeRegistry;


//TODO 1.17 adapt if ForgeRegistries for LootFunctionType and LootConditionType have been implemented
public class ModLoot {

    public static LootFunctionType add_book_nbt;
    public static LootFunctionType set_item_blood_charge;
    public static LootFunctionType set_meta_from_level;
    public static LootFunctionType add_refinement_set;

    public static LootConditionType with_stake;
    public static LootConditionType adjustable_level;
    public static LootConditionType is_tent_spawner;
    public static LootConditionType with_oil_item;
    public static LootConditionType faction;

    public static GlobalLootModifierSerializer<?> smelting;

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
        with_oil_item = Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation(REFERENCE.MODID, "with_oil_item"), new LootConditionType(new OilItemCondition.Serializer()));
        faction = Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation(REFERENCE.MODID, "faction"), new LootConditionType(new FactionCondition.Serializer()));
    }

    static void registerLootModifier(IForgeRegistry<GlobalLootModifierSerializer<?>> event) {
        event.register(smelting = new SmeltItemLootModifier.Serializer().setRegistryName(REFERENCE.MODID, "smelting"));
    }
}
