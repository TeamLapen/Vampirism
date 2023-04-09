package de.teamlapen.vampirism.api.entity.convertible;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;

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

    /**
     * If Vampirism's default converted creature is used, this can be used to specify some properties of the converted creature
     */
    interface IDefaultHelper {

        /**
         * use {@link #getConvertedDMG(net.minecraft.world.entity.EntityType, net.minecraft.util.RandomSource)}
         */
        @Deprecated(forRemoval = true)
        default double getConvertedDMG(EntityType<? extends PathfinderMob> entity) {
            return 0;
        }

        default double getConvertedDMG(EntityType<? extends PathfinderMob> entity, RandomSource random) {
            return getConvertedDMG(entity);
        }

        @Deprecated(forRemoval = true)
        default double getConvertedKnockbackResistance(EntityType<? extends PathfinderMob> entity) {
            return 0;
        }

        default double getConvertedKnockbackResistance(EntityType<? extends PathfinderMob> entity, RandomSource random) {
            return getConvertedKnockbackResistance(entity);
        }

        @Deprecated(forRemoval = true)
        default double getConvertedMaxHealth(EntityType<? extends PathfinderMob> entity) {
            return 0;
        }

        default double getConvertedMaxHealth(EntityType<? extends PathfinderMob> entity, RandomSource random) {
            return getConvertedMaxHealth(entity);
        }

        @Deprecated(forRemoval = true)
        default double getConvertedSpeed(EntityType<? extends PathfinderMob> entity) {
            return 0;
        }

        default double getConvertedSpeed(EntityType<? extends PathfinderMob> entity, RandomSource random) {
            return getConvertedSpeed(entity);
        }

    }
}
