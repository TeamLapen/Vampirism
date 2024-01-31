package de.teamlapen.vampirism.data.provider;

import de.teamlapen.vampirism.api.datamaps.FluidBloodConversion;
import de.teamlapen.vampirism.api.datamaps.ItemBlood;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
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
        gatherItemBlood(builder(ModRegistries.ITEM_BLOOD));
        gatherFluidBloodConversion(builder(ModRegistries.FLUID_BLOOD_CONVERSION));
    }

    protected void gatherCompostables(Builder<Compostable, Item> compostables) {
        compostables.add((Holder<Item>) (Object)ModBlocks.VAMPIRE_ORCHID, new Compostable(0.65F), false);
        compostables.add(ModItems.ITEM_GARLIC, new Compostable(0.65f), false);
    }

    @SuppressWarnings("deprecation")
    protected void gatherItemBlood(Builder<ItemBlood, Item> itemBlood) {
        itemBlood.add(Items.BEEF.builtInRegistryHolder(), new ItemBlood(200), false);
        itemBlood.add(Items.MUTTON.builtInRegistryHolder(), new ItemBlood(100), false);
        itemBlood.add(Items.PORKCHOP.builtInRegistryHolder(), new ItemBlood(100), false);
        itemBlood.add(Items.RABBIT.builtInRegistryHolder(), new ItemBlood(100), false);
        itemBlood.add(Items.CHICKEN.builtInRegistryHolder(), new ItemBlood(), false);
        itemBlood.add(Items.COOKED_CHICKEN.builtInRegistryHolder(), new ItemBlood(), false);
        itemBlood.add(Items.ROTTEN_FLESH.builtInRegistryHolder(), new ItemBlood(), false);
        itemBlood.add(Items.COOKED_MUTTON.builtInRegistryHolder(), new ItemBlood(), false);
        itemBlood.add(Items.COOKED_PORKCHOP.builtInRegistryHolder(), new ItemBlood(), false);
        itemBlood.add(Items.COOKED_RABBIT.builtInRegistryHolder(), new ItemBlood(), false);
        itemBlood.add(ModItems.HUMAN_HEART, new ItemBlood(200), false);
        itemBlood.add(ModItems.WEAK_HUMAN_HEART, new ItemBlood(100), false);
    }

    protected void gatherFluidBloodConversion(Builder<FluidBloodConversion, Fluid> fluidConversions) {
        fluidConversions.add(ModFluids.IMPURE_BLOOD, new FluidBloodConversion(0.75f), false);
    }
}
