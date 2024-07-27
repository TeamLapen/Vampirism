package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.client.extensions.EffectExtensions;
import de.teamlapen.vampirism.core.ModEffects;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

public class ModClientEffects {

    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerMobEffect(EffectExtensions.NIGHT_VISION, MobEffects.NIGHT_VISION.value());
        event.registerMobEffect(EffectExtensions.POISON, ModEffects.POISON.value());
        event.registerMobEffect(EffectExtensions.SANGUINARE, ModEffects.SANGUINARE.value());
    }
}
