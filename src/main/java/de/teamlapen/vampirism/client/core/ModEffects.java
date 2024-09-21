package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.client.extensions.EffectExtensions;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

public class ModEffects {

    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerMobEffect(EffectExtensions.NIGHT_VISION, MobEffects.NIGHT_VISION.value());
        event.registerMobEffect(EffectExtensions.POISON, de.teamlapen.vampirism.core.ModEffects.POISON.get());
        event.registerMobEffect(EffectExtensions.SANGUINARE, de.teamlapen.vampirism.core.ModEffects.SANGUINARE.get());
    }
}
