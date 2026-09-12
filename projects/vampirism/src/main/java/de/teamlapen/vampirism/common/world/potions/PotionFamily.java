package de.teamlapen.vampirism.common.world.potions;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;

import java.util.List;

public record PotionFamily(String name, List<Effect> effects) {

    public static PotionFamily single(String name, Holder<MobEffect> effect, int base, int longDuration, int strongDuration, int strongAmplifier, int amplifierStep, boolean instant) {
        return new PotionFamily(name, List.of(new Effect(effect, base, longDuration, strongDuration, 0, strongAmplifier, amplifierStep, instant)));
    }

    public record Effect(Holder<MobEffect> effect, int base, int longDuration, int strongDuration, int baseAmplifier, int strongAmplifier, int amplifierStep, boolean instant) {}
}
