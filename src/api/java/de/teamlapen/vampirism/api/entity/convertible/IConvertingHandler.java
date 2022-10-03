package de.teamlapen.vampirism.api.entity.convertible;

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

        double getConvertedDMG(EntityType<? extends PathfinderMob> entity);

        double getConvertedKnockbackResistance(EntityType<? extends PathfinderMob> entity);

        double getConvertedMaxHealth(EntityType<? extends PathfinderMob> entity);

        double getConvertedSpeed(EntityType<? extends PathfinderMob> entity);

    }
}
