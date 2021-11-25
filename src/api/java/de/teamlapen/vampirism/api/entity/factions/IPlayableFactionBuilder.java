package de.teamlapen.vampirism.api.entity.factions;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface IPlayableFactionBuilder<T extends IFactionPlayer<?>> extends IFactionBuilder<T> {

    @Override
    IPlayableFactionBuilder<T> color(Color color);

    @Override
    IPlayableFactionBuilder<T> hostileTowardsNeutral();

    /**
     * Sets the maximum level for a player of this faction
     *
     * @param highestLevel the highest possible level for players,  {@code highestLordLevel > 0}
     * @return the builder
     */
    IPlayableFactionBuilder<T> highestLevel(int highestLevel);

    /**
     * Sets the maximum lord level for player of this faction<br>
     * If set to 0 the player can not become lord
     *
     * @param highestLordLevel the highest possible lord level for players,  {@code highestLordLevel >= 0}
     * @return the builder
     */
    IPlayableFactionBuilder<T> lordLevel(int highestLordLevel);

    /**
     * Sets custom lord titles
     * <br>
     * Only relevant when lord level > 0
     *
     * @param lordTitleFunction a function that return the title for a lord player based on level and gender
     * @return the builder
     */
    IPlayableFactionBuilder<T> lordTitle(@Nonnull BiFunction<Integer, Boolean, ITextComponent> lordTitleFunction);

    @Override
    IPlayableFactionBuilder<T> village(@Nullable IVillageFactionData villageFactionData);

    /**
     * Allows this faction to have accessories
     *
     * @param accessoryBySlotFunction function to get the refinement item for each slot
     * @return the builder
     */
    IPlayableFactionBuilder<T> accessoryItems(Function<IRefinementItem.AccessorySlotType, IRefinementItem> accessoryBySlotFunction);

    @Override
    IPlayableFactionBuilder<T> chatColor(TextFormatting color);

    @Override
    IPlayableFactionBuilder<T> name(String name);

    @Override
    IPlayableFactionBuilder<T> namePlural(String plural);

    @Override
    IPlayableFaction<T> register();
}
