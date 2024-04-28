package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.api.items.oil.IApplicableOil;
import de.teamlapen.vampirism.api.items.oil.IArmorOil;
import de.teamlapen.vampirism.items.component.AppliedOilContent;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class OilUtils {

    public static Collection<IArmorOil> getEquippedArmorOils(Player player) {
        return StreamSupport.stream(player.getArmorSlots().spliterator(), false).map(OilUtils::getAppliedOil).filter(o -> o.isPresent() && o.get() instanceof IArmorOil).map(iApplicableOil -> (IArmorOil)iApplicableOil.get()).collect(Collectors.toList());
    }

    public static @NotNull Optional<IApplicableOil> getAppliedOil(@NotNull ItemStack stack) {
        return AppliedOilContent.getAppliedOil(stack).map(AppliedOilContent::oil).map(Holder::value);
    }

    public static boolean reduceAppliedOilDuration(@NotNull ItemStack stack) {
        return AppliedOilContent.getAppliedOil(stack).map(appliedOil -> {
            int newDuration = appliedOil.duration() - appliedOil.oil().value().getDurationReduction();
            if (newDuration > 0) {
                AppliedOilContent.apply(stack, appliedOil.oil(), newDuration);
                return false;
            } else {
                AppliedOilContent.remove(stack);
                return true;
            }
        }).orElse(true);
    }

    public static boolean reduceAppliedOilDuration(@NotNull ItemStack stack, IApplicableOil oil, int durationReduction) {
        return AppliedOilContent.getAppliedOil(stack).filter(s -> s.oil().value() == oil).map(appliedOil -> {
            int newDuration = appliedOil.duration() - durationReduction;
            if (newDuration > 0) {
                AppliedOilContent.apply(stack, appliedOil.oil(), newDuration);
                return false;
            } else {
                AppliedOilContent.remove(stack);
                return true;
            }
        }).orElse(true);
    }

}
