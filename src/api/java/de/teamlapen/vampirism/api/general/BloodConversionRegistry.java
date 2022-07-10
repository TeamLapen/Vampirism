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
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    private static final Map<ResourceLocation, Float> items = Maps.newHashMap();

    /**
     * stores conversion rate from fluids to blood
     */
    @Nonnull
    private static final Map<ResourceLocation, Float> fluids = Maps.newHashMap();

    @Nonnull
    private static final Map<ResourceLocation, Float> entities = Maps.newHashMap();

    /**
     * stores conversion rate from not listed items to impure blood
     */
    @Nonnull
    private static final Map<ResourceLocation, Float> items_calculated = Maps.newHashMap();
    /**
     * stores items with no conversion rate
     */
    @Nonnull
    private static final Set<ResourceLocation> items_blacklist = Sets.newHashSet();

    /**
     * @deprecated use {@link #applyNewFluidResources(java.util.Map)}
     */
    @Deprecated //TODO remove
    public static void applyNewFluidResources(Map<ResourceLocation, Integer> values, int divider) {
        applyNewFluidResources(values.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() / (float)divider)));
    }

    /**
     * @deprecated use {@link #applyNewItemResources(java.util.Map)}
     */
    @Deprecated //TODO remove
    public static void applyNewItemResources(Map<ResourceLocation, Integer> values, int multiplier) {
        applyNewItemResources(values.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() * (float)multiplier)));
    }

    /**
     * @deprecated no longer used
     */
    @Deprecated //TODO remove
    public static void applyNewItemCalculated(Map<ResourceLocation, Integer> values) {
        items_calculated.putAll(values.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() * 100f)));
    }

    /**
     * @deprecated use {@link #getItemConversions()}
     */
    @Deprecated //TODO remove
    public static Map<ResourceLocation, Integer> getItemValues() {
        return items.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> (int) (e.getValue() / 100)));
    }

    /**
     * @deprecated use {@link #getFluidConversions()}
     */
    @Deprecated //TODO remove
    public static Map<ResourceLocation, Integer> getFluidValues() {
        return fluids.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> (int) (e.getValue() * 100)));
    }

    /**
     * @deprecated use {@link #getItemConversionCalculated()}
     */
    @Deprecated //TODO remove
    public static Map<ResourceLocation, Integer> getItemValuesCalculated() {
        return items_calculated.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> (int) (e.getValue() / 100f)));
    }

    public static void applyNewFluidResources(Map<ResourceLocation, Float> values) {
        fluids.clear();
        fluids.putAll(values);
    }

    public static void applyNewEntitiesResources(Map<ResourceLocation, Float> values) {
        entities.clear();
        entities.putAll(values);
    }

    public static void applyNewItemResources(Map<ResourceLocation, Float> values) {
        items.clear();
        for (Map.Entry<ResourceLocation, Float> value : values.entrySet()) {
            if (value.getValue() != 0) {
                items.put(value.getKey(), value.getValue());
            } else {
                items_blacklist.add(value.getKey());
            }
        }
    }

    public static Map<ResourceLocation, Float> getItemConversions() {
        return Collections.unmodifiableMap(items);
    }

    public static Map<ResourceLocation, Float> getEntityConversions() {
        return Collections.unmodifiableMap(entities);
    }

    public static Map<ResourceLocation, Float> getFluidConversions() {
        return Collections.unmodifiableMap(fluids);
    }

    public static Map<ResourceLocation, Float> getItemConversionCalculated() {
        return Collections.unmodifiableMap(items_calculated);
    }

    @Deprecated //TODO remove
    public static int getFluidDivider() {
        return 100;
    }

    @Deprecated //TODO remove
    public static int getItemMultiplier() {
        return 100;
    }

    /**
     * Get the amount of impure blood the given item is worth.
     *
     * @param item ItemStack
     * @return Impure blood amount in mB or 0
     */
    public static int getImpureBloodValue(@Nonnull Item item) {
        if (items.containsKey(item.getRegistryName()) || items_calculated.containsKey(item.getRegistryName())) {
            return (items.containsKey(item.getRegistryName()) ? items.get(item.getRegistryName()) : items_calculated.get(item.getRegistryName())).intValue();
        }
        return 0;
    }

    public static boolean canBeConverted(@Nonnull Item item) {
        if (items.containsKey(item.getRegistryName()) || items_calculated.containsKey(item.getRegistryName())) {
            return true;
        } else if (items_blacklist.contains(item.getRegistryName())) {
            return false;
        } else {
            if (item.isEdible() && item.getFoodProperties().isMeat()) {
                int value = MathHelper.clamp((item.getRegistryName() != null && item.getRegistryName().getPath().contains("cooked")) ? 0 : item.getFoodProperties().getNutrition() / 2, 0, 5);
                if (value > 0) {
                    items_calculated.put(item.getRegistryName(), (float) (value * 100));
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
        if (fluid.getFluid().isSame(VReference.blood_fluid_supplier.get()))
            return fluid;
        return new FluidStack(VReference.blood_fluid_supplier.get(), (int) (getBloodValue(fluid) * fluid.getAmount()));
    }
}
