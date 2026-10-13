package de.teamlapen.faction.common.core;

import de.teamlapen.faction.api.util.FIdentifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public class FactionLootTables {

    // Minion collect resource tasks
    public static final ResourceKey<LootTable> MINION_COLLECT_RESOURCES = register("minion_tasks/collect_resources");
    public static final ResourceKey<LootTable> MINION_COLLECT_WOOD = register("minion_tasks/collect_wood");
    public static final ResourceKey<LootTable> MINION_COLLECT_ORES = register("minion_tasks/collect_ores");
    public static final ResourceKey<LootTable> MINION_COLLECT_FISH = register("minion_tasks/collect_fish");
    public static final ResourceKey<LootTable> MINION_COLLECT_PLANTS = register("minion_tasks/collect_plants");
    public static final ResourceKey<LootTable> MINION_COLLECT_MOB_LOOT = register("minion_tasks/collect_mob_loot");

    private static ResourceKey<LootTable> register(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE, FIdentifier.mod(path));
    }
}
