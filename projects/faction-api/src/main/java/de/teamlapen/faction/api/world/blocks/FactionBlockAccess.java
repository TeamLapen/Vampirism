package de.teamlapen.faction.api.world.blocks;

import de.teamlapen.faction.api.util.FIdentifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

public class FactionBlockAccess {

    public static final DeferredHolder<Block, Block> TOTEM_TOP = retrieveBlock(Keys.TOTEM_TOP);
    public static final DeferredHolder<Block, Block> TOTEM_TOP_CRAFTED = retrieveBlock(Keys.TOTEM_TOP_CRAFTED);

    public static class Keys {
        public static final Identifier TOTEM_TOP = FIdentifier.mod("totem_top");
        public static final Identifier TOTEM_TOP_CRAFTED = FIdentifier.mod("totem_top_crafted");
    }

    private static DeferredHolder<Block, Block> retrieveBlock(Identifier key) {
        return DeferredHolder.create(ResourceKey.create(Registries.BLOCK, key));
    }
}
