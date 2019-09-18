package de.teamlapen.vampirism.potion;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Potion which replaces the vanilla night vision one.
 */
public class VampirismNightVisionPotion extends Effect {

    private final static Logger LOGGER = LogManager.getLogger(VampirismNightVisionPotion.class);

    public VampirismNightVisionPotion() {
        super(EffectType.BENEFICIAL, 2039713);
        LOGGER.warn("-------Please ignore the following error!------");
        this.setRegistryName("minecraft", "night_vision");
        LOGGER.warn("-----------------------------------------------");
    }

    @Override
    public boolean shouldRender(EffectInstance effect) {
        return !(effect instanceof VampireNightVisionEffect) && super.shouldRender(effect);
    }
}
