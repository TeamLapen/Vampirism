package de.teamlapen.vampirism.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectCategory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Potion which replaces the vanilla night vision one.
 */
public class VampirismNightVisionPotion extends MobEffect {

    private final static Logger LOGGER = LogManager.getLogger(VampirismNightVisionPotion.class);

    public VampirismNightVisionPotion() {
        super(MobEffectCategory.BENEFICIAL, 2039713);
        LOGGER.warn("-------Please ignore the following error!------");
        this.setRegistryName("minecraft", "night_vision");
        LOGGER.warn("-----------------------------------------------");
    }

    @Override
    public boolean shouldRender(MobEffectInstance effect) {
        return !(effect instanceof VampireNightVisionEffectInstance) && super.shouldRender(effect);
    }

    @Override
    public boolean shouldRenderHUD(MobEffectInstance effect) {
        return !(effect instanceof VampireNightVisionEffectInstance) && super.shouldRender(effect);
    }
}
