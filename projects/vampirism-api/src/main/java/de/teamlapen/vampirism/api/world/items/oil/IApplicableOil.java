package de.teamlapen.vampirism.api.world.items.oil;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IApplicableOil extends IOil {

    /**
     * checks if the oil can be applied to the stack
     *
     * @return true if it is possible
     */
    boolean canBeApplied(ItemInstance stack);

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
     * @param stack  itemStack which oil duration should be reduced
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
    default int getDurationReduction() {
        return 1;
    }

    Component getDescriptionTitle();

    List<Component> getEffectDescription(@Nullable Item.TooltipContext context);
}
