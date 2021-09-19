package de.teamlapen.vampirism.effects;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;

import javax.annotation.Nullable;
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

    public VampirismPotion(String regName, @Nullable String baseName, EffectInstance... effects) {
        super(baseName, effects);
        this.setRegistryName(REFERENCE.MODID, regName);
    }

    public static class HunterPotion extends VampirismPotion {
        public HunterPotion(String regName, @Nullable String baseName, EffectInstance... effects) {
            super(regName, baseName, effects);
        }
    }
}