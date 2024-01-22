package de.teamlapen.vampirism.data.provider;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.datamaps.*;
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
        gatherItemBlood(builder(ModDataMaps.ITEM_BLOOD_MAP));
        gatherFluidBloodConversion(builder(ModDataMaps.FLUID_BLOOD_CONVERSION_MAP));
        gatherEntityBlood(builder(ModDataMaps.ENTITY_BLOOD_MAP));
        gatherEntityConverter(builder(ModDataMaps.ENTITY_CONVERTER_MAP));
        gatherGarlicDiffuserFuel(builder(ModDataMaps.GARLIC_DIFFUSER_FUEL_MAP));
        gatherFogDiffuserFuel(builder(ModDataMaps.FOG_DIFFUSER_FUEL_MAP));
    }

    private void gatherFogDiffuserFuel(Builder<IFogDiffuserFuel, Item> builder) {
        builder.add(ModItems.PURE_BLOOD_0, new FogDiffuserFuel(288000), false);
        builder.add(ModItems.PURE_BLOOD_1, new FogDiffuserFuel(432000), false);
        builder.add(ModItems.PURE_BLOOD_2, new FogDiffuserFuel(864000), false);
        builder.add(ModItems.PURE_BLOOD_3, new FogDiffuserFuel(1296000), false);
        builder.add(ModItems.PURE_BLOOD_4, new FogDiffuserFuel(1728000), false);
    }

    private void gatherGarlicDiffuserFuel(Builder<IGarlicDiffuserFuel, Item> builder) {
        builder.add(ModItems.PURIFIED_GARLIC, new GarlicDiffuserFuel(108000), false);
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

    protected void gatherEntityBlood(Builder<IEntityBlood, EntityType<?>> entityValues) {
        Function<EntityType<?>, Holder<EntityType<?>>> holder = BuiltInRegistries.ENTITY_TYPE::wrapAsHolder;

        entityValues.add(holder.apply(EntityType.COW), new EntityBloodEntry(10), false);
        entityValues.add(holder.apply(EntityType.PIG), new EntityBloodEntry(5), false);
        entityValues.add(holder.apply(EntityType.SHEEP), new EntityBloodEntry(5), false);
        entityValues.add(holder.apply(EntityType.CHICKEN), EntityBloodEntry.EMPTY, false);
        entityValues.add(holder.apply(EntityType.RABBIT), new EntityBloodEntry(2), false);
        entityValues.add(holder.apply(EntityType.WOLF), new EntityBloodEntry(5), false);
        entityValues.add(holder.apply(EntityType.MOOSHROOM), new EntityBloodEntry(10), false);
        entityValues.add(holder.apply(EntityType.OCELOT), new EntityBloodEntry(4), false);
        entityValues.add(holder.apply(EntityType.HORSE), new EntityBloodEntry(13), false);
        entityValues.add(holder.apply(EntityType.DONKEY), new EntityBloodEntry(13), false);
        entityValues.add(holder.apply(EntityType.MULE), new EntityBloodEntry(13), false);
        entityValues.add(holder.apply(EntityType.PARROT), EntityBloodEntry.EMPTY, false);
        entityValues.add(holder.apply(EntityType.LLAMA), new EntityBloodEntry(10), false);
        entityValues.add(holder.apply(EntityType.BEE), EntityBloodEntry.EMPTY, false);
        entityValues.add(holder.apply(EntityType.VILLAGER), new EntityBloodEntry(15), false);
        entityValues.add(holder.apply(EntityType.POLAR_BEAR), new EntityBloodEntry(10), false);
        entityValues.add(holder.apply(EntityType.TRADER_LLAMA), new EntityBloodEntry(10), false);
        entityValues.add(holder.apply(EntityType.PANDA), new EntityBloodEntry(13), false);
        entityValues.add(holder.apply(EntityType.CAT), new EntityBloodEntry(3), false);
        entityValues.add(holder.apply(EntityType.TURTLE), new EntityBloodEntry(5), false);
        entityValues.add(holder.apply(EntityType.FOX), new EntityBloodEntry(4), false);
        entityValues.add(holder.apply(EntityType.SKELETON_HORSE), EntityBloodEntry.EMPTY, false);
        entityValues.add(holder.apply(EntityType.ALLAY), EntityBloodEntry.EMPTY, false);
        entityValues.add(holder.apply(EntityType.AXOLOTL), EntityBloodEntry.EMPTY, false);
        entityValues.add(holder.apply(EntityType.FROG), EntityBloodEntry.EMPTY, false);
        entityValues.add(holder.apply(EntityType.GOAT), new EntityBloodEntry(7), false);
        entityValues.add(holder.apply(EntityType.CAMEL), new EntityBloodEntry(15), false);
        entityValues.add(holder.apply(EntityType.SNIFFER), new EntityBloodEntry(20), false);
    }

    protected void gatherEntityConverter(Builder<IConverterEntry, EntityType<?>> entityValues) {
        Function<String, ResourceLocation> overlay = (String name) -> new ResourceLocation(REFERENCE.MODID, String.format("textures/entity/vanilla/%s_overlay.png", name));
        Function<EntityType<?>, Holder<EntityType<?>>> holder = BuiltInRegistries.ENTITY_TYPE::wrapAsHolder;

        entityValues.add(holder.apply(EntityType.COW), new ConverterEntry(new SpecialConverter<>(ModEntities.CONVERTED_COW), overlay.apply("cow")), false);
        entityValues.add(holder.apply(EntityType.SHEEP), new ConverterEntry(new SpecialConverter<>(ModEntities.CONVERTED_SHEEP), overlay.apply("sheep")), false);
        entityValues.add(holder.apply(EntityType.HORSE), new ConverterEntry(new SpecialConverter<>(ModEntities.CONVERTED_HORSE), overlay.apply("horse")), false);
        entityValues.add(holder.apply(EntityType.DONKEY), new ConverterEntry(new SpecialConverter<>(ModEntities.CONVERTED_DONKEY), overlay.apply("horse")), false);
        entityValues.add(holder.apply(EntityType.MULE), new ConverterEntry(new SpecialConverter<>(ModEntities.CONVERTED_MULE), overlay.apply("horse")), false);
        entityValues.add(holder.apply(EntityType.VILLAGER), new ConverterEntry(new SpecialConverter<>(ModEntities.VILLAGER_CONVERTED), overlay.apply("villager")), false);
        entityValues.add(holder.apply(EntityType.FOX), new ConverterEntry(new SpecialConverter<>(ModEntities.CONVERTED_FOX), overlay.apply("fox")), false);
        entityValues.add(holder.apply(EntityType.GOAT), new ConverterEntry(new SpecialConverter<>(ModEntities.CONVERTED_GOAT), overlay.apply("goat")), false);
        entityValues.add(holder.apply(EntityType.CAMEL), new ConverterEntry(new SpecialConverter<>(ModEntities.CONVERTED_CAMEL), overlay.apply("camel")), false);
        entityValues.add(holder.apply(EntityType.PIG), new ConverterEntry(overlay.apply("pig")), false);
        entityValues.add(holder.apply(EntityType.RABBIT), new ConverterEntry(overlay.apply("rabbit")), false);
        entityValues.add(holder.apply(EntityType.LLAMA), new ConverterEntry(overlay.apply("llama")), false);
        entityValues.add(holder.apply(EntityType.POLAR_BEAR), new ConverterEntry(overlay.apply("polarbear")), false);
        entityValues.add(holder.apply(EntityType.PANDA), new ConverterEntry(overlay.apply("panda")), false);
        entityValues.add(holder.apply(EntityType.CAT), new ConverterEntry(overlay.apply("cat")), false);
    }

}
