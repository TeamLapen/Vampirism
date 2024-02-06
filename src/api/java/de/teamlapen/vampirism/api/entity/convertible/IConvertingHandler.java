package de.teamlapen.vampirism.api.entity.convertible;

import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;

/**
 * Handles conversion of entities
 */
public interface IConvertingHandler<T extends PathfinderMob> {

    /**
     * @param entity Creature to be converted
     * @return A converted creature that will replace the old entity
     */
    @Nullable
    IConvertedCreature<T> createFrom(T entity);

    default void updateEntityAttributes(PathfinderMob creature) {

    }

    /**
     * If Vampirism's default converted creature is used, this can be used to specify some properties of the converted creature
     */
    interface IDefaultHelper {

        Map<Attribute, com.mojang.datafixers.util.Pair<FloatProvider,Double>> getAttributeModifier();
    }
}
