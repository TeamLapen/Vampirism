package de.teamlapen.vampirism.api.items.oil;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.Optional;

public interface IApplicableOil extends IOil {

    /**
     * checks if the oil can be applied to the stack
     *
     * @return true if it is possible
     */
    boolean canBeApplied(ItemStack stack);

    /**
     * gets the current duration of the oil for the given itemstack
     */
    int getDuration(ItemStack stack);

    /**
     * gets the maximum duration of the oil for the given itemstack
     */
    int getMaxDuration(ItemStack stack);

    /**
     * Reduced the duration of the applied oil for the given itemstack.
     *
     * @param stack itemStack which oil duration should be reduced
     * @param amount the amount by which the duration should be reduced
     * @return true if the oil is worn off
     */
    boolean reduceDuration(ItemStack stack, IApplicableOil oil, int amount);

    /**
     * whether the oil is permanent or has a duration
     */
    boolean hasDuration();

    /**
     * determines how much the duration is reduced per reduction
     *
     * @return duration reduction
     */
    int getDurationReduction();

    /**
     * creates an optional tooltip entry for an item which has this oil applied to
     */
    Optional<Component> getToolTipLine(ItemStack stack, IApplicableOil oil, int duration, TooltipFlag flag);
}
