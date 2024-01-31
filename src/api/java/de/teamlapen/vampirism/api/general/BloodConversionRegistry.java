package de.teamlapen.vampirism.api.general;

import com.google.common.collect.Maps;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.datamaps.FluidBloodConversion;
import de.teamlapen.vampirism.api.datamaps.ItemBlood;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BloodConversionRegistry {

    @NotNull
    private static final Map<ResourceLocation, Float> entities = Maps.newHashMap();

    private static final ItemBlood EMPTY = new ItemBlood(0);
    private static final Map<Item, ItemBlood> CALCULATED = new HashMap<>();

    public static void applyNewEntitiesResources(@NotNull Map<ResourceLocation, Float> values) {
        entities.clear();
        entities.putAll(values);
    }

    public static @NotNull Map<ResourceLocation, Float> getEntityConversions() {
        return Collections.unmodifiableMap(entities);
    }

    /**
     * @deprecated use {@link #getItemBlood(net.minecraft.world.item.ItemStack)}
     */
    @Deprecated
    public static int getImpureBloodValue(@NotNull Item item) {
        return getItemBlood(new ItemStack(item)).blood();
    }

    public static boolean canBeConverted(@NotNull ItemStack stack) {
        return getItemBlood(stack).blood() > 0;
    }

    public static ItemBlood getItemBlood(ItemStack stack) {
        ItemBlood data = stack.getItemHolder().getData(VampirismRegistries.ITEM_BLOOD_VALUES.get());
        if (data == null) {
            data = CALCULATED.get(stack.getItem());
        }
        if (data == null) {
            FoodProperties food = stack.getFoodProperties(null);
            if (food != null && food.isMeat() && !id(stack.getItem()).getPath().contains("cooked")) {
                data = new ItemBlood(food.getNutrition() * 10);
            } else {
                data = EMPTY;
            }
            CALCULATED.put(stack.getItem(), data);
        }
        return data;
    }

    /**
     * Get the amount of blood the given fluid is worth.
     *
     * @param fluid FluidStack
     * @return Impure blood amount in mB or 0
     */
    public static float getBloodValue(@NotNull FluidStack fluid) {
        FluidBloodConversion fluidConversion = getFluidConversion(fluid.getFluid());
        if (fluidConversion != null) {
            return fluidConversion.conversionRate();
        }
        return 0;
    }

    public static FluidBloodConversion getFluidConversion(Fluid fluid) {
        return fluid.builtInRegistryHolder().getData(VampirismRegistries.FLUID_BLOOD_CONVERSION.get());
    }

    public static boolean hasConversion(@NotNull Fluid fluid) {
        return getFluidConversion(fluid) != null;
    }

    /**
     * Get the FluidStack of blood equivalent to the given fluid.
     *
     * @param fluid FluidStack
     * @return Impure blood amount in mB or 0
     */
    public static @NotNull FluidStack getBloodFromFluid(@NotNull FluidStack fluid) {
        if (fluid.getFluid().isSame(VReference.BLOOD.get())) {
            return fluid;
        }
        return new FluidStack(VReference.BLOOD.get(), (int) (getBloodValue(fluid) * fluid.getAmount()));
    }

    public static @NotNull ResourceLocation id(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }

    public static @NotNull ResourceLocation id(Fluid block) {
        return BuiltInRegistries.FLUID.getKey(block);
    }
}
