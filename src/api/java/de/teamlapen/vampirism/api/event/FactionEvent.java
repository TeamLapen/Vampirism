package de.teamlapen.vampirism.api.event;

import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Event related to any faction changes of players
 */
public class FactionEvent extends Event {

    @Nullable
    private final IPlayableFaction currentFaction;
    @Nonnull
    private final IFactionPlayerHandler player;

    private FactionEvent(@Nonnull IFactionPlayerHandler player, @Nullable IPlayableFaction currentFaction) {
        this.currentFaction = currentFaction;
        this.player = player;
    }

    /**
     * @return The faction the respective player is currently in.
     */
    @Nullable
    public IPlayableFaction getCurrentFaction() {
        return currentFaction;
    }

    /**
     * You can use {@link IFactionPlayerHandler#getPlayer()} to get the actual EntityPlayer
     *
     * @return The faction handler representing the current player
     */
    @Nonnull
    public IFactionPlayerHandler getPlayer() {
        return player;
    }

    /**
     * Posted when a player is about to change their faction or level.
     * If canceled the level/faction change is canceled.
     * But the player is not notified, so you should probably consider doing so.
     */
    @Cancelable
    public static class ChangeLevelOrFaction extends FactionEvent {
        private final int currentLevel;
        private final int newLevel;
        @Nullable
        private final IPlayableFaction newFaction;

        public ChangeLevelOrFaction(@Nonnull IFactionPlayerHandler player, @Nullable IPlayableFaction currentFaction, int currentLevel, @Nullable IPlayableFaction newFaction, int newLevel) {
            super(player, currentFaction);
            this.currentLevel = currentLevel;
            this.newLevel = newLevel;
            this.newFaction = newFaction;
        }

        /**
         * @return The current level
         */
        public int getCurrentLevel() {
            return currentLevel;
        }

        /**
         * @return The faction the player is going to be
         */
        @Nullable
        public IPlayableFaction getNewFaction() {
            return newFaction;
        }

        /**
         * @return The level the player is going to have
         */
        public int getNewLevel() {
            return newLevel;
        }
    }

    /**
     * Posted to check if a player can join a faction.
     * DENY disallows
     * DEFAULT default check (if the current faction is null)
     * ALLOW allows to join even if in another faction (not recommend)
     * <p>
     * The player is not notified if not DEFAULT, so you should consider doing so.
     */
    @HasResult
    public static class CanJoinFaction extends FactionEvent {

        private final IPlayableFaction toJoin;

        public CanJoinFaction(@Nonnull IFactionPlayerHandler player, @Nullable IPlayableFaction currentFaction, IPlayableFaction toJoin) {
            super(player, currentFaction);
            this.toJoin = toJoin;
        }

        /**
         * @return The faction the player wants to join
         */
        public IPlayableFaction getFactionToJoin() {
            return toJoin;
        }

    }
}
