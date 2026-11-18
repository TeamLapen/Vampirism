package de.teamlapen.faction.common.world.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * Plain {@link MobEffect} without custom behaviour, e.g. for effects that only apply attribute modifiers
 */
public class SimpleMobEffect extends MobEffect {

    public SimpleMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    }
}
