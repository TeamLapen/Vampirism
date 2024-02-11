package de.teamlapen.vampirism.api.entity.convertible;

import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Handles the actual conversion of entities
 */
public interface IConvertingHandler<T extends PathfinderMob> {

    /**
     * @param entity Creature to be converted
     * @return A converted creature that will replace the old entity
     */
    @Nullable
    IConvertedCreature<T> createFrom(T entity);

    /**
     * updates the attributes of the converted creature
     * @param creature the converted creature
     */
    default void updateEntityAttributes(PathfinderMob creature) {

    }

    /**
     * If Vampirism's default converted creature is used, this can be used to specify some properties of the converted creature
     */
    interface IDefaultHelper {

        Map<Attribute, com.mojang.datafixers.util.Pair<FloatProvider,Double>> getAttributeModifier();
    }
}
