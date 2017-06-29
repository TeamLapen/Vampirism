package de.teamlapen.vampirism.potion;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * Potion which replaces the vanilla night vision one.
 */
public class FakeNightVisionPotion extends Potion {



    public FakeNightVisionPotion() {
        super(false, 2039713);
        setIconIndex(4, 1);
        setPotionName("effect.nightVision2");
        this.setRegistryName("minecraft", "night_vision");
    }

    @Override
    public boolean shouldRender(PotionEffect effect) {
        if (effect instanceof FakeNightVisionPotionEffect) return false;
        return super.shouldRender(effect);
    }
}
