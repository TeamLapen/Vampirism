package de.teamlapen.factions.misc.injection;

import de.teamlapen.factions.misc.extensions.IEffectInstanceWithSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

@Deprecated
public interface IEffectInstanceWithSourceFactionsMock extends IEffectInstanceWithSource {
    @Override
    default @Nullable MobEffectInstance factions$getHiddenEffect() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default Set<ResourceLocation> factions$getProperties() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default boolean factions$hasProperty(@Nullable ResourceLocation source) {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default void factions$setProperties(Collection<ResourceLocation> sources) {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default void factions$addProperty(@Nullable ResourceLocation source) {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default boolean factions$hasProperties() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default void factions$removeEffect() {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
