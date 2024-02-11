package de.teamlapen.vampirism.api.event;

import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Event related to any faction changes of players
 */
public class PlayerFactionEvent extends Event {

    @Nullable
    private final IPlayableFaction<?> currentFaction;
    @NotNull
    private final IFactionPlayerHandler player;

    private PlayerFactionEvent(@NotNull IFactionPlayerHandler player, @Nullable IPlayableFaction<?> currentFaction) {
        this.currentFaction = currentFaction;
        this.player = player;
    }

    /**
     * @return The faction the respective player is currently in.
     */
    @Nullable
    public IPlayableFaction<?> getCurrentFaction() {
        return currentFaction;
    }

    /**
     * You can use {@link de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler#asEntity()} to get the actual EntityPlayer
     *
     * @return The faction handler representing the current player
     */
    @NotNull
    public IFactionPlayerHandler getPlayer() {
        return player;
    }

    /**
     * Posted when a player is about to change their faction or level. Only server side
     * If canceled the level/faction change is canceled.
     * But the player is not notified, so you should probably consider doing so.
     */
    public static class FactionLevelChangePre extends PlayerFactionEvent implements ICancellableEvent {
        private final int currentLevel;
        private final int newLevel;
        @Nullable
        private final IPlayableFaction<?> newFaction;

        public FactionLevelChangePre(@NotNull IFactionPlayerHandler player, @Nullable IPlayableFaction<?> currentFaction, int currentLevel, @Nullable IPlayableFaction<?> newFaction, int newLevel) {
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
        public IPlayableFaction<?> getNewFaction() {
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
     * Posted when a player after a player changed their faction or faction level
     */
    public static class FactionLevelChanged extends PlayerFactionEvent {
        private final int oldLevel;
        private final int newLevel;
        @Nullable
        private final IPlayableFaction<?> oldFaction;

        public FactionLevelChanged(@NotNull IFactionPlayerHandler player, @Nullable IPlayableFaction<?> oldFaction, int oldLevel, @Nullable IPlayableFaction<?> newFaction, int newLevel) {
            super(player, newFaction);
            this.oldLevel = oldLevel;
            this.newLevel = newLevel;
            this.oldFaction = oldFaction;
        }

        /**
         * @return The old level
         */
        public int getOldLevel() {
            return oldLevel;
        }

        /**
         * @return The faction the player was before
         */
        @Nullable
        public IPlayableFaction<?> getOldFaction() {
            return oldFaction;
        }

        /**
         * @return The level the player now has
         */
        public int getNewLevel() {
            return newLevel;
        }
    }

    /**
     * Posted to check if a player can join a faction.
     * <p>
     * {@link  Behavior#DENY} disallows
     * <p>
     * {@link Behavior#ONLY_WHEN_NO_FACTION} default check (if the current faction is null)
     * <p>
     * {@link  Behavior#ALLOW} allows joining even if in another faction (not recommend)
     * <p>
     * The player is not notified if not {@link Behavior#ONLY_WHEN_NO_FACTION}, so you should consider doing so.
     */
    public static class CanJoinFaction extends PlayerFactionEvent {

        private final IPlayableFaction<?> toJoin;
        private Behavior behavior = Behavior.ONLY_WHEN_NO_FACTION;

        public CanJoinFaction(@NotNull IFactionPlayerHandler player, @Nullable IPlayableFaction<?> currentFaction, IPlayableFaction<?> toJoin) {
            super(player, currentFaction);
            this.toJoin = toJoin;
        }

        /**
         * @return The faction the player wants to join
         */
        public IPlayableFaction<?> getFactionToJoin() {
            return toJoin;
        }

        public Behavior getBehavior() {
            return behavior;
        }

        public void setBehavior(Behavior behavior) {
            this.behavior = behavior;
        }

        public enum Behavior {
            ONLY_WHEN_NO_FACTION,
            ALLOW,
            DENY
        }

    }
}
