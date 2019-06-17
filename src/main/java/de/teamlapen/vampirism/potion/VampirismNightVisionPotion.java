package de.teamlapen.vampirism.potion;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Potion which replaces the vanilla night vision one.
 */
public class VampirismNightVisionPotion extends Potion {

    private final static Logger LOGGER = LogManager.getLogger(VampirismNightVisionPotion.class);
    public VampirismNightVisionPotion() {
        super(false, 2039713);
        setIconIndex(4, 1);
        LOGGER.warn("-------Please ignore the following error!------");
        this.setRegistryName("minecraft", "night_vision");
        LOGGER.warn("-----------------------------------------------");
    }

    @Override
    public boolean shouldRender(PotionEffect effect) {
        return !(effect instanceof VampireNightVisionEffect) && super.shouldRender(effect);
    }
}
