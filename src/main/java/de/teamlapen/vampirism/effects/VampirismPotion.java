package de.teamlapen.vampirism.effects;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;

import org.jetbrains.annotations.Nullable;
import java.util.Optional;

public class VampirismPotion extends Potion {
    public static Optional<VampirismPotion.HunterPotion> isHunterPotion(ItemStack stack, boolean onlyNormal) {
        if (stack.getItem() == Items.POTION || (!onlyNormal && (stack.getItem() == Items.LINGERING_POTION || stack.getItem() == Items.SPLASH_POTION))) {
            Potion potion = PotionUtils.getPotion(stack);
            if (potion instanceof VampirismPotion.HunterPotion) {
                return Optional.of((VampirismPotion.HunterPotion) potion);
            }
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
