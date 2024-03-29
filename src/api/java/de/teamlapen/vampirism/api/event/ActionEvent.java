package de.teamlapen.vampirism.api.event;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
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
     * Posted before an action fires. Use this to modify the cooldown or duration of action, or to prevent the action from activating.
     */
    public static class ActionActivatedEvent extends ActionEvent implements ICancellableEvent {

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
         * @return The original duration of the action, this will return -1 if the action does not implement ILastingAction.
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
         * @param cooldown The new cooldown of the action, in ticks
         */
        public void setCooldown(int cooldown) {
            this.cooldown = cooldown;
        }
    }

    /**
     * Posted when an action deactivates, either when deactivated manually or when out of time. As regular actions instantly deactivate, this only fires for actions that implement {@link de.teamlapen.vampirism.api.entity.player.actions.ILastingAction}.
     */
    public static class ActionUpdateEvent extends ActionEvent {
        private final int remainingDuration;
        private boolean deactivate;
        private boolean skipActionUpdate;

        public ActionUpdateEvent(@NotNull IFactionPlayer<?> factionPlayer, @NotNull ILastingAction<?> action, int remainingDuration) {
            super(factionPlayer, action);
            this.remainingDuration = remainingDuration;
        }

        public @NotNull ILastingAction<?> getAction() {
            return (ILastingAction<?>) super.getAction();
        }

        /**
         * @return The remaining duration of the action, in ticks.
         */
        public int getRemainingDuration() {
            return this.remainingDuration;
        }

        /**
         * Call this to deactivate the action. Or override the current result.
         *
         * @param overrideDeactivation If true, the action will be deactivated.
         */
        public void setDeactivation(boolean overrideDeactivation) {
            this.deactivate = overrideDeactivation;
        }

        /**
         * @return If true, the action will be deactivated.
         */
        public boolean shouldDeactivation() {
            return this.deactivate;
        }

        public void setSkipActionUpdate(boolean skipActionUpdate) {
            this.skipActionUpdate = skipActionUpdate;
        }

        public boolean shouldSkipActionUpdate() {
            return this.skipActionUpdate;
        }

    }

}
