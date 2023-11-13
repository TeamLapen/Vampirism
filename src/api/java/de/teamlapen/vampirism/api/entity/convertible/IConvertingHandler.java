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

        /**
         * use {@link #getAttributeModifier()} instead
         */
        @Deprecated(forRemoval = true)
        default double getConvertedDMG(EntityType<? extends PathfinderMob> entity) {
            return 0;
        }

        /**
         * use {@link #getAttributeModifier()} instead
         */
        @Deprecated(forRemoval = true)
        default double getConvertedKnockbackResistance(EntityType<? extends PathfinderMob> entity) {
            return 0;
        }

        /**
         * use {@link #getAttributeModifier()} instead
         */
        @Deprecated(forRemoval = true)
        default double getConvertedMaxHealth(EntityType<? extends PathfinderMob> entity) {
            return 0;
        }

        /**
         * use {@link #getAttributeModifier()} instead
         */
        @Deprecated(forRemoval = true)
        default double getConvertedSpeed(EntityType<? extends PathfinderMob> entity) {
            return 0;
        }

        default Map<Attribute, com.mojang.datafixers.util.Pair<FloatProvider,Double>> getAttributeModifier() {
            return Collections.emptyMap();
        }
    }
}
