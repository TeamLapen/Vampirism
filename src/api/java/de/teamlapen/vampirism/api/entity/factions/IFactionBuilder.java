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

    /**
     * Sets the faction chat color
     *
     * @param color chat color
     * @return the builder
     */
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

    /**
     * Sets the singular name for a entity of this faction
     *
     * @param nameKey the translation key of the name
     * @return the builder
     */
    IFactionBuilder<T> name(String nameKey);

    /**
     * Sets the plural name for a entity of this faction
     * @param namePluralKey the translation key of the name
     * @return the builder
     */
    IFactionBuilder<T> namePlural(String namePluralKey);

    /**
     * finish the building and registers the faction with values from the builder
     *
     * @return the final faction
     */
    IFaction<T> register();
}
