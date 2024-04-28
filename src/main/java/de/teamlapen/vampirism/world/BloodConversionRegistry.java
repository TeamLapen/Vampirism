package de.teamlapen.vampirism.world;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismDataMaps;
import de.teamlapen.vampirism.api.datamaps.IFluidBloodConversion;
import de.teamlapen.vampirism.api.datamaps.IItemBlood;
import de.teamlapen.vampirism.api.general.IBloodConversionRegistry;
import de.teamlapen.vampirism.core.ModDataMaps;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.datamaps.FluidBloodConversion;
import de.teamlapen.vampirism.datamaps.ItemBlood;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BloodConversionRegistry implements IBloodConversionRegistry {

    private final IItemBlood EMPTY = new ItemBlood(0);
    private final Map<Item, IItemBlood> CALCULATED = new HashMap<>();

    @Override
    public int getImpureBloodValue(@NotNull Item item) {
        return getItemBlood(new ItemStack(item)).blood();
    }

    @Override
    public boolean canBeConverted(@NotNull ItemStack stack) {
        return getItemBlood(stack).blood() > 0;
    }

    @Override
    public @NotNull IItemBlood getItemBlood(@NotNull ItemStack stack) {
        IItemBlood data = stack.getItemHolder().getData(VampirismDataMaps.ITEM_BLOOD.get());
        if (data == null) {
            data = CALCULATED.get(stack.getItem());
        }
        if (data == null) {
            FoodProperties food = stack.getFoodProperties(null);
            if (food != null && stack.is(ItemTags.MEAT) && !id(stack.getItem()).getPath().contains("cooked")) {
                data = new ItemBlood(food.nutrition() * 10);
            } else {
                data = EMPTY;
            }
            CALCULATED.put(stack.getItem(), data);
        }
        return data;
    }

    private float getBloodValue(@NotNull FluidStack fluid) {
        return getFluidConversion(fluid.getFluid()).conversionRate();
    }

    @Override
    public @NotNull IFluidBloodConversion getFluidConversion(@NotNull Fluid fluid) {
        Map<ResourceKey<Fluid>, IFluidBloodConversion> dataMap = BuiltInRegistries.FLUID.getDataMap(VampirismDataMaps.FLUID_BLOOD_CONVERSION.get());
        var conversion = BuiltInRegistries.FLUID.wrapAsHolder(fluid).getData(ModDataMaps.FLUID_BLOOD_CONVERSION_MAP);
        if (conversion != null) {
            return conversion;
        }
        return FluidBloodConversion.NONE;
    }

    @Override
    public boolean hasConversion(@NotNull Fluid fluid) {
        return getFluidConversion(fluid).conversionRate() != 0;
    }

    @Override
    public @NotNull FluidStack getBloodFromFluid(@NotNull FluidStack fluid) {
        if (fluid.getFluid().isSame(VReference.BLOOD.get())) {
            return fluid;
        }
        return new FluidStack(VReference.BLOOD.get(), (int) (getBloodValue(fluid) * fluid.getAmount()));
    }

    private static @NotNull ResourceLocation id(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }

    private static @NotNull ResourceLocation id(Fluid block) {
        return BuiltInRegistries.FLUID.getKey(block);
    }

    public void reload() {
        CALCULATED.clear();
    }
}
