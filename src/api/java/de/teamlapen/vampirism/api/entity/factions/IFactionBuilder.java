package de.teamlapen.vampirism.api.entity.factions;

import javax.annotation.Nullable;

public interface IFactionBuilder<T extends IFactionEntity> {

    /**
     * Sets the faction color
     *
     * @param color Color e.g. for level rendering
     * @return the builder
     */
    IFactionBuilder<T> color(int color);

    /**
     * Sets this faction as hostile to neutral entities
     *
     * @return the builder
     */
    IFactionBuilder<T> hostileTowardsNeutral();

    /**
     * Adds faction village compatibility
     *
     * @param villageFactionData village capture related utility class (if null will gets filled with dummy)
     * @return the builder
     */
    IFactionBuilder<T> village(@Nullable IVillageFactionData villageFactionData);

    /**
     * finish the building and registers the faction with values from the builder
     *
     * @return the final faction
     */
    IFaction<T> register();
}
