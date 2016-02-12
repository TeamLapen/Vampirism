package de.teamlapen.vampirism.api.entity;

import net.minecraft.entity.EntityCreature;

/**
 * Interface for vampirism extended entity property which every {@link EntityCreature} has
 */
public interface IExtendedCreatureVampirism extends IBiteableEntity {
    /**
     * @return the representing entity
     */
    EntityCreature getEntity();

    /**
     * If the entity never had any blood, this returns -1
     *
     * @return current blood level
     */
    int getBlood();

    /**
     * @param blood Value is checked
     */
    void setBlood(int blood);

    /**
     * @return Max blood level
     */
    int getMaxBlood();
}
