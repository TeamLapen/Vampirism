package de.teamlapen.vampirism.world.loot;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
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
public class LootHandler {

    public static final ResourceLocation STRUCTURE_VAMPIRE_DUNGEON = register("vampire_dungeon");
    public static final ResourceLocation STRUCTURE_VILLAGE_TRAINER = register("village_trainer");
    public static final ResourceLocation BASIC_VAMPIRE = register("entities/basic_vampire");
    public static final ResourceLocation BASIC_HUNTER = register("entities/basic_hunter");
    public static final ResourceLocation ADVANCED_VAMPIRE = register("entities/advanced_vampire");
    public static final ResourceLocation ADVANCED_HUNTER = register("entities/advanced_hunter");
    public static final ResourceLocation VAMPIRE_BARON = register("entities/baron");
    public static final ResourceLocation GHOST = register("entities/ghost");
    private static final List<String> INJECTION_TABLES = ImmutableList.of(
            "inject/abandoned_mineshaft", "inject/jungle_temple", "inject/stronghold_corridor", "inject/desert_pyramid", "inject/stronghold_library");
    private static final List<String> STRUCTURE_TABLES = Lists.newArrayList();
    private static final LootHandler instance = new LootHandler();

    public static LootHandler getInstance() {
        return instance;
    }

    private static ResourceLocation register(String s) {
        ResourceLocation loc = new ResourceLocation(REFERENCE.MODID, s);
        LootTables.register(loc);
        return loc;
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
    private int injected = 0;

    private LootHandler() {
        for (String s : INJECTION_TABLES) {
            LootTables.register(new ResourceLocation(REFERENCE.MODID, s));
        }

        LootFunctionManager.registerFunction(new AddBookNbt.Serializer());
        LootFunctionManager.registerFunction(new SetItemBloodCharge.Serializer());
        LootFunctionManager.registerFunction(new SetMetaBasedOnLevel.Serializer());
        LootConditionManager.registerCondition(new StakeCondition.Serializer());
        LootConditionManager.registerCondition(new AdjustableLevelCondition.Serializer());
    }

    public boolean checkAndResetInsertedAll() {
        int i = injected;
        injected = 0;
        return i >= INJECTION_TABLES.size(); //Sponge loads the loot tables for all worlds at start. Which makes this test not work anyway.
    }

    @SubscribeEvent
    public void onLootLoad(LootTableLoadEvent event) {
        String prefix = "minecraft:chests/";
        String name = event.getName().toString();
        if (name.startsWith(prefix)) {
            String file = name.substring(name.indexOf(prefix) + prefix.length());
            switch (file) {
                case "abandoned_mineshaft":
                case "desert_pyramid":
                case "jungle_temple":
                case "stronghold_corridor":
                case "stronghold_library":
                    event.getTable().addPool(getInjectPool(file));
                    injected++;
                    break;
                default:
                    break;
            }
        }

    }

    private TableLootEntry getInjectEntry(String name, int weight) {
        return new TableLootEntry(new ResourceLocation(REFERENCE.MODID, "inject/" + name), weight, 0, new ILootCondition[0], "vampirism_inject_entry");
    }

    private LootPool getInjectPool(String entryName) {
        return new LootPool(new ILootGenerator[]{getInjectEntry(entryName, 1)}, new ILootCondition[0], new RandomValueRange(1), new RandomValueRange(0, 1), "vampirism_inject_pool");
    }
}

