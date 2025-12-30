package de.teamlapen.faction.data.provider.loot;

import de.teamlapen.faction.common.core.FactionBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModBlockLootTableProvider extends BlockLootSubProvider {

    private static final Set<Item> EXPLOSION_RESISTANT = Stream.of(FactionBlocks.TOTEM_TOP, FactionBlocks.TOTEM_TOP_CRAFTED)
            .map(DeferredBlock::asItem)
            .collect(Collectors.toSet());

    public ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(EXPLOSION_RESISTANT, FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        this.dropSelf(FactionBlocks.TOTEM_BASE.get());
        this.dropSelf(FactionBlocks.TOTEM_TOP.get());
        this.dropSelf(FactionBlocks.TOTEM_TOP_CRAFTED.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return FactionBlocks.BLOCKS.getEntries().stream().map(DeferredHolder::get).collect(Collectors.toList());
    }
}
