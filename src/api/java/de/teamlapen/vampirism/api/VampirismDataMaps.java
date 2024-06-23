package de.teamlapen.vampirism.api;

import com.google.common.base.Suppliers;
import de.teamlapen.vampirism.api.datamaps.IConverterEntry;
import de.teamlapen.vampirism.api.datamaps.IEntityBlood;
import de.teamlapen.vampirism.api.datamaps.IFluidBloodConversion;
import de.teamlapen.vampirism.api.datamaps.IItemBlood;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import com.google.common.base.Suppliers;
import de.teamlapen.vampirism.api.datamaps.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.RegistryManager;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

import java.util.function.Supplier;

import static de.teamlapen.vampirism.api.APIUtil.supplyDataMap;

@SuppressWarnings({"unused"})
public class VampirismDataMaps {

    public static final Supplier<DataMapType<Item, IItemBlood>> ITEM_BLOOD = supplyDataMap(Registries.ITEM, Keys.ITEM_BLOOD);
    public static final Supplier<DataMapType<EntityType<?>, IEntityBlood>> ENTITY_BLOOD = supplyDataMap(Registries.ENTITY_TYPE, Keys.ENTITY_BLOOD);
    public static final Supplier<DataMapType<Fluid, IFluidBloodConversion>> FLUID_BLOOD_CONVERSION = supplyDataMap(Registries.FLUID, Keys.FLUID_BLOOD_CONVERSION);
    public static final Supplier<DataMapType<EntityType<?>, IConverterEntry>> ENTITY_CONVERTER = supplyDataMap(Registries.ENTITY_TYPE, Keys.ENTITY_CONVERTER);
    public static final Supplier<DataMapType<Item, Integer>> LIQUID_COLOR = supplyDataMap(Registries.ITEM, Keys.LIQUID_COLOR);
    public static final Supplier<DataMapType<Item, IGarlicDiffuserFuel>> GARLIC_DIFFUSER_FUEL = supplyDataMap(Registries.ITEM, Keys.GARLIC_DIFFUSER_FUEL);
    public static final Supplier<DataMapType<Item, IFogDiffuserFuel>> FOG_DIFFUSER_FUEL = supplyDataMap(Registries.ITEM, Keys.FOG_DIFFUSER_FUEL);

    public static class Keys {
        public static final ResourceLocation ITEM_BLOOD = VResourceLocation.mod("item_blood");
        public static final ResourceLocation ENTITY_BLOOD = VResourceLocation.mod("entity_blood");
        public static final ResourceLocation FLUID_BLOOD_CONVERSION = VResourceLocation.mod("fluid_blood_conversion");
        public static final ResourceLocation ENTITY_CONVERTER = VResourceLocation.mod("entity_converter");
        public static final ResourceLocation LIQUID_COLOR = VResourceLocation.mod("liquid_color");
        public static final ResourceLocation GARLIC_DIFFUSER_FUEL = VResourceLocation.mod("garlic_diffuser_fuel");
        public static final ResourceLocation FOG_DIFFUSER_FUEL = VResourceLocation.mod("fog_diffuser_fuel");
    }


}
