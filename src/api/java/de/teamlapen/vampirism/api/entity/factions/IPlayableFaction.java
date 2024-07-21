package de.teamlapen.vampirism.api.entity.factions;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Collection;

/**
 * Represents one playable faction (e.g. Vampire Player)
 * One instance should be used for players and entities at the same time.
 */
public interface IPlayableFaction<T extends IFactionPlayer<T>> extends IFaction<T> {

    /**
     * @return The highest reachable lord level or 0 if no lord
     */
    @Range(from = 0, to = Integer.MAX_VALUE)
    int getHighestLordLevel();

    /**
     * @return Highest reachable level for players
     */
    @Range(from = 0, to = Integer.MAX_VALUE)
    int getHighestReachableLevel();


    /**
     * Gets the lord title provider for this faction
     */
    @Nullable
    ILordTitleProvider lordTiles();

    /**
     * @return The "<? extends IFactionPlayer>" of this faction for the given player
     */
    T getPlayerCapability(Player player);

    /**
     * @return If this faction is allowed to have accessories
     */
    boolean hasRefinements();

    /**
     * Gets the corresponding item for the slot
     *
     * @throws NullPointerException if there are no accessories available
     */
    <Z extends Item & IRefinementItem> Z getRandomRefinementItem(RandomSource random, IRefinementItem.AccessorySlotType type);

    Collection<IRefinementItem> getRefinementItems();

    Collection<IRefinementItem> getRefinementItems(IRefinementItem.AccessorySlotType type);

    enum TitleGender implements StringRepresentable {
        UNKNOWN("unknown"),
        MALE("male"),
        FEMALE("female");

        private final String name;

        TitleGender(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }
}
