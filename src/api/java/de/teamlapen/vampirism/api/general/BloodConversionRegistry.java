package de.teamlapen.vampirism.api.general;

import com.google.common.collect.Maps;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    /**
     * stores conversion rate from items to impure blood during resource reload
     */
    private @Nullable
    static Map<ResourceLocation, Integer> items_temp;
    /**
     * stores conversion rate from fluids to blood during resource reload
     */
    private @Nullable
    static Map<ResourceLocation, Integer> fluids_temp;

    /**
     * process new fluid values
     * replaces old values with values loaded from datapack
     */
    @Deprecated
    public static void processFluids(int divider) {
        fluids.clear();
        for (Map.Entry<ResourceLocation, Integer> entry : fluids_temp.entrySet()) {
            fluids.put(entry.getKey(), ((float) entry.getValue()) / divider);
        }
        fluids_temp = null;
    }

    /**
     * prepare for new fluid values
     */
    @Deprecated
    public static void prepareFluids() {
        fluids_temp = Maps.newConcurrentMap();
    }

    /**
     * process new item values
     * replaces old values with values loaded from datapack
     */
    @Deprecated
    public static void processItems(int multiplier) {
        items.clear();
        for (Map.Entry<ResourceLocation, Integer> entry : items_temp.entrySet()) {
            items.put(entry.getKey(), entry.getValue() * multiplier);
        }
        items_temp = null;
    }

    /**
     * prepare for new item values
     */
    @Deprecated
    public static void prepareItems() {
        items_temp = Maps.newConcurrentMap();
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

    /**
     * Get the amount of blood the given fluid is worth.
     *
     * @param fluid FluidStack
     * @return Impure blood amount in mB or 0
     */
    //TODO 1.14 fluid
    public static float getBloodValue(@Nonnull FluidStack fluid) {
//        if(fluids.containsKey(fluid.getFluid().getRegistryName())){
//            return fluids.get(fluid.getFluid().getRegistryName());
//        }
        return 0f;
    }
//    /**
//     * Get the FluidStack of blood equivalent to the given fluid.
//     *
//     * @param fluid FluidStack
//     * @return Impure blood amount in mB or 0
//     */
//    public static FluidStack getBloodFromFluid(@Nonnull FluidStack fluid){
//        if(fluid == ModFluids.blood)return fluid;
//        return new FluidStack(ModFluids.blood,getBloodValue(fluid));
//    }

    /**
     * Register the impure blood amount for the given item.
     * This affects any stack of the given item regardless of meta or NBT
     *
     * @param amount Impure blood amount in mB
     */
    public static void registerItem(ResourceLocation itemId, int amount) {
        items_temp.put(itemId, amount);
    }

    /**
     * Register the impure blood amounts for the given items.
     * This affects any stack of the given items regardless of meta or NBT
     *
     * @param items map of items to impure blood amount in mB
     */
    public static void registerItems(Map<ResourceLocation, Integer> items) {
        for (Map.Entry<ResourceLocation, Integer> item : items.entrySet()) {
            registerItem(item.getKey(), item.getValue());
        }
    }

    /**
     * Register the impure blood amount for the given fluid.
     * This affects any stack of the given fluid regardless of meta or NBT
     *
     * @param amount Impure blood amount in mB
     */
    public static void registerFluid(ResourceLocation itemId, int amount) {
        fluids_temp.put(itemId, amount);
    }

    /**
     * Register the blood amounts for the given fluids.
     * This affects any stack of the given fluids regardless of meta or NBT
     *
     * @param fluids map of item to impure blood amount in mB
     */
    public static void registerFluids(Map<ResourceLocation, Integer> fluids) {
        for (Map.Entry<ResourceLocation, Integer> item : fluids.entrySet()) {
            registerFluid(item.getKey(), item.getValue());
        }
    }
}
