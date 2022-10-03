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
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

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

    public static void applyNewFluidResources(@NotNull Map<ResourceLocation, Float> values) {
        fluids.clear();
        fluids.putAll(values);
    }

    public static void applyNewEntitiesResources(@NotNull Map<ResourceLocation, Float> values) {
        entities.clear();
        entities.putAll(values);
    }

    public static void applyNewItemResources(@NotNull Map<ResourceLocation, Float> values) {
        items.clear();
        for (Map.Entry<ResourceLocation, Float> value : values.entrySet()) {
            if (value.getValue() != 0) {
                items.put(value.getKey(), value.getValue());
            } else {
                items_blacklist.add(value.getKey());
            }
        }
    }

    public static @NotNull Map<ResourceLocation, Float> getItemConversions() {
        return Collections.unmodifiableMap(items);
    }

    public static @NotNull Map<ResourceLocation, Float> getEntityConversions() {
        return Collections.unmodifiableMap(entities);
    }

    public static @NotNull Map<ResourceLocation, Float> getFluidConversions() {
        return Collections.unmodifiableMap(fluids);
    }

    public static @NotNull Map<ResourceLocation, Float> getItemConversionCalculated() {
        return Collections.unmodifiableMap(items_calculated);
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
    public static @NotNull FluidStack getBloodFromFluid(@NotNull FluidStack fluid) {
        if (fluid.getFluid().isSame(VReference.BLOOD.get())) {
            return fluid;
        }
        return new FluidStack(VReference.BLOOD.get(), (int) (getBloodValue(fluid) * fluid.getAmount()));
    }

    public static @Nullable ResourceLocation id(Item item) {
        return ForgeRegistries.ITEMS.getKey(item);
    }

    public static @Nullable ResourceLocation id(Fluid block) {
        return ForgeRegistries.FLUIDS.getKey(block);
    }
}
