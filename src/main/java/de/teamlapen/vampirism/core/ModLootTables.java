package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTables;

public class ModLootTables {
    public static final ResourceLocation vampire = LootTables.register(new ResourceLocation(REFERENCE.MODID, "entities/" + ModEntities.vampire.getRegistryName().getPath()));
    public static final ResourceLocation hunter = LootTables.register(new ResourceLocation(REFERENCE.MODID, "entities/" + ModEntities.hunter.getRegistryName().getPath()));
    public static final ResourceLocation advanced_vampire = LootTables.register(new ResourceLocation(REFERENCE.MODID, "entities/" + ModEntities.advanced_vampire.getRegistryName().getPath()));
    public static final ResourceLocation advanced_hunter = LootTables.register(new ResourceLocation(REFERENCE.MODID, "entities/" + ModEntities.advanced_hunter.getRegistryName().getPath()));
}