package de.teamlapen.factions.api.factions;

import de.teamlapen.factions.api.factions.village.IFactionVillageBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.function.Consumer;

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


    IFactionBuilder<T> village(Consumer<IFactionVillageBuilder> villageBuilder);

    /**
     * finish the building and registers the faction with values from the builder
     *
     * @return the final faction
     */
    IFaction<T> build();
}
