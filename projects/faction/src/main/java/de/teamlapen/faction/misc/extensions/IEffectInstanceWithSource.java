package de.teamlapen.faction.misc.extensions;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
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

    Set<Identifier> factions$getProperties();

    boolean factions$hasProperty(@Nullable Identifier source);

    void factions$setProperties(Collection<Identifier> sources);

    void factions$addProperty(@Nullable Identifier source);

    boolean factions$hasProperties();

    /**
     * remove this effect instance from the entity
     *
     * @implNote this will set the effect duration to 1
     */
    void factions$removeEffect();

    static void removePotionEffect(@NotNull LivingEntity entity, @NotNull Holder<MobEffect> effect, @NotNull Identifier source) {
        MobEffectInstance ins = entity.getEffect(effect);
        while (ins != null) {
            if (ins.factions$getProperties().contains(source)) {
                ins.factions$removeEffect();
                break;
            }
            ins = ins.factions$getHiddenEffect();
        }
    }
}
