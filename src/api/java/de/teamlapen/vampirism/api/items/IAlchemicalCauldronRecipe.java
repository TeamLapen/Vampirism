package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Recipe for Alchemical Cauldron
 */
public interface IAlchemicalCauldronRecipe extends AbstractCookingRecipe {
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
    IAlchemicalCauldronRecipe configure(int ticks, float exp, int reqLevel, @Nullable ISkill... reqSkills);

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
    @Nonnull
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
    IAlchemicalCauldronRecipe setExperience(float exp);

    /**
     * Allows META wildcard
     *
     * @return The input item, can be EMPTY
     */
    @Nonnull
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
    ISkill[] getRequiredSkills();

    /**
     * Checks if the given stack has a {@link CapabilityFluidHandler#FLUID_HANDLER_ITEM_CAPABILITY} and contains the required fluid, if so it returns the required Fluid Stack otherwise null.
     *
     * @param stack may be EMPTY
     * @return If nonnull the ItemStack has a {@link CapabilityFluidHandler#FLUID_HANDLER_ITEM_CAPABILITY} and contains the required (and returned) fluid.
     */
    @Nullable
    FluidStack isValidFluidItem(@Nonnull ItemStack stack);

    /**
     * Checks if the given stack contains the required stack and if so, returns the required fluid stack
     */
    @Nullable
    FluidStack isValidFluidStack(FluidStack stack);

    /**
     * Used for items without {@link CapabilityFluidHandler#FLUID_HANDLER_ITEM_CAPABILITY}
     *
     * @param stack may be EMPTY
     * @return If the given stack contains the required 'liquid' item stack
     */
    boolean isValidLiquidItem(@Nonnull ItemStack stack);

    /**
     * Set the requirements
     * Only accepts ISkill<IHunterPlayer>.
     *
     * @return This
     */
    IAlchemicalCauldronRecipe setRequirements(int reqLevel, @Nullable ISkill... reqSkills);
}
