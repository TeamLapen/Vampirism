package de.teamlapen.vampirism.api.event;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class ActionEvent extends Event {

    @NotNull
    private final IFactionPlayer<?> factionPlayer;
    @NotNull
    private final IAction<?> action;
    public ActionEvent(@NotNull IFactionPlayer<?> factionPlayer, @NotNull IAction<?> action) {
        this.factionPlayer = factionPlayer;
        this.action = action;
    }
    /**
     * @return The FactionPlayer who activated the action.
     */
    public @NotNull IFactionPlayer<?> getFactionPlayer() {
        return this.factionPlayer;
    }
    /**
     * @return The action the event is firing for.
     */
    public @NotNull IAction<?> getAction() {
        return this.action;
    }

    /**
     * Posted after an action fires. Use this to modify the cooldown or duration of an event
     */
    public static class ActionActivatedEvent extends ActionEvent {

        private int cooldown;
        private int duration;

        public ActionActivatedEvent(@NotNull IFactionPlayer<?> factionPlayer, @NotNull IAction<?> action, int cooldown, int duration) {
            super(factionPlayer, action);
            this.cooldown = cooldown;
            this.duration = duration;
        }
        /**
         * @return The original cooldown of the action, in ticks
         */
        public int getCooldown() {
            return cooldown;
        }
        /**
         * @param cooldown the new cooldown of the action, in ticks.
         */
        public void setCooldown(int cooldown) {
            this.cooldown = cooldown;
        }
        /**
         * @return The original duration of the action, this will return 0 if the action does not implement ILastingAction.
         */
        public int getDuration() {
            return duration;
        }
        /**
         * @param duration the new duration of the action, in ticks.
         */
        public void setDuration(int duration) {
            this.duration = duration;
        }
    }
    /**
     * Posted when an action deactivates, either when deactivated manually or when out of time. As regular actions instantly deactivate, this only fires for actions that implement ILastingAction.
     */
    public static class ActionDeactivatedEvent extends ActionEvent {
        private final int remainingDuration;
        private int cooldown;
        public ActionDeactivatedEvent(@NotNull IFactionPlayer<?> factionPlayer, @NotNull IAction<?> action, int remainingDuration, int cooldown) {
            super(factionPlayer, action);
            this.remainingDuration = remainingDuration;
            this.cooldown = cooldown;
        }
        /**
         * @return The remaining duration of the action, in ticks.
         */
        public int getRemainingDuration() {
            return remainingDuration;
        }
        /**
         * @return The original cooldown of the action, in ticks
         */
        public int getCooldown() {
            return cooldown;
        }

        /**
         *
         * @param cooldown The new cooldown of the action, in ticks
         */
        public void setCooldown(int cooldown) {
            this.cooldown = cooldown;
        }
    }
    /**
     * Posted when an action deactivates, either when deactivated manually or when out of time. As regular actions instantly deactivate, this only fires for actions that implement ILastingAction.
     */
    public static class ActionUpdateEvent extends ActionEvent {
        private final int remainingDuration;
        private boolean shouldDeactivate;
        public ActionUpdateEvent(@NotNull IFactionPlayer<?> factionPlayer, @NotNull IAction<?> action, int remainingDuration, boolean shouldDeactivate) {
            super(factionPlayer, action);
            this.remainingDuration = remainingDuration;
            this.shouldDeactivate = shouldDeactivate;
        }
        /**
         * @return The remaining duration of the action, in ticks.
         */
        public int getRemainingDuration() {
            return this.remainingDuration;
        }
        /**
         * @return Whether the lasting action should be deactivated
         */
        public boolean shouldDeactivate() {
            return this.shouldDeactivate;
        }
        /**
         * @param deactivate If set to true, the action will deactivate. If set to false, the action will not deactivate at all on that update.
         */
        public void setDeactivation(boolean deactivate) {
            this.shouldDeactivate = deactivate;
        }

    }
    /**
     * Posted before a player activates an action, can be used to prevent an action being fired.
     */
    @Cancelable
    public static class PreActionActivatedEvent extends ActionEvent {
        public PreActionActivatedEvent(@NotNull IFactionPlayer<?> factionPlayer, @NotNull IAction<?> action) {
            super(factionPlayer, action);
        }
    }

}
