package de.teamlapen.vampirism.api.entity.effect;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for {@link MobEffectInstance} to supply source identifier for the instance.<br>
 * <br>
 * This interface will be implemented using mixins
 */
public interface EffectInstanceWithSource {

    /**
     * @return the hidden effect of the effect instance
     */
    @Nullable
    MobEffectInstance vampirism$getHiddenEffect();

    /**
     * @return the source of this effect instance
     */
    @Nullable
    ResourceLocation vampirism$getSource();

    /**
     * sets the source of this effect instance
     *
     * @param source the id of the source
     */
    void vampirism$setSource(@Nullable ResourceLocation source);

    /**
     * @return if this effect instance has a defined source
     */
    boolean vampirism$hasSource();

    /**
     * remove this effect instance from the entity
     *
     * @implNote this will set the effect duration to 1
     */
    void vampirism$removeEffect();

    static void removePotionEffect(@NotNull LivingEntity entity, @NotNull Holder<MobEffect> effect, @NotNull ResourceLocation source) {
        MobEffectInstance ins = entity.getEffect(effect);
        while (ins != null) {
            EffectInstanceWithSource insM = ((EffectInstanceWithSource) ins);
            if (insM.vampirism$hasSource()) {
                if (insM.vampirism$getSource().equals(source)) {
                    insM.vampirism$removeEffect();
                    break;
                }
            }
            ins = insM.vampirism$getHiddenEffect();
        }
    }
}
