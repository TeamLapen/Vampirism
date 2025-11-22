package de.teamlapen.factions.api.factions;

import de.teamlapen.factions.api.entities.player.IFactionPlayer;
import de.teamlapen.factions.api.factions.lord.ILordPlayerEntry;
import de.teamlapen.factions.api.factions.village.IFactionVillage;
import de.teamlapen.factions.api.items.IRefinementItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
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
    IPlayableFactionBuilder<T> village(IFactionVillage villageBuilder);

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

    IPlayableFactionBuilder<T> lord(ILordPlayerEntry lordPlayerBuilder);

    @Override
    <Z> IPlayableFactionBuilder<T> addTag(ResourceKey<? extends Registry<Z>> registryKey, TagKey<Z> tag);

    @Override
    IPlayableFaction<T> build();
}
