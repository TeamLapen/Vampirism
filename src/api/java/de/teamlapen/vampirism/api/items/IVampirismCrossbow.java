package de.teamlapen.vampirism.api.items;

import com.google.errorprone.annotations.ForOverride;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * @deprecated use {@link de.teamlapen.vampirism.api.items.ICrossbow} or {@link de.teamlapen.vampirism.api.items.IHunterCrossbow} instead
 */
@Deprecated(forRemoval = true)
public interface IVampirismCrossbow extends ICrossbow {

    boolean performShootingMod(Level level, LivingEntity shooter, InteractionHand hand, ItemStack stack, float speed, float angle);

    @Override
    default boolean performShooting(Level level, LivingEntity shooter, InteractionHand hand, ItemStack stack, float speed, float angle) {
        return performShootingMod(level, shooter, hand, stack, speed, angle);
    }

    int getChargeDurationMod(ItemStack crossbow);

    @Override
    default int getChargeDuration(ItemStack crossbow) {
        return getChargeDurationMod(crossbow);
    }
}
