package de.teamlapen.vampirism.api.entity.factions;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

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
     * <br>
     * if not set the faction color will be used
     *
     * @param color chat color
     * @return the builder
     */
    IFactionBuilder<T> chatColor(TextColor color);

    /**
     * Sets the faction chat color
     * <br>
     * if not set the faction color will be used
     *
     * @param color chat color
     * @return the builder
     * @implNote calls {@link #chatColor(TextColor)} with {@link TextColor#fromLegacyFormat(ChatFormatting)}}
     */
    IFactionBuilder<T> chatColor(ChatFormatting color);


    /**
     * Adds faction village compatibility
     *
     * @param villageBuilder village builder
     * @return the builder
     */
    IFactionBuilder<T> village(@NotNull IFactionVillage villageBuilder);

    /**
     * Sets the singular name for a entity of this faction
     *
     * @param nameKey the translation key of the name
     * @return the builder
     */
    IFactionBuilder<T> name(@NotNull String nameKey);

    /**
     * Sets the plural name for a entity of this faction
     *
     * @param namePluralKey the translation key of the name
     * @return the builder
     */
    IFactionBuilder<T> namePlural(@NotNull String namePluralKey);

    /**
     * Marks the tag as a faction tag
     *
     * @param registryKey the registry key for the tag registry
     * @param tag the faction tag
     * @return the builder
     */
    <Z> IFactionBuilder<T> addTag(ResourceKey<? extends Registry<Z>> registryKey, TagKey<Z> tag);

    /**
     * finish the building and registers the faction with values from the builder
     *
     * @return the final faction
     */
    IFaction<T> build();
}
