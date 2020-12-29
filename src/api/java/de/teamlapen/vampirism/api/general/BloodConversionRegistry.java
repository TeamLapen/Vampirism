package de.teamlapen.vampirism.api.general;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.teamlapen.vampirism.api.VReference;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Store blood conversion factors.
 * Item -> impure blood.
 * Liquids -> blood
 */
public class BloodConversionRegistry {
    /**
     * stores conversion rate from items to impure blood
     */
    @Nonnull
    private static final Map<ResourceLocation, Integer> items = Maps.newHashMap();
    /**
     * stores conversion rate from fluids to blood
     */
    @Nonnull
    private static final Map<ResourceLocation, Integer> fluids = Maps.newHashMap();

    /**
     * stores conversion rate from not listed items to impure blood
     */
    @Nonnull
    private static final Map<ResourceLocation, Integer> items_calculated = Maps.newHashMap();
    /**
     * stores items with no conversion rate
     */
    @Nonnull
    private static final Set<ResourceLocation> items_blacklist = Sets.newHashSet();

    private static int fluidDivider = 100;
    private static int itemMultiplier = 100;

    public static void applyNewFluidResources(Map<ResourceLocation, Integer> values, int divider) {
        fluids.clear();
        fluidDivider = divider;
        fluids.putAll(values);
    }

    public static void applyNewItemResources(Map<ResourceLocation, Integer> values, int multiplier) {
        items.clear();
        itemMultiplier = multiplier;
        for (Map.Entry<ResourceLocation, Integer> value : values.entrySet()) {
            if (value.getValue() != 0) {
                items.put(value.getKey(), value.getValue());
            } else {
                items_blacklist.add(value.getKey());
            }
        }
    }

    public static void applyNewItemCalculated(Map<ResourceLocation, Integer> values) {
        items_calculated.putAll(values);
    }

    public static Map<ResourceLocation, Integer> getItemValues() {
        return new ConcurrentHashMap<>(items);
    }

    public static Map<ResourceLocation, Integer> getFluidValues() {
        return new ConcurrentHashMap<>(fluids);
    }

    public static Map<ResourceLocation, Integer> getItemValuesCalculated() {
        return new ConcurrentHashMap<>(items_calculated);
    }

    public static int getFluidDivider() {
        return fluidDivider;
    }

    public static int getItemMultiplier() {
        return itemMultiplier;
    }

    /**
     * Get the amount of impure blood the given item is worth.
     *
     * @param item ItemStack
     * @return Impure blood amount in mB or 0
     */
    public static int getImpureBloodValue(@Nonnull Item item) {
        if (items.containsKey(item.getRegistryName()) || items_calculated.containsKey(item.getRegistryName())) {
            return items.containsKey(item.getRegistryName()) ? items.get(item.getRegistryName()) * itemMultiplier : items_calculated.get(item.getRegistryName()) * itemMultiplier;
        }
        return 0;
    }

    public static boolean canBeConverted(@Nonnull Item item) {
        if (items.containsKey(item.getRegistryName()) || items_calculated.containsKey(item.getRegistryName())) {
            return true;
        } else if (items_blacklist.contains(item.getRegistryName())) {
            return false;
        } else {
            if (item.isFood() && item.getFood().isMeat()) {
                int value = MathHelper.clamp((item.getRegistryName() != null && item.getRegistryName().getPath().contains("cooked")) ? 0 : item.getFood().getHealing() / 2, 0, 5);
                if (value > 0) {
                    items_calculated.put(item.getRegistryName(), value);
                    return true;
                }
            }
            items_blacklist.add(item.getRegistryName());
            return false;
        }
    }

    /**
     * Get the amount of blood the given fluid is worth.
     *
     * @param fluid FluidStack
     * @return Impure blood amount in mB or 0
     */
    public static float getBloodValue(@Nonnull FluidStack fluid) {
        if (fluids.containsKey(fluid.getFluid().getRegistryName())) {
            return (float) fluids.get(fluid.getFluid().getRegistryName()) / fluidDivider;
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
