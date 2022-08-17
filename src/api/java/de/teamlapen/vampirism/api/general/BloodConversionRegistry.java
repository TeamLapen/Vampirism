package de.teamlapen.vampirism.api.general;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.teamlapen.vampirism.api.VReference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

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
    @NotNull
    private static final Map<ResourceLocation, Float> items = Maps.newHashMap();

    /**
     * stores conversion rate from fluids to blood
     */
    @NotNull
    private static final Map<ResourceLocation, Float> fluids = Maps.newHashMap();

    @NotNull
    private static final Map<ResourceLocation, Float> entities = Maps.newHashMap();

    /**
     * stores conversion rate from not listed items to impure blood
     */
    @NotNull
    private static final Map<ResourceLocation, Float> items_calculated = Maps.newHashMap();
    /**
     * stores items with no conversion rate
     */
    @NotNull
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
    public static int getImpureBloodValue(@NotNull Item item) {
        if (items.containsKey(id(item)) || items_calculated.containsKey(id(item))) {
            return (items.containsKey(id(item)) ? items.get(id(item)) : items_calculated.get(id(item))).intValue();
        }
        return 0;
    }

    /**
     * @deprecated use {@link #canBeConverted(net.minecraft.world.item.ItemStack)}
     */
    @Deprecated
    public static boolean canBeConverted(@NotNull Item item) {
        return canBeConverted(new ItemStack(item));
    }

    public static boolean canBeConverted(@NotNull ItemStack stack) {
        ResourceLocation id = id(stack.getItem());
        if (items.containsKey(id) || items_calculated.containsKey(id)) {
            return true;
        } else if (items_blacklist.contains(id)) {
            return false;
        } else {
            if (stack.isEdible() && stack.getFoodProperties(null).isMeat()) {
                int value = Mth.clamp((id != null && id.getPath().contains("cooked")) ? 0 : stack.getFoodProperties(null).getNutrition() / 2, 0, 5);
                if (value > 0) {
                    items_calculated.put(id, (float) (value * 100));
                    return true;
                }
            }
            items_blacklist.add(id);
            return false;
        }
    }

    /**
     * Get the amount of blood the given fluid is worth.
     *
     * @param fluid FluidStack
     * @return Impure blood amount in mB or 0
     */
    public static float getBloodValue(@NotNull FluidStack fluid) {
        if (fluids.containsKey(id(fluid.getFluid()))) {
            return fluids.get(id(fluid.getFluid()));
        }
        return 0f;
    }

    public static boolean existsBloodValue(@NotNull Fluid fluid) {
        return fluids.containsKey(id(fluid));
    }

    /**
     * Get the FluidStack of blood equivalent to the given fluid.
     *
     * @param fluid FluidStack
     * @return Impure blood amount in mB or 0
     */
    public static FluidStack getBloodFromFluid(@NotNull FluidStack fluid) {
        if (fluid.getFluid().isSame(VReference.BLOOD.get()))
            return fluid;
        return new FluidStack(VReference.BLOOD.get(), (int) (getBloodValue(fluid) * fluid.getAmount()));
    }

    public static ResourceLocation id(Item item) {
        return ForgeRegistries.ITEMS.getKey(item);
    }

    public static ResourceLocation id(Fluid block) {
        return ForgeRegistries.FLUIDS.getKey(block);
    }
}
