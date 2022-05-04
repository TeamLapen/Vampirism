package de.teamlapen.vampirism.api.items.oil;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import java.util.Optional;

public interface IApplicableOil extends IOil {

    boolean canBeApplied(ItemStack stack);

    int getDuration(ItemStack stack);

    int getMaxDuration(ItemStack stack);

    /**
     * Reduced the duration of the applied oil for the given itemstack.
     *
     * @param stack itemStack which oil duration should be reduced
     * @param amount the amount by which the duration should be reduced
     * @return true if the oil is worn off
     */
    boolean reduceDuration(ItemStack stack, IApplicableOil oil, int amount);

    boolean hasDuration();

    Optional<ITextComponent> getToolTipLine(ItemStack stack, IApplicableOil oil, int duration);
}
