package de.teamlapen.vampirism.api.general;

import de.teamlapen.vampirism.api.datamaps.IFluidBloodConversion;
import de.teamlapen.vampirism.api.datamaps.IItemBlood;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

/**
 * Registry for blood conversion.
 * <br><br>
 * <p>
 * Converted are the following:<br>
 * - Items into impure blood<br>
 * - Fluids into blood<br>
 *
 * <br>
 * <p>
 * Values are loaded using <a href="https://docs.neoforged.net/docs/datamaps/">neoforge datamaps</a>:<br>
 * {@link de.teamlapen.vampirism.api.VampirismDataMaps#ITEM_BLOOD}<br>
 * {@link de.teamlapen.vampirism.api.VampirismDataMaps#FLUID_BLOOD_CONVERSION}
 * <br>
 * <br>
 * Entity blood values are handled by {@link de.teamlapen.vampirism.api.entity.IVampirismEntityRegistry}
 */
public interface IBloodConversionRegistry {

    /**
     * @implNote a default {@link net.minecraft.world.item.ItemStack} will be created to check the blood value
     * @deprecated use {@link net.minecraft.world.item.ItemStack} sensitive variant {@link #getItemBlood(ItemStack)}
     */
    @Deprecated
    int getImpureBloodValue(@NotNull Item item);

    /**
     * Checks if the item can be converted into impure blood
     *
     * @param stack the item that should be converted
     * @return {@code true} if the item can be converted
     */
    boolean canBeConverted(@NotNull ItemStack stack);

    /**
     * Gets a blood representation of the given item. This returns an instance of {@link IItemBlood} which can be used to get the blood value of the item.
     * If an explicit conversion exists, that will be used. Otherwise, it tries to create a calculated value.
     *
     * @param stack the item for which the blood representation should be returned
     * @return an {@link IItemBlood} object for the given item
     * @implNote the calculation is limited by checking the {@link net.minecraft.tags.ItemTags#MEAT} method and checking for {@code "cooked"} in the item's registry name
     */
    @NotNull
    IItemBlood getItemBlood(@NotNull ItemStack stack);

    /**
     * Gets the conversion rate of the fluid into blood
     *
     * @param fluid the fluid that should be converted
     * @return the conversion rate
     * @implSpec if no explicit conversion exists, a default conversion rate of 0 is returned
     */
    @NotNull
    IFluidBloodConversion getFluidConversion(@NotNull Fluid fluid);

    /**
     * Checks if the fluid can be converted to blood
     *
     * @param fluid the fluid that should be converted
     * @return {@code true} if the fluid can be converted
     */
    boolean hasConversion(@NotNull Fluid fluid);

    /**
     * Transforms the given fluid into blood. If there is conversion, the returned stack will be empty.
     *
     * @param fluid the fluid stack that should be converted
     * @return a fluid stack representing the blood
     */
    @NotNull
    FluidStack getBloodFromFluid(@NotNull FluidStack fluid);

}
