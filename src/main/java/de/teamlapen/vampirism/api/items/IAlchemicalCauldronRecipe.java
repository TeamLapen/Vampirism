package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Recipe for Alchemical Cauldron
 */
public interface IAlchemicalCauldronRecipe {
    /**
     * @return if the ingredients are the same
     */
    boolean areSameIngredients(IAlchemicalCauldronRecipe recipe);

    /**
     * @return If the player can craft this recipe
     */
    boolean canBeCooked(int level, ISkillHandler<IHunterPlayer> skillHandler);

    /**
     * @return This
     */
    IAlchemicalCauldronRecipe configure(int ticks, int exp, int reqLevel, @Nullable ISkill<IHunterPlayer>... reqSkills);

    /**
     * Either the default value or a custom set one.
     *
     * @return The time this recipe takes
     */
    int getCookingTime();

    /**
     * @param ticks Cooking ticks
     * @return This
     */
    IAlchemicalCauldronRecipe setCookingTime(int ticks);

    /**
     * @return A stack that describes the required fluid stack. E.g. a bucket with a custom text
     */
    ItemStack getDescriptiveFluidStack();

    /**
     * Either the default value or a custom set one.
     *
     * @return The experience this recipe gives
     */
    float getExperience();

    /**
     * @return This
     */
    IAlchemicalCauldronRecipe setExperience(int exp);

    /**
     * Allows META wildcard
     *
     * @return The input item
     */
    @Nullable
    ItemStack getIngredient();

    /**
     * @return The output of this recipe
     */
    @Nonnull
    ItemStack getOutput();

    /**
     * @return The required hunter level
     */
    int getRequiredLevel();

    /**
     * @return The skills required for this recipe
     */
    @Nonnull
    ISkill<IHunterPlayer>[] getRequiredSkills();

    /**
     * Checks if the given stack has a {@link CapabilityFluidHandler#FLUID_HANDLER_CAPABILITY} and contains the required fluid, if so it returns the required Fluid Stack otherwise null.
     *
     * @param stack
     * @return If nonnull the ItemStack has a {@link CapabilityFluidHandler#FLUID_HANDLER_CAPABILITY} and contains the required (and returned) fluid.
     */
    @Nullable
    FluidStack isValidFluidItem(ItemStack stack);

    /**
     * Checks if the given stack contains the required stack and if so, returns the required fluid stack
     */
    @Nullable
    FluidStack isValidFluidStack(FluidStack stack);

    /**
     * Used for items without {@link CapabilityFluidHandler#FLUID_HANDLER_CAPABILITY}
     *
     * @return If the given stack contains the required 'liquid' item stack
     */
    boolean isValidLiquidItem(ItemStack stack);

    /**
     * Set the requirements
     *
     * @return This
     */
    IAlchemicalCauldronRecipe setRequirements(int reqLevel, @Nullable ISkill<IHunterPlayer>... reqSkills);
}
