package de.teamlapen.vampirism.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.world.loot.AddBookNbt;
import de.teamlapen.vampirism.world.loot.AdjustableLevelCondition;
import de.teamlapen.vampirism.world.loot.SetItemBloodCharge;
import de.teamlapen.vampirism.world.loot.SetMetaBasedOnLevel;
import de.teamlapen.vampirism.world.loot.StakeCondition;
import de.teamlapen.vampirism.world.loot.TentSpawnerCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.TableLootEntry;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

/**
 * Handles loading mod loot tables as well as injecting pools into vanilla tables
 * Inspired (or almost copied) from @williewillus LootHandler for vazkii's Botania
 * https://github.com/williewillus/Botania/blob/07f68b37da9ad3a246b95c042cd6c10bd91698d1/src/main/java/vazkii/botania/common/core/loot/LootHandler.java
 */
public class ModLootTables {
    public static final ResourceLocation vampire = register("entities/" + ModEntities.vampire.getRegistryName().getPath());
    public static final ResourceLocation hunter = register("entities/" + ModEntities.hunter.getRegistryName().getPath());
    public static final ResourceLocation advanced_vampire = register("entities/" + ModEntities.advanced_vampire.getRegistryName().getPath());
    public static final ResourceLocation advanced_hunter = register("entities/" + ModEntities.advanced_hunter.getRegistryName().getPath());
    public static final ResourceLocation chest_hunter_trainer = register("chests/hunter_trainer");

    private static final List<String> INJECTION_TABLES = ImmutableList.of("abandoned_mineshaft", "jungle_temple", "stronghold_corridor", "desert_pyramid", "stronghold_library");
    private static final List<String> STRUCTURE_TABLES = Lists.newArrayList();
    private static int injected = 0;

    static {
        INJECTION_TABLES.forEach(table -> LootTables.register(new ResourceLocation(REFERENCE.MODID, "inject/" + table)));
    }

    static ResourceLocation register(String resourceName){
        return LootTables.register(new ResourceLocation(REFERENCE.MODID, resourceName));
    }

    static ResourceLocation register(ResourceLocation resourceLocation) {
        return LootTables.register(resourceLocation);
    }

    static void registerLootFunctions() {
        LootFunctionManager.registerFunction(new AddBookNbt.Serializer());
        LootFunctionManager.registerFunction(new SetItemBloodCharge.Serializer());
        LootFunctionManager.registerFunction(new SetMetaBasedOnLevel.Serializer());
        LootConditionManager.registerCondition(new StakeCondition.Serializer());
        LootConditionManager.registerCondition(new AdjustableLevelCondition.Serializer());
        LootConditionManager.registerCondition(new TentSpawnerCondition.Serializer());
    }

    @SubscribeEvent
    public static void onLootLoad(LootTableLoadEvent event) {
        String prefix = "minecraft:chests/";
        String name = event.getName().toString();
        if (name.startsWith(prefix)) {
            String file = name.substring(name.indexOf(prefix) + prefix.length());
            if(INJECTION_TABLES.contains(file)){
                event.getTable().addPool(getInjectPool(file));
                injected++;
            }
        }
    }

    private static LootPool getInjectPool(String entryName) {
        LootEntry.Builder<?> entryBuilder = TableLootEntry.builder(new ResourceLocation(REFERENCE.MODID, "inject/" + entryName)).weight(1);
        return LootPool.builder().name("vampirism_inject_pool").bonusRolls(0, 1).rolls(new RandomValueRange(1)).addEntry(entryBuilder).build();
    }

    public static boolean checkAndResetInsertedAll() {
        int i = injected;
        injected = 0;
        return i >= INJECTION_TABLES.size(); //Sponge loads the loot tables for all worlds at start. Which makes this test not work anyway.
    }

    /**
     * Add a loot structure loot table to the list
     *
     * @param name
     */
    public static ResourceLocation addStructureLootTable(String name) {
        String rs_id = "structure/" + name;
        STRUCTURE_TABLES.add(rs_id);
        ResourceLocation id = new ResourceLocation(rs_id);
        LootTables.register(id);
        return id;
    }
}