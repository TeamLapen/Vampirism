package de.teamlapen.vampirism.api.entity.convertible;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;

import javax.annotation.Nullable;

/**
 * Handles conversion of entities
 */
public interface IConvertingHandler<T extends CreatureEntity> {

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

        double getConvertedDMG(EntityType<? extends CreatureEntity> entity);

        double getConvertedKnockbackResistance(EntityType<? extends CreatureEntity> entity);

        double getConvertedMaxHealth(EntityType<? extends CreatureEntity> entity);

        double getConvertedSpeed(EntityType<? extends CreatureEntity> entity);

    }
}
