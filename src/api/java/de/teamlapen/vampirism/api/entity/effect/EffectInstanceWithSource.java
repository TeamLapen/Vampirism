package de.teamlapen.vampirism.api.entity.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.Contract;
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
    MobEffectInstance getHiddenEffect();

    /**
     * @return the source of this effect instance
     */
    @Nullable
    ResourceLocation getSource();

    /**
     * sets the source of this effect instance
     *
     * @param source the id of the source
     */
    void setSource(@Nullable ResourceLocation source);

    /**
     * @return if this effect instance has a defined source
     */
    boolean hasSource();

    /**
     * remove this effect instance from the entity
     *
     * @implNote this will set the effect duration to 1
     */
    void removeEffect();
}
