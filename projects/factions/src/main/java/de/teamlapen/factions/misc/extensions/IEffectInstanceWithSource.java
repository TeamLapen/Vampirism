package de.teamlapen.factions.misc.extensions;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

/**
 * Interface for {@link MobEffectInstance} to supply source identifier for the instance.<br>
 * <br>
 * This interface will be implemented using mixins
 */
public interface IEffectInstanceWithSource {

    /**
     * @return the hidden effect of the effect instance
     */
    @Nullable
    MobEffectInstance factions$getHiddenEffect();

    Set<ResourceLocation> factions$getProperties();

    boolean factions$hasProperty(@Nullable ResourceLocation source);

    void factions$setProperties(Collection<ResourceLocation> sources);

    void factions$addProperty(@Nullable ResourceLocation source);

    boolean factions$hasProperties();

    /**
     * remove this effect instance from the entity
     *
     * @implNote this will set the effect duration to 1
     */
    void factions$removeEffect();

    static void removePotionEffect(@NotNull LivingEntity entity, @NotNull Holder<MobEffect> effect, @NotNull ResourceLocation source) {
        MobEffectInstance ins = entity.getEffect(effect);
        while (ins != null) {
            IEffectInstanceWithSource insM = ((IEffectInstanceWithSource) ins);
            if (insM.factions$hasProperties()) {
                if (insM.factions$getProperties().contains(source)) {
                    insM.factions$removeEffect();
                    break;
                }
            }
            ins = insM.factions$getHiddenEffect();
        }
    }
}
