package de.teamlapen.vampirism.inventory;

import de.teamlapen.lib.lib.util.FluidLib;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.items.IAlchemicalCauldronRecipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * 1.10
 *
 * @author maxanier
 */
public class AlchemicalCauldronRecipe implements IAlchemicalCauldronRecipe {
    private static final ISkill[] EMPTY_SKILLS = {};
    private final ItemStack output;
    private final ItemStack ingredient;
    private final FluidStack fluidStack;
    private final ItemStack fluidItem;
    @Nullable
    private ISkill<IHunterPlayer>[] skills = null;
    private int reqLevel = 0;
    private int cookingTime = 200;
    private int experience;


    AlchemicalCauldronRecipe(ItemStack liquid, ItemStack ingredient, ItemStack output) {
        if (liquid.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
            IFluidHandler handler = liquid.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            FluidStack stack = handler.drain(Integer.MAX_VALUE, false);
            if (stack != null) {
                VampirismMod.log.d("AlchemicalCauldron", "Replaced %s liquid item with %s fluid stack", liquid, stack);
                fluidStack = stack;
                fluidItem = null;
            } else {
                VampirismMod.log.d("AlchemicalCauldron", "Could not extract fluid from fluid container item %s", liquid);
                fluidStack = null;
                fluidItem = liquid;
            }
        } else {
            fluidItem = liquid;
            fluidStack = null;
        }
        this.ingredient = ingredient;
        if (output == null)
            throw new IllegalArgumentException("AlchemicalCauldron: Output cannot be null (" + liquid + "," + ingredient + ")");
        this.output = output;
    }

    AlchemicalCauldronRecipe(FluidStack fluidStack, ItemStack ingredient, ItemStack output) {
        this.fluidStack = fluidStack;
        this.fluidItem = null;
        this.ingredient = ingredient;
        if (output == null)
            throw new IllegalArgumentException("AlchemicalCauldron: Output cannot be null (" + fluidStack + "," + ingredient + ")");
        this.output = output;
    }

    @Override
    public boolean areSameIngredients(IAlchemicalCauldronRecipe recipe) {
        if (recipe instanceof AlchemicalCauldronRecipe) {
            AlchemicalCauldronRecipe r2 = (AlchemicalCauldronRecipe) recipe;
            if (ItemStack.areItemStacksEqual(r2.fluidItem, fluidItem) && ItemStack.areItemStacksEqual(r2.ingredient, ingredient) && FluidLib.areFluidStacksEqual(r2.fluidStack, fluidStack)) {
                return true;
            }
        }
        return false;

    }

    @Override
    public boolean canBeCooked(int level, ISkillHandler<IHunterPlayer> skillHandler) {
        if (level < reqLevel) return false;
        if (skills == null) return true;
        for (ISkill<IHunterPlayer> s : skills) {
            if (!skillHandler.isSkillEnabled(s)) return false;
        }
        return true;
    }

    @Override
    public IAlchemicalCauldronRecipe configure(int ticks, int exp, int reqLevel, @Nullable ISkill<IHunterPlayer>... reqSkills) {
        return setCookingTime(ticks).setExperience(exp).setRequirements(reqLevel, reqSkills);
    }

    @Override
    public int getCookingTime() {
        return cookingTime;
    }

    @Override
    public float getExperience() {
        return experience;
    }

    @Nullable
    @Override
    public ItemStack getIngredient() {
        return ingredient;
    }

    @Nonnull
    @Override
    public ItemStack getOutput() {
        return output;
    }

    @Override
    public int getRequiredLevel() {
        return reqLevel;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public ISkill<IHunterPlayer>[] getRequiredSkills() {
        return (skills == null) ? EMPTY_SKILLS : skills;
    }

    @Nullable
    @Override
    public FluidStack isValidFluidItem(ItemStack stack) {
        if (fluidStack == null) return null;
        if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
            IFluidHandler handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            FluidStack drained = handler.drain(fluidStack, false);
            if (drained == null || !drained.isFluidStackIdentical(fluidStack)) {
                return null;
            }
            return fluidStack.copy();
        }
        return null;
    }

    @Nullable
    @Override
    public FluidStack isValidFluidStack(FluidStack stack) {
        return (fluidStack != null && stack != null && stack.containsFluid(fluidStack)) ? fluidStack.copy() : null;
    }

    @Override
    public boolean isValidLiquidItem(ItemStack stack) {
        return fluidItem != null && stack != null && UtilLib.doesStackContain(stack, fluidItem);
    }

    @Override
    public IAlchemicalCauldronRecipe setCookingTime(int ticks) {
        cookingTime = ticks;
        return this;
    }

    @Override
    public IAlchemicalCauldronRecipe setExperience(int exp) {
        experience = exp;
        return this;
    }

    @Override
    public IAlchemicalCauldronRecipe setRequirements(int reqLevel, @Nullable ISkill<IHunterPlayer>... reqSkills) {
        this.reqLevel = reqLevel;
        this.skills = reqSkills;
        return this;
    }

    @Override
    public String toString() {
        return "AlchemicalCauldronRecipe{" +
                "cookingTime=" + cookingTime +
                ", skills=" + Arrays.toString(skills) +
                ", output=" + output +
                ", ingredient=" + ingredient +
                ", reqLevel=" + reqLevel +
                ", experience=" + experience +
                ", fluidStack=" + fluidStack +
                ", fluidItem=" + fluidItem +
                '}';
    }
}
