package de.teamlapen.vampirism.api.entity.minions;

/**
 * Minion lord which can manage {@link ISaveableMinion}s that are saved with it.
 */
public interface IMinionLordWithSaveable<T extends ISaveableMinion> extends IMinionLord<T> {


    /**
     * @return The saveable minion handler of this lord
     */
    ISaveableMinionHandler<T> getSaveableMinionHandler();
}
