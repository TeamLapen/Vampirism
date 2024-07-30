package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ModRegistryItems {

    public static final DeferredHolder<Block, Block> DARK_SPRUCE_PLANKS = DeferredHolder.create(ResourceKey.create(Registries.BLOCK, VResourceLocation.mod("dark_spruce_planks")));
    public static final DeferredHolder<Block, Block> CURSED_SPRUCE_PLANKS = DeferredHolder.create(ResourceKey.create(Registries.BLOCK, VResourceLocation.mod("cursed_spruce_planks")));
    public static final DeferredHolder<Item, Item> DARK_SPRUCE_BOAT = DeferredHolder.create(ResourceKey.create(Registries.ITEM, VResourceLocation.mod("dark_spruce_boat")));
    public static final DeferredHolder<Item, Item> CURSED_SPRUCE_BOAT = DeferredHolder.create(ResourceKey.create(Registries.ITEM, VResourceLocation.mod("cursed_spruce_boat")));
    public static final DeferredHolder<Item, Item> DARK_SPRUCE_CHEST_BOAT = DeferredHolder.create(ResourceKey.create(Registries.ITEM, VResourceLocation.mod("dark_spruce_chest_boat")));
    public static final DeferredHolder<Item, Item> CURSED_SPRUCE_CHEST_BOAT = DeferredHolder.create(ResourceKey.create(Registries.ITEM, VResourceLocation.mod("cursed_spruce_chest_boat")));
}
