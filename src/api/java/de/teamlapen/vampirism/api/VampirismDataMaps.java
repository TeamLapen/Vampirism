package de.teamlapen.vampirism.api;

import com.google.common.base.Suppliers;
import de.teamlapen.vampirism.api.datamaps.IConverterEntry;
import de.teamlapen.vampirism.api.datamaps.IEntityBlood;
import de.teamlapen.vampirism.api.datamaps.IFluidBloodConversion;
import de.teamlapen.vampirism.api.datamaps.IItemBlood;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.RegistryManager;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

import java.util.function.Supplier;

@SuppressWarnings({"unused", "unchecked"})
public class VampirismDataMaps {

    public static final Supplier<DataMapType<Item, IItemBlood>> ITEM_BLOOD = Suppliers.memoize(() -> (DataMapType<Item, IItemBlood>) RegistryManager.getDataMap(Registries.ITEM, Keys.ITEM_BLOOD));
    public static final Supplier<DataMapType<EntityType<?>, IEntityBlood>> ENTITY_BLOOD = Suppliers.memoize(() -> (DataMapType<EntityType<?>, IEntityBlood>) RegistryManager.getDataMap(Registries.ENTITY_TYPE, Keys.ENTITY_BLOOD));
    public static final Supplier<DataMapType<Fluid, IFluidBloodConversion>> FLUID_BLOOD_CONVERSION = Suppliers.memoize(() -> (DataMapType<Fluid, IFluidBloodConversion>) RegistryManager.getDataMap(Registries.FLUID, Keys.FLUID_BLOOD_CONVERSION));
    public static final Supplier<DataMapType<EntityType<?>, IConverterEntry>> ENTITY_CONVERTER = Suppliers.memoize(() -> (DataMapType<EntityType<?>, IConverterEntry>) RegistryManager.getDataMap(Registries.ENTITY_TYPE, Keys.ENTITY_CONVERTER));

    public static class Keys {
        public static final ResourceLocation ITEM_BLOOD = new ResourceLocation(VReference.MODID, "item_blood");
        public static final ResourceLocation ENTITY_BLOOD = new ResourceLocation(VReference.MODID, "entity_blood");
        public static final ResourceLocation FLUID_BLOOD_CONVERSION = new ResourceLocation(VReference.MODID, "fluid_blood_conversion");
        public static final ResourceLocation ENTITY_CONVERTER = new ResourceLocation(VReference.MODID, "entity_converter");
    }
}
