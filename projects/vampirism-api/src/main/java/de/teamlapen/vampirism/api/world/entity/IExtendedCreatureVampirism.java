package de.teamlapen.vampirism.api.world.entity;

import de.teamlapen.vampirism.api.world.entity.convertible.IConvertedCreature;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for Vampirism's capability every {@link PathfinderMob} has attached
 */
public interface IExtendedCreatureVampirism extends IBiteableEntity {
    /**
     * @return If this entity can be converted to a vampire version
     */
    boolean canBecomeVampire();

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
     * @return the representing entity
     */
    PathfinderMob getEntity();

    /**
     * @return Max blood level
     */
    int getMaxBlood();

    /**
     * @return Whether the bitter should get poisoned on bite
     */
    boolean hasPoisonousBlood();

    /**
     * @return The duration of poisonous blood left
     */
    int getPoisonousBloodDuration();

    /**
     * @deprecated Use {@link IExtendedCreatureVampirism#setPoisonousBlood(int)}
     */
    @Deprecated()
    default void setPoisonousBlood(boolean poisonous) {
    }

    /**
     * Sets the duration of poisonous blood of the entity
     */
    void setPoisonousBlood(int poisonous);

    /**
     * Convert this creature into a vampire version if possible
     * Will replace/remove existing entity
     *
     * @return The converted creature
     */
    @Nullable
    IConvertedCreature<?> makeVampire();

    /**
     * Called every tick
     */
    void tick();
}
