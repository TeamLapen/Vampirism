package de.teamlapen.vampirism.world.loot;

import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

/**
 * Handles loading mod loot tables as well as injecting pools into vanilla tables
 * Inspired (or almost copied) from @williewillus LootHandler for vazkii's Botania
 * https://github.com/williewillus/Botania/blob/07f68b37da9ad3a246b95c042cd6c10bd91698d1/src/main/java/vazkii/botania/common/core/loot/LootHandler.java
 */
public class LootHandler {

    private static final List<String> INJECTION_TABLES = ImmutableList.of(
            "inject/abandoned_mineshaft", "inject/jungle_temple", "inject/stronghold_corridor", "inject/desert_pyramid", "inject/stronghold_library");
    private static final List<String> TABLES = ImmutableList.of("vampire_dungeon", "village_trainer");

    private static final LootHandler instance = new LootHandler();

    public static LootHandler getInstance() {
        return instance;
    }

    private int injected = 0;

    private LootHandler() {
        for (String s : INJECTION_TABLES) {
            LootTableList.register(new ResourceLocation(REFERENCE.MODID, s));
        }
        for (String s : TABLES) {
            LootTableList.register(new ResourceLocation(REFERENCE.MODID, s));
        }
        LootFunctionManager.registerFunction(new AddBookNbt.Serializer());
        LootFunctionManager.registerFunction(new SetItemTier.Serializer());
    }

    public boolean checkAndResetInsertedAll() {
        int i = injected;
        injected = 0;
        return i == INJECTION_TABLES.size();
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

    private LootEntryTable getInjectEntry(String name, int weight) {
        return new LootEntryTable(new ResourceLocation(REFERENCE.MODID, "inject/" + name), weight, 0, new LootCondition[0], "vampirism_inject_entry");
    }

    private LootPool getInjectPool(String entryName) {
        return new LootPool(new LootEntry[]{getInjectEntry(entryName, 1)}, new LootCondition[0], new RandomValueRange(1), new RandomValueRange(0, 1), "vampirism_inject_pool");
    }
}

