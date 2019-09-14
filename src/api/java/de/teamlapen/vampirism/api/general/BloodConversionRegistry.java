package de.teamlapen.vampirism.api.general;

import com.google.common.collect.Maps;

import de.teamlapen.vampirism.api.VReference;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Store blood conversion factors.
 * Item -> impure blood.
 * Liquids -> blood
 */
public class BloodConversionRegistry {
    /**
     * stores conversion rate from items to impure blood
     */
    private @Nonnull
    static final Map<ResourceLocation, Integer> items = Maps.newHashMap();
    /**
     * stores conversion rate from fluids to blood
     */
    private @Nonnull
    static final Map<ResourceLocation, Float> fluids = Maps.newHashMap();

    public static void applyNewFluidResources(Map<ResourceLocation, Integer> values, int divider) {
        fluids.clear();
        for (Map.Entry<ResourceLocation, Integer> entry : values.entrySet()) {
            fluids.put(entry.getKey(), ((float) entry.getValue()) / divider);
        }
    }

    public static void applyNewItemResources(Map<ResourceLocation, Integer> values, int multiplier) {
        items.clear();
        for (Map.Entry<ResourceLocation, Integer> entry : values.entrySet()) {
            items.put(entry.getKey(), entry.getValue() * multiplier);
        }
    }

    /**
     * Get the amount of impure blood the given item is worth.
     *
     * @param item ItemStack
     * @return Impure blood amount in mB or 0
     */
    public static int getImpureBloodValue(@Nonnull ItemStack item) {
        if (items.containsKey(item.getItem().getRegistryName())) {
            return items.get(item.getItem().getRegistryName());
        }
        return 0;
    }

    public static boolean existsImpureBloodValue(@Nonnull Item item) {
        return items.containsKey(item.getRegistryName());
    }

    /**
     * Get the amount of blood the given fluid is worth.
     *
     * @param fluid FluidStack
     * @return Impure blood amount in mB or 0
     */
    public static float getBloodValue(@Nonnull FluidStack fluid) {
        if (fluids.containsKey(fluid.getFluid().getRegistryName())) {
            return fluids.get(fluid.getFluid().getRegistryName());
        }
        return 0f;
    }

    public static boolean existsBloodValue(@Nonnull Fluid fluid) {
        return fluids.containsKey(fluid.getRegistryName());
    }

    /**
     * Get the FluidStack of blood equivalent to the given fluid.
     *
     * @param fluid FluidStack
     * @return Impure blood amount in mB or 0
     */
    public static FluidStack getBloodFromFluid(@Nonnull FluidStack fluid) {
        if (fluid.getFluid().isEquivalentTo(VReference.blood_fluid))
            return fluid;
        return new FluidStack(VReference.blood_fluid, (int) (getBloodValue(fluid) * fluid.getAmount()));
    }
}
