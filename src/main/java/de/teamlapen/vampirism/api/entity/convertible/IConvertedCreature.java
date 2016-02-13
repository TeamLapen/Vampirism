package de.teamlapen.vampirism.api.entity.convertible;

import de.teamlapen.vampirism.api.entity.IVampire;
import net.minecraft.entity.EntityCreature;

/**
 *  Interface for entities that were bitten an then converted to a vampire.
 *  When converted the old creature is removed and a new {@link IConvertedCreature} is spawned
 */
public interface IConvertedCreature<T extends EntityCreature> extends IVampire{
    /**
     * Set the old creature (the one before conversion)
     * @param creature
     */
    void setEntityCreature(T creature);
}
