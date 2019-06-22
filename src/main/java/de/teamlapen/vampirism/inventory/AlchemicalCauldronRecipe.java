package de.teamlapen.vampirism.inventory;

import de.teamlapen.lib.lib.util.FluidLib;
import de.teamlapen.lib.lib.util.ItemStackUtil;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.items.IAlchemicalCauldronRecipe;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * 1.10
 *
 * @author maxanier
 */
public class AlchemicalCauldronRecipe implements IAlchemicalCauldronRecipe {
    private final static Logger LOGGER = LogManager.getLogger(AlchemicalCauldronRecipe.class);
    private static final ISkill[] EMPTY_SKILLS = {};
    @Nonnull
    private final ItemStack output;
    @Nonnull
    private final ItemStack ingredient;
    private final FluidStack fluidStack;
    private final ItemStack fluidItem;
    @Nullable
    private ISkill[] skills = null;
    private int reqLevel = 0;
    private int cookingTime = 400;
    private float experience = 0.2F;
    @Nonnull
    private ItemStack descriptiveStack = ItemStack.EMPTY;


    AlchemicalCauldronRecipe(@Nonnull ItemStack output, ItemStack liquid, @Nonnull ItemStack ingredient) {
        IFluidHandler handler = FluidLib.getFluidItemCap(liquid).orElse(null);
        if (handler != null) {
            FluidStack stack = handler.drain(Integer.MAX_VALUE, false);
            if (stack != null) {
                LOGGER.debug("Replaced {} liquid item with {} fluid stack", liquid, stack);
                fluidStack = stack;
                fluidItem = ItemStack.EMPTY;
                descriptiveStack = liquid;
            } else {
                LOGGER.debug("Could not extract fluid from fluid container item {}", liquid);
                fluidStack = null;
                fluidItem = liquid;
            }
        } else {
            fluidItem = liquid;
            fluidStack = null;
        }
        this.ingredient = ingredient;
        if (output.isEmpty())
            throw new IllegalArgumentException("AlchemicalCauldron: Output cannot be empty (" + liquid + "," + ingredient + ")");
        this.output = output;
    }

    AlchemicalCauldronRecipe(@Nonnull ItemStack output, FluidStack fluidStack, @Nonnull ItemStack ingredient) {
        this.fluidStack = fluidStack;
        this.fluidItem = ItemStack.EMPTY;
        this.ingredient = ingredient;
        if (output.isEmpty())
            throw new IllegalArgumentException("AlchemicalCauldron: Output cannot be empty (" + fluidStack + "," + ingredient + ")");
        this.output = output;
    }

    @Override
    public boolean areSameIngredients(IAlchemicalCauldronRecipe recipe) {
        if (recipe instanceof AlchemicalCauldronRecipe) {
            AlchemicalCauldronRecipe r2 = (AlchemicalCauldronRecipe) recipe;
            return ItemStack.areItemStacksEqual(r2.fluidItem, fluidItem) && ItemStack.areItemStacksEqual(r2.ingredient, ingredient) && FluidLib.areFluidStacksEqual(r2.fluidStack, fluidStack);
        }
        return false;

    }

    @Override
    public boolean canBeCooked(int level, ISkillHandler<IHunterPlayer> skillHandler) {
        if (level < reqLevel) return false;
        if (skills == null) return true;
        for (ISkill s : skills) {
            if (!skillHandler.isSkillEnabled(s)) return false;
        }
        return true;
    }

    @SafeVarargs
    @Override
    public final IAlchemicalCauldronRecipe configure(int ticks, float exp, int reqLevel, @Nullable ISkill... reqSkills) {
        return setCookingTime(ticks).setExperience(exp).setRequirements(reqLevel, reqSkills);
    }

    @Override
    public int getCookingTime() {
        return cookingTime;
    }

    @Nonnull
    @Override
    public ItemStack getDescriptiveFluidStack() {
        if (!descriptiveStack.isEmpty()) return descriptiveStack;
        if (!fluidItem.isEmpty()) return fluidItem;
        descriptiveStack = new ItemStack(Items.BUCKET);
        addFluidStackDescription(descriptiveStack, fluidStack);
        return descriptiveStack;
    }

    @Override
    public float getExperience() {
        return experience;
    }

    @Nonnull
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
    public ISkill[] getRequiredSkills() {
        return (skills == null) ? EMPTY_SKILLS : skills;
    }

    @Nullable
    @Override
    public FluidStack isValidFluidItem(@Nonnull ItemStack stack) {
        if (fluidStack == null) return null;
        return FluidLib.getFluidItemCap(stack).map(handler -> {
            FluidStack drained = handler.drain(fluidStack, false);
            if (drained == null || !drained.isFluidStackIdentical(fluidStack)) {
                return null;
            }
            return fluidStack.copy();
        }).orElse(null);


    }

    @Nullable
    @Override
    public FluidStack isValidFluidStack(FluidStack stack) {
        return (fluidStack != null && stack != null && stack.containsFluid(fluidStack)) ? fluidStack.copy() : null;
    }

    @Override
    public boolean isValidLiquidItem(@Nonnull ItemStack stack) {
        return !fluidItem.isEmpty() && !stack.isEmpty() && ItemStackUtil.doesStackContain(stack, fluidItem);
    }

    @Override
    public IAlchemicalCauldronRecipe setCookingTime(int ticks) {
        cookingTime = ticks;
        return this;
    }

    @Override
    public IAlchemicalCauldronRecipe setExperience(float exp) {
        experience = exp;
        return this;
    }

    @Override
    public IAlchemicalCauldronRecipe setRequirements(int reqLevel, @Nullable ISkill... reqSkills) {
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

    private void addFluidStackDescription(ItemStack stack, FluidStack fluidStack) {

        NBTTagCompound nbt = stack.getTag();
        if (nbt == null) nbt = new NBTTagCompound();
        NBTTagCompound display = nbt.contains("display", 10) ? nbt.getCompound("display") : new NBTTagCompound();
        NBTTagList lore = nbt.contains("Lore", 0) ? nbt.getList("Lore", 9) : new NBTTagList();
        lore.add(new NBTTagString(UtilLib.translate("text.vampirism.liquid_container")));
        display.put("Lore", lore);
        nbt.put("display", display);
        stack.setTag(nbt);

        stack.addEnchantment(Enchantments.UNBREAKING, 1);
        stack.setDisplayName(new TextComponentString(fluidStack.getLocalizedName() + ": " + fluidStack.amount + "mB"));
    }
}
