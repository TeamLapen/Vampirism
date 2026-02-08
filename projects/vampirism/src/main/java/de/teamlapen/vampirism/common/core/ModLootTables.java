package de.teamlapen.vampirism.common.core;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Set;

public class ModLootTables {
    private static final Set<ResourceKey<LootTable>> LOOT_TABLES = Sets.newHashSet();

    // Chests
    public static final ResourceKey<LootTable> CHEST_HUNTER_TRAINER = register("chests/village/hunter_trainer");
    public static final ResourceKey<LootTable> CHEST_VAMPIRE_DUNGEON = register("chests/dungeon/vampire_dungeon");
    public static final ResourceKey<LootTable> CHEST_VAMPIRE_HUT = register("chests/vampire_hut");
    public static final ResourceKey<LootTable> CHEST_VAMPIRE_ALTAR = register("chests/vampire_altar");
    public static final ResourceKey<LootTable> CHEST_CRYPT = register("chests/crypt");
    public static final ResourceKey<LootTable> CHEST_HUNTER_OUTPOST_SMITH = register("chests/hunter_outpost_smith");
    public static final ResourceKey<LootTable> CHEST_HUNTER_OUTPOST_TENT = register("chests/hunter_outpost_tent");
    public static final ResourceKey<LootTable> CHEST_HUNTER_OUTPOST_ALCHEMY = register("chests/hunter_outpost_alchemy");
    public static final ResourceKey<LootTable> CHEST_HUNTER_OUTPOST_TOWER_FOOD = register("chests/hunter_outpost_tower_food");
    public static final ResourceKey<LootTable> CHEST_HUNTER_OUTPOST_TOWER_BASIC = register("chests/hunter_outpost_tower_basic");
    public static final ResourceKey<LootTable> CHEST_HUNTER_OUTPOST_TOWER_SPECIAL = register("chests/hunter_outpost_tower_special");
    public static final ResourceKey<LootTable> DRACULA_CASTLE = register("chests/dracula_castle");
    public static final ResourceKey<LootTable> DRACULA_CASTLE_LIBRARY = register("chests/dracula_castle/library");
    public static final ResourceKey<LootTable> DRACULA_CASTLE_KITCHEN = register("chests/dracula_castle/kitchen");
    public static final ResourceKey<LootTable> DRACULA_CASTLE_KITCHEN_BARREL = register("chests/dracula_castle/kitchen_barrel");
    public static final ResourceKey<LootTable> DRACULA_CASTLE_DUNGEON = register("chests/dracula_castle/dungeon");
    public static final ResourceKey<LootTable> DRACULA_CASTLE_PANTRY = register("chests/dracula_castle/pantry");
    public static final ResourceKey<LootTable> DRACULA_CASTLE_BEDROOM = register("chests/dracula_castle/bedroom");
    public static final ResourceKey<LootTable> DRACULA_CASTLE_GUARDTOWER = register("chests/dracula_castle/guardtower");
    public static final ResourceKey<LootTable> DRACULA_CASTLE_HUNTERCAMP = register("chests/dracula_castle/huntercamp");
    public static final ResourceKey<LootTable> DRACULA_CAVE_CHEST = register("chests/dracula_cave/chest");
    public static final ResourceKey<LootTable> DRACULA_CAVE_BARREL = register("chests/dracula_cave/barrel");

    // Injects
    public static final ResourceKey<LootTable> INJECT_ABANDONED_MINESHAFT = register("chests/inject/abandoned_mineshaft");
    public static final ResourceKey<LootTable> INJECT_JUNGLE_TEMPLE = register("chests/inject/jungle_temple");
    public static final ResourceKey<LootTable> INJECT_STRONGHOLD_CORRIDOR = register("chests/inject/stronghold_corridor");
    public static final ResourceKey<LootTable> INJECT_DESERT_PYRAMID = register("chests/inject/desert_pyramid");
    public static final ResourceKey<LootTable> INJECT_STRONGHOLD_LIBRARY = register("chests/inject/stronghold_library");

    private static ResourceKey<LootTable> register(String resourceName) {
        return register(VIdentifier.mod(resourceName));
    }

    private static ResourceKey<LootTable> register(Identifier resourceLocation) {
        ResourceKey<LootTable> key = ResourceKey.create(Registries.LOOT_TABLE, resourceLocation);
        LOOT_TABLES.add(key);
        return key;
    }

    public static Set<ResourceKey<LootTable>> getLootTables() {
        return ImmutableSet.copyOf(LOOT_TABLES);
    }
}