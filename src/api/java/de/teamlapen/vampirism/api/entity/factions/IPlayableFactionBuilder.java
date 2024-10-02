package de.teamlapen.vampirism.api.entity.factions;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;
import org.jetbrains.annotations.NotNull;

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

    @Override
    IPlayableFactionBuilder<T> village(@NotNull IFactionVillage villageBuilder);

    /**
     * Allows this faction to have accessories
     *
     * @param refinementItemBySlot function to get the refinement item for each slot
     * @return the builder
     */
    IPlayableFactionBuilder<T> refinementItem(@NotNull IRefinementItem.AccessorySlotType type, Supplier<IRefinementItem> item);

    @Override
    IPlayableFactionBuilder<T> chatColor(TextColor color);

    @Override
    IPlayableFactionBuilder<T> chatColor(ChatFormatting color);

    @Override
    IPlayableFactionBuilder<T> name(@NotNull String nameKey);

    @Override
    IPlayableFactionBuilder<T> namePlural(@NotNull String namePluralKey);

    IPlayableFactionBuilder<T> lord(ILordPlayerEntry lordPlayerBuilder);

    @Override
    IPlayableFaction<T> build();
}
