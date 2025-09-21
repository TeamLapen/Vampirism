package de.teamlapen.vampirism.common.effects;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.effect.EffectInstanceWithSource;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.util.Helper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ModEffectInstanceHelper {

    public static MobEffectInstance createSanguinare(int duration) {
        return new MobEffectInstance(ModEffects.SANGUINARE, duration, 0, false, true) {
            @Override
            public boolean update(MobEffectInstance other) {
                return false;
            }

            @Override
            public boolean tick(LivingEntity entity, Runnable onExpirationRunnable) {
                if (this.getDuration() % 10 == 0 && entity instanceof Player) {
                    if (!Helper.canBecomeVampire((Player) entity)) {
                        return false;
                    }
                }
                return super.tick(entity, onExpirationRunnable);
            }
        };
    }

    public static MobEffectInstance createNightVision() {
        MobEffectInstance source = new MobEffectInstance(MobEffects.NIGHT_VISION, -1, 0, false, false, false) {
            @Override
            public boolean tick(LivingEntity entity, Runnable onExpirationRunnable) {
                return true;
            }

            @Override
            public boolean update(MobEffectInstance other) {
                return false;
            }

            @Override
            public boolean equals(Object other) {
                return other == this;
            }
        };
        return withSource(source, VReference.PERMANENT_INVISIBLE_MOB_EFFECT, VReference.VAMPIRE_NIGHT_VISION_EFFECT);
    }


    public static MobEffectInstance withSource(MobEffectInstance instance, ResourceLocation... source) {
        addSource(instance, source);
        return instance;
    }

    public static void addSource(MobEffectInstance instance, ResourceLocation... source) {
        ((EffectInstanceWithSource) instance).vampirism$setProperties(Arrays.stream(source).toList());
    }

    public static boolean hasSource(@Nullable MobEffectInstance instance, ResourceLocation source) {
        if (instance == null) return false;
        return ((EffectInstanceWithSource) instance).vampirism$getProperties().contains(source);
    }
}
