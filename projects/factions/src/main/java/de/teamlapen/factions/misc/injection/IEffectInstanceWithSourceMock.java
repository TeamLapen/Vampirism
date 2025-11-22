package de.teamlapen.factions.misc.injection;

import de.teamlapen.factions.misc.extensions.IEffectInstanceWithSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

public interface IEffectInstanceWithSourceMock extends IEffectInstanceWithSource {
    @Override
    default @Nullable MobEffectInstance factions$getHiddenEffect() {
        return null;
    }

    @Override
    default Set<ResourceLocation> factions$getProperties() {
        return Set.of();
    }

    @Override
    default boolean factions$hasProperty(@Nullable ResourceLocation source) {
        return false;
    }

    @Override
    default void factions$setProperties(Collection<ResourceLocation> sources) {

    }

    @Override
    default void factions$addProperty(@Nullable ResourceLocation source) {

    }

    @Override
    default boolean factions$hasProperties() {
        return false;
    }

    @Override
    default void factions$removeEffect() {

    }
}
