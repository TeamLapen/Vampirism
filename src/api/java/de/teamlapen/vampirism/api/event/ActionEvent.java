package de.teamlapen.vampirism.api.event;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.ISkillPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class ActionEvent<T extends IFactionPlayer<T> & ISkillPlayer<T>, Z extends IAction<T>> extends Event {

    @NotNull
    private final T factionPlayer;
    @NotNull
    private final Holder<Z> action;

    @ApiStatus.Internal
    public ActionEvent(@NotNull T factionPlayer, @NotNull Holder<Z> action) {
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
    public @NotNull Holder<Z> action() {
        return this.action;
    }

    /**
     * Posted before an action fires. Use this to modify the cooldown or duration of action, or to prevent the action from activating.
     */
    public static class ActionActivatedEvent<T extends IFactionPlayer<T> & ISkillPlayer<T>> extends ActionEvent<T, IAction<T>> implements ICancellableEvent {

        private int cooldown;
        private int duration;
        @NotNull
        private Component cancelMessage = Component.translatable("text.vampirism.action.cancelled");

        @ApiStatus.Internal
        public ActionActivatedEvent(@NotNull T factionPlayer, @NotNull Holder<IAction<T>> action, int cooldown, int duration) {
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

        /**
         * @return The message that is shown to the player if the event is canceled
         */
        @NotNull
        public Component getCancelMessage() {
            return cancelMessage;
        }

        /**
         * sets the message shown to the player if the event is canceled
         * */
        public void setCancelMessage(@NotNull Component message) {
            this.cancelMessage = message;
        }
    }

    /**
     * Posted when an action deactivates, either when deactivated manually or when out of time. As regular actions instantly deactivate, this only fires for actions that implement ILastingAction.
     */
    public static class ActionDeactivatedEvent<T extends ISkillPlayer<T>> extends ActionEvent<T, ILastingAction<T>> {
        private final int remainingDuration;
        private int cooldown;
        private boolean ignoreCooldown;
        private boolean fullCooldown;

        @ApiStatus.Internal
        public ActionDeactivatedEvent(@NotNull T factionPlayer, @NotNull Holder<ILastingAction<T>> action, int remainingDuration, int cooldown, boolean ignoreCooldown, boolean fullCooldown) {
            super(factionPlayer, action);
            this.remainingDuration = remainingDuration;
            this.cooldown = cooldown;
            this.ignoreCooldown = ignoreCooldown;
            this.fullCooldown = fullCooldown;
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

        /**
         * @return If true, the action will skip the cooldown
         */
        public boolean ignoreCooldown() {
            return this.ignoreCooldown;
        }

        /**
         * @param ignoreCooldown If true, the action will skip the cooldown
         */
        public void setIgnoreCooldown(boolean ignoreCooldown) {
            this.ignoreCooldown = ignoreCooldown;
        }

        /**
         * @return If true, the action will have a full cooldown instead of considering the remaining duration to reduce the cooldown
         */
        public boolean fullCooldown() {
            return this.fullCooldown;
        }

        /**
         * @param fullCooldown If true, the action will have a full cooldown instead of considering the remaining duration to reduce the cooldown
         */
        public void setFullCooldown(boolean fullCooldown) {
            this.fullCooldown = fullCooldown;
        }
    }

    /**
     * Posted when an action deactivates, either when deactivated manually or when out of time. As regular actions instantly deactivate, this only fires for actions that implement {@link de.teamlapen.vampirism.api.entity.player.actions.ILastingAction}.
     */
    public static class ActionUpdateEvent<T extends ISkillPlayer<T>> extends ActionEvent<T, ILastingAction<T>> {
        private final int remainingDuration;
        private boolean deactivate;
        private boolean skipActionUpdate;

        @ApiStatus.Internal
        public ActionUpdateEvent(@NotNull T factionPlayer, @NotNull Holder<ILastingAction<T>> action, int remainingDuration) {
            super(factionPlayer, action);
            this.remainingDuration = remainingDuration;
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
