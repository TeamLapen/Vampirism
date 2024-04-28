package de.teamlapen.vampirism.core;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.api.VampirismDataMaps;
import de.teamlapen.vampirism.api.datamaps.IConverterEntry;
import de.teamlapen.vampirism.api.datamaps.IEntityBlood;
import de.teamlapen.vampirism.api.datamaps.IFluidBloodConversion;
import de.teamlapen.vampirism.api.datamaps.IItemBlood;
import de.teamlapen.vampirism.datamaps.ConverterEntry;
import de.teamlapen.vampirism.datamaps.EntityBloodEntry;
import de.teamlapen.vampirism.datamaps.FluidBloodConversion;
import de.teamlapen.vampirism.datamaps.ItemBlood;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

import static de.teamlapen.vampirism.api.VampirismDataMaps.Keys.*;

public class ModDataMaps {

    public static final DataMapType<Item, IItemBlood> ITEM_BLOOD_MAP = DataMapType.builder(ITEM_BLOOD, Registries.ITEM, ItemBlood.CODEC).synced(ItemBlood.NETWORK_CODEC, true).build();
    public static final DataMapType<EntityType<?>, IEntityBlood> ENTITY_BLOOD_MAP = DataMapType.builder(ENTITY_BLOOD, Registries.ENTITY_TYPE, EntityBloodEntry.CODEC).synced(EntityBloodEntry.NETWORK_CODEC, true).build();
    public static final DataMapType<Fluid, IFluidBloodConversion> FLUID_BLOOD_CONVERSION_MAP = DataMapType.builder(FLUID_BLOOD_CONVERSION, Registries.FLUID, FluidBloodConversion.CODEC).synced(FluidBloodConversion.NETWORK_CODEC, true).build();
    public static final DataMapType<EntityType<?>, IConverterEntry> ENTITY_CONVERTER_MAP = DataMapType.builder(VampirismDataMaps.Keys.ENTITY_CONVERTER, Registries.ENTITY_TYPE, ConverterEntry.CODEC).synced(ConverterEntry.CODEC, true).build();
    public static final DataMapType<Item, Integer> LIQUID_COLOR_MAP = DataMapType.builder(LIQUID_COLOR, Registries.ITEM, Codec.INT).synced(Codec.INT, true).build();

    static void registerDataMaps(RegisterDataMapTypesEvent event) {
        event.register(ITEM_BLOOD_MAP);
        event.register(FLUID_BLOOD_CONVERSION_MAP);
        event.register(ENTITY_BLOOD_MAP);
        event.register(ENTITY_CONVERTER_MAP);
        event.register(LIQUID_COLOR_MAP);
    }
}
