package de.teamlapen.factions.data.provider.model;

import de.teamlapen.factions.api.util.REFERENCE;
import de.teamlapen.factions.common.core.FactionBlocks;
import de.teamlapen.factions.common.core.FactionItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.stream.Stream;

public class ModModelProvider extends ModelProvider {

    public ModModelProvider(PackOutput output) {
        super(output, REFERENCE.MOD_ID);
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return FactionBlocks.BLOCKS.getEntries().stream();
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return FactionItems.ITEMS.getEntries().stream();
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        super.registerModels(new ModBlockModelGenerators(blockModels), new ModItemModelGenerators(itemModels));
    }
}
