package de.teamlapen.vampirism.effects;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class VampirismPotion extends Potion {
    public static @NotNull Optional<VampirismPotion.HunterPotion> isHunterPotion(@NotNull ItemStack stack, boolean onlyNormal) {
        if (stack.getItem() == Items.POTION || (!onlyNormal && (stack.getItem() == Items.LINGERING_POTION || stack.getItem() == Items.SPLASH_POTION))) {
            PotionContents potionContents = stack.get(DataComponents.POTION_CONTENTS);
            return Optional.ofNullable(potionContents).flatMap(PotionContents::potion).map(Holder::value).filter(HunterPotion.class::isInstance).map(HunterPotion.class::cast);
        }
        return Optional.empty();
    }

    public VampirismPotion(@Nullable String baseName, MobEffectInstance... effects) {
        super(baseName, effects);
    }

    public static class HunterPotion extends VampirismPotion {
        public HunterPotion(@Nullable String baseName, MobEffectInstance... effects) {
            super(baseName, effects);
        }
    }
}
