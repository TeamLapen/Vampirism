package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IEffectInstanceWithSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

public interface IEffectInstanceWithSourceMock extends IEffectInstanceWithSource {
    @Override
    default @Nullable MobEffectInstance vampirism$getHiddenEffect() {
        return null;
    }

    @Override
    default Set<ResourceLocation> vampirism$getProperties() {
        return Set.of();
    }

    @Override
    default boolean vampirism$hasProperty(@Nullable ResourceLocation source) {
        return false;
    }

    @Override
    default void vampirism$setProperties(Collection<ResourceLocation> sources) {

    }

    @Override
    default void vampirism$addProperty(@Nullable ResourceLocation source) {

    }

    @Override
    default boolean vampirism$hasProperties() {
        return false;
    }

    @Override
    default void vampirism$removeEffect() {

    }
}
