package de.teamlapen.vampirism.potion;

import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * Potion which replaces the vanilla night vision one.
 */
public class VampirismNightVisionPotion extends Potion {

    public VampirismNightVisionPotion() {
        super(false, 2039713);
        setIconIndex(4, 1);
        setPotionName("effect.nightVision");
        VampirismMod.log.w("NightVision", "-------Please ignore the following error!------");
        this.setRegistryName("minecraft", "night_vision");
        VampirismMod.log.w("NightVision", "-----------------------------------------------");
    }

    @Override
    public boolean shouldRender(PotionEffect effect) {
        return !(effect instanceof VampireNightVisionEffect) && super.shouldRender(effect);
    }
}
