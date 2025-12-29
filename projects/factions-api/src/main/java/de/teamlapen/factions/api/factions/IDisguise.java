package de.teamlapen.factions.api.factions;

import net.minecraft.core.Holder;
import org.jetbrains.annotations.Nullable;

/**
 * a player of a faction can disguise themselves as another faction
 */
public interface IDisguise {

    /**
     * @return the actual faction of the player
     */
    Holder<? extends IPlayableFaction<?>> actualFaction();

    /**
     * returns the faction the player is currently disguised as, as seen by the viewerFaction
     *
     * @param viewerFaction the faction of the entity viewing the faction player
     * @return the viewed faction
     */
    Holder<? extends IFaction<?>> getViewedFaction(@Nullable Holder<? extends IFaction<?>> viewerFaction);

    default Holder<? extends IFaction<?>> getViewedFaction(@Nullable Holder<? extends IFaction<?>> viewerFaction, boolean ignoreDisguise) {
        return ignoreDisguise ? actualFaction() : getViewedFaction(viewerFaction);
    }

    /**
     * disguises the player as the given faction
     *
     * @param faction the faction to disguise as
     * @apiNote the faction player does not need to implement an actual disguise
     */
    void disguiseAs(Holder<? extends IFaction<?>> faction);

    /**
     * removes the disguise of the player
     */
    void unDisguise();

    /**
     * checks if the player is disguised
     *
     * @return true if the player is disguised
     */
    boolean isDisguised();

    record None(Holder<? extends IPlayableFaction<?>> faction) implements IDisguise {

        @Override
        public Holder<? extends IPlayableFaction<?>> actualFaction() {
            return this.faction;
        }

        @Override
        public Holder<? extends IFaction<?>> getViewedFaction(@Nullable Holder<? extends IFaction<?>> viewerFaction) {
            return this.faction;
        }

        @Override
        public void disguiseAs(@Nullable Holder<? extends IFaction<?>> faction) {

        }

        @Override
        public void unDisguise() {

        }

        @Override
        public boolean isDisguised() {
            return false;
        }
    }
}
