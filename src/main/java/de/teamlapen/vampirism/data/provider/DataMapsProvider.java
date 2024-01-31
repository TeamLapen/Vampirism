package de.teamlapen.vampirism.data.provider;

import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import java.util.concurrent.CompletableFuture;

public class DataMapsProvider extends DataMapProvider {

    public DataMapsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather() {
        gatherCompostables(builder(NeoForgeDataMaps.COMPOSTABLES));
    }

    protected void gatherCompostables(Builder<Compostable, Item> compostables) {
        compostables.add((Holder<Item>) (Object)ModBlocks.VAMPIRE_ORCHID, new Compostable(0.65F), false);
        compostables.add(ModItems.ITEM_GARLIC, new Compostable(0.65f), false);
    }
}
