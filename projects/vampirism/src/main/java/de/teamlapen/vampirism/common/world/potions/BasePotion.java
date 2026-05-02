package de.teamlapen.vampirism.common.world.potions;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BasePotion extends Potion {

    public BasePotion(@Nullable String baseName, MobEffectInstance... effects) {
        super(baseName, effects);
    }

    public static Optional<BasePotion.HunterPotion> isHunterPotion(ItemStack stack, boolean onlyNormal) {
        if (stack.getItem() == Items.POTION || (!onlyNormal && (stack.getItem() == Items.LINGERING_POTION || stack.getItem() == Items.SPLASH_POTION))) {
            PotionContents potionContents = stack.get(DataComponents.POTION_CONTENTS);
            return Optional.ofNullable(potionContents).flatMap(PotionContents::potion).map(Holder::value).filter(HunterPotion.class::isInstance).map(HunterPotion.class::cast);
        }
        return Optional.empty();
    }

    public static boolean isHunterPotion(Holder<Potion> potion) {
        return Optional.ofNullable(potion).map(Holder::value).stream().anyMatch(HunterPotion.class::isInstance);
    }

    public static class HunterPotion extends BasePotion {

        public HunterPotion(@Nullable String baseName, MobEffectInstance... effects) {
            super(baseName, effects);
        }
    }
}
