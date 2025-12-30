package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ModRegistryItems {

    public static final DeferredHolder<Block, Block> DARK_SPRUCE_PLANKS = DeferredHolder.create(ResourceKey.create(Registries.BLOCK, VIdentifier.mod("dark_spruce_planks")));
    public static final DeferredHolder<Block, Block> CURSED_SPRUCE_PLANKS = DeferredHolder.create(ResourceKey.create(Registries.BLOCK, VIdentifier.mod("cursed_spruce_planks")));
}
