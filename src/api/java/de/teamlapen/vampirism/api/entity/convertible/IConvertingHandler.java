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

        @Deprecated
        default double getConvertedDMG(EntityType<? extends PathfinderMob> entity) {
            return getConvertedDMG(entity, RandomSource.create());
        }

        double getConvertedDMG(EntityType<? extends PathfinderMob> entity, RandomSource random);

        @Deprecated
        default double getConvertedKnockbackResistance(EntityType<? extends PathfinderMob> entity) {
            return getConvertedKnockbackResistance(entity, RandomSource.create());
        }

        double getConvertedKnockbackResistance(EntityType<? extends PathfinderMob> entity, RandomSource random);

        @Deprecated
        default double getConvertedMaxHealth(EntityType<? extends PathfinderMob> entity) {
            return getConvertedMaxHealth(entity, RandomSource.create());
        }

        double getConvertedMaxHealth(EntityType<? extends PathfinderMob> entity, RandomSource random);

        @Deprecated
        default double getConvertedSpeed(EntityType<? extends PathfinderMob> entity) {
            return getConvertedSpeed(entity, RandomSource.create());
        }

        double getConvertedSpeed(EntityType<? extends PathfinderMob> entity, RandomSource random);

    }
}
