package de.teamlapen.vampirism.data.provider;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.datamaps.IEntityBloodEntry;
import de.teamlapen.vampirism.api.datamaps.IFluidBloodConversion;
import de.teamlapen.vampirism.api.datamaps.IItemBlood;
import de.teamlapen.vampirism.datamaps.*;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.entity.converted.converter.SpecialConverter;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class DataMapsProvider extends DataMapProvider {

    public DataMapsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather() {
        gatherCompostables(builder(NeoForgeDataMaps.COMPOSTABLES));
        gatherItemBlood(builder(ModRegistries.ITEM_BLOOD));
        gatherFluidBloodConversion(builder(ModRegistries.FLUID_BLOOD_CONVERSION));
        gatherEntityEntries(builder(ModRegistries.ENTITY_BLOOD));
    }

    protected void gatherCompostables(Builder<Compostable, Item> compostables) {
        compostables.add((Holder<Item>) (Object)ModBlocks.VAMPIRE_ORCHID, new Compostable(0.65F), false);
        compostables.add(ModItems.ITEM_GARLIC, new Compostable(0.65f), false);
    }

    @SuppressWarnings("deprecation")
    protected void gatherItemBlood(Builder<IItemBlood, Item> itemBlood) {
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

    protected void gatherFluidBloodConversion(Builder<IFluidBloodConversion, Fluid> fluidConversions) {
        fluidConversions.add(ModFluids.IMPURE_BLOOD, new FluidBloodConversion(0.75f), false);
    }

    protected void gatherEntityEntries(Builder<IEntityBloodEntry, EntityType<?>> entityValues) {
        Function<String, ResourceLocation> overlay = (String name) -> new ResourceLocation(REFERENCE.MODID, String.format("textures/entity/vanilla/%s_overlay.png", name));
        Function<EntityType<?>, Holder<EntityType<?>>> holder = BuiltInRegistries.ENTITY_TYPE::wrapAsHolder;

        entityValues.add(holder.apply(EntityType.COW), new ConvertibleEntityBloodEntry(10, new ConverterEntry(new SpecialConverter<>(ModEntities.CONVERTED_COW), overlay.apply("cow"))), false);
        entityValues.add(holder.apply(EntityType.PIG), new ConvertibleEntityBloodEntry(5, new ConverterEntry(overlay.apply("pig"))), false);
        entityValues.add(holder.apply(EntityType.SHEEP), new ConvertibleEntityBloodEntry(5, new ConverterEntry(new SpecialConverter<>(ModEntities.CONVERTED_SHEEP), overlay.apply("sheep"))), false);
        entityValues.add(holder.apply(EntityType.CHICKEN), EmptyEntityBloodEntry.INSTANCE, false);
        entityValues.add(holder.apply(EntityType.RABBIT), new ConvertibleEntityBloodEntry(2, new ConverterEntry(overlay.apply("rabbit"))), false);
        entityValues.add(holder.apply(EntityType.WOLF), new EntityBloodEntry(5), false);
        entityValues.add(holder.apply(EntityType.MOOSHROOM), new EntityBloodEntry(10), false);
        entityValues.add(holder.apply(EntityType.OCELOT), new EntityBloodEntry(4), false);
        entityValues.add(holder.apply(EntityType.HORSE), new ConvertibleEntityBloodEntry(13, new ConverterEntry(new SpecialConverter<>(ModEntities.CONVERTED_HORSE), overlay.apply("horse"))), false);
        entityValues.add(holder.apply(EntityType.DONKEY), new ConvertibleEntityBloodEntry(13, new ConverterEntry(new SpecialConverter<>(ModEntities.CONVERTED_DONKEY), overlay.apply("horse"))), false);
        entityValues.add(holder.apply(EntityType.MULE), new ConvertibleEntityBloodEntry(13, new ConverterEntry(new SpecialConverter<>(ModEntities.CONVERTED_MULE), overlay.apply("horse"))), false);
        entityValues.add(holder.apply(EntityType.PARROT), EmptyEntityBloodEntry.INSTANCE, false);
        entityValues.add(holder.apply(EntityType.LLAMA), new ConvertibleEntityBloodEntry(10, new ConverterEntry(overlay.apply("llama"))), false);
        entityValues.add(holder.apply(EntityType.BEE), EmptyEntityBloodEntry.INSTANCE, false);
        entityValues.add(holder.apply(EntityType.VILLAGER), new ConvertibleEntityBloodEntry(15, new ConverterEntry(new SpecialConverter<>(ModEntities.VILLAGER_CONVERTED), overlay.apply("villager"))), false);
        entityValues.add(holder.apply(EntityType.POLAR_BEAR), new ConvertibleEntityBloodEntry(10, new ConverterEntry(overlay.apply("polarbear"))), false);
        entityValues.add(holder.apply(EntityType.TRADER_LLAMA), new EntityBloodEntry(10), false);
        entityValues.add(holder.apply(EntityType.PANDA), new ConvertibleEntityBloodEntry(13, new ConverterEntry(overlay.apply("panda"))), false);
        entityValues.add(holder.apply(EntityType.CAT), new ConvertibleEntityBloodEntry(3, new ConverterEntry(overlay.apply("cat"))), false);
        entityValues.add(holder.apply(EntityType.TURTLE), new EntityBloodEntry(5), false);
        entityValues.add(holder.apply(EntityType.FOX), new ConvertibleEntityBloodEntry(4, new ConverterEntry(new SpecialConverter<>(ModEntities.CONVERTED_FOX), overlay.apply("fox"))), false);
        entityValues.add(holder.apply(EntityType.SKELETON_HORSE), EmptyEntityBloodEntry.INSTANCE, false);
        entityValues.add(holder.apply(EntityType.ALLAY), EmptyEntityBloodEntry.INSTANCE, false);
        entityValues.add(holder.apply(EntityType.AXOLOTL), EmptyEntityBloodEntry.INSTANCE, false);
        entityValues.add(holder.apply(EntityType.FROG), EmptyEntityBloodEntry.INSTANCE, false);
        entityValues.add(holder.apply(EntityType.GOAT), new ConvertibleEntityBloodEntry(7, new ConverterEntry(new SpecialConverter<>(ModEntities.CONVERTED_GOAT), overlay.apply("goat"))), false);
        entityValues.add(holder.apply(EntityType.CAMEL), new ConvertibleEntityBloodEntry(15, new ConverterEntry(new SpecialConverter<>(ModEntities.CONVERTED_CAMEL), overlay.apply("camel"))), false);
        entityValues.add(holder.apply(EntityType.SNIFFER), new EntityBloodEntry(20), false);
    }
}
