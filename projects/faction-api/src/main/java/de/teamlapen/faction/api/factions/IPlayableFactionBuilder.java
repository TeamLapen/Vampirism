package de.teamlapen.faction.api.factions;

import de.teamlapen.faction.api.factions.lord.ILordPlayerBuilder;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.faction.api.world.items.IRefinementItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IPlayableFactionBuilder<T extends IFactionPlayer<T>> extends IFactionBuilder<T> {

    @Override
    IPlayableFactionBuilder<T> color(int color);

    /**
     * Sets the maximum level for a player of this faction
     *
     * @param highestLevel the highest possible level for players,  {@code highestLordLevel > 0}
     * @return the builder
     */
    IPlayableFactionBuilder<T> highestLevel(int highestLevel);

    /**
     * Allows this faction to have accessories
     *
     * @return the builder
     */
    IPlayableFactionBuilder<T> refinementItem(IRefinementItem.AccessorySlotType type, Supplier<IRefinementItem> item);

    @Override
    IPlayableFactionBuilder<T> chatColor(TextColor color);

    @Override
    IPlayableFactionBuilder<T> chatColor(ChatFormatting color);

    IPlayableFactionBuilder<T> lord(Consumer<ILordPlayerBuilder<T>> builder);

    @Override
    IPlayableFaction<T> build();
}
