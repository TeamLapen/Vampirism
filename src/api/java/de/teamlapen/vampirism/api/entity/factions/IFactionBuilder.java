package de.teamlapen.vampirism.api.entity.factions;

import net.minecraft.ChatFormatting;

import javax.annotation.Nullable;

public interface IFactionBuilder<T extends IFactionEntity> {

    /**
     * Sets the faction color
     *
     * @param color Color e.g. for level rendering
     * @return the builder
     */
    IFactionBuilder<T> color(int color);

    IFactionBuilder<T> chatColor(ChatFormatting color);

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

    IFactionBuilder<T> name(String name);

    IFactionBuilder<T> namePlural(String plural);

    /**
     * finish the building and registers the faction with values from the builder
     *
     * @return the final faction
     */
    IFaction<T> register();
}
