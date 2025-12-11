package de.teamlapen.factions.api.event;

import de.teamlapen.factions.api.factions.IFactionPlayerHandler;
import de.teamlapen.factions.api.factions.IPlayableFaction;
import de.teamlapen.factions.api.factions.LevelingChange;
import net.minecraft.core.Holder;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Event related to any faction changes of players
 */
public class PlayerFactionEvent extends Event {

    private final Holder<IPlayableFaction<?>> currentFaction;
    private final IFactionPlayerHandler player;

    private PlayerFactionEvent(IFactionPlayerHandler player, Holder<IPlayableFaction<?>> currentFaction) {
        this.currentFaction = currentFaction;
        this.player = player;
    }

    /**
     * @return The faction the respective player is currently in.
     */
    public Holder<IPlayableFaction<?>> getCurrentFaction() {
        return currentFaction;
    }

    /**
     * You can use {@link IFactionPlayerHandler#asEntity()} to get the actual EntityPlayer
     *
     * @return The faction handler representing the current player
     */
    public IFactionPlayerHandler getPlayerHandler() {
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
        private final Holder<IPlayableFaction<?>> newFaction;

        public FactionLevelChangePre(IFactionPlayerHandler player, Holder<IPlayableFaction<?>> currentFaction, int currentLevel, Holder<IPlayableFaction<?>> newFaction, int newLevel) {
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
        public Holder<IPlayableFaction<?>> getNewFaction() {
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
        private final Holder<IPlayableFaction<?>> oldFaction;

        public FactionLevelChanged(IFactionPlayerHandler player, Holder<IPlayableFaction<?>> oldFaction, int oldLevel, Holder<IPlayableFaction<?>> newFaction, int newLevel) {
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
        public Holder<IPlayableFaction<?>> getOldFaction() {
            return oldFaction;
        }

        /**
         * @return The level the player now has
         */
        public int getNewLevel() {
            return newLevel;
        }
    }

    public static class LevelChanged extends PlayerFactionEvent {

        private final LevelingChange change;

        @SuppressWarnings("unchecked")
        public LevelChanged(IFactionPlayerHandler player, LevelingChange change) {
            super(player, (Holder<IPlayableFaction<?>>) change.getNewFaction());
            this.change = change;
        }

        public LevelingChange getChange() {
            return this.change;
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

        private final Holder<IPlayableFaction<?>> toJoin;
        private Behavior behavior = Behavior.ONLY_WHEN_NO_FACTION;

        public CanJoinFaction(IFactionPlayerHandler player, Holder<IPlayableFaction<?>> currentFaction, Holder<IPlayableFaction<?>> toJoin) {
            super(player, currentFaction);
            this.toJoin = toJoin;
        }

        /**
         * @return The faction the player wants to join
         */
        public Holder<IPlayableFaction<?>> getFactionToJoin() {
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
