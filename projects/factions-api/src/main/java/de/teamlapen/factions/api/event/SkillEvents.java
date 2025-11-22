package de.teamlapen.factions.api.event;

import de.teamlapen.factions.api.skills.ISkill;
import de.teamlapen.factions.api.skills.ISkillHandler;
import de.teamlapen.factions.api.skills.ISkillPlayer;
import de.teamlapen.factions.api.skills.ISkillTree;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Event related to any skill changes of players
 */
public abstract class SkillEvents<T extends ISkillPlayer<T>, Z extends ISkill<T>> extends Event {

    @NotNull
    private final T skillPlayer;
    private final Holder<Z> skill;

    @ApiStatus.Internal
    public SkillEvents(@NotNull T skillPlayer, Holder<Z> skill) {
        this.skillPlayer = skillPlayer;
        this.skill = skill;
    }

    /**
     * @return The skill Player for which this event is fired
     */
    public ISkillPlayer<?> getSkillPlayer() {
        return this.skillPlayer;
    }

    /**
     * @return The skill the event is firing for.
     */
    public Holder<Z> skill() {
        return this.skill;
    }

    /**
     * @return The player for which this event is fired
     */
    public Player getPlayer() {
        return this.skillPlayer.asEntity();
    }

    /**
     * This event is posted before the skill check is conducted.
     * If {@link #result} is set using {@link #setResult(ISkillHandler.Result)} the check will be skipped. and {@link #getResult()} will be used as the result.
     */
    public static class SkillUnlockCheckEvent<T extends ISkillPlayer<T>> extends SkillEvents<T, ISkill<T>> {

        @Nullable
        private ISkillHandler.Result result;

        @ApiStatus.Internal
        @SuppressWarnings("unchecked")
        public SkillUnlockCheckEvent(@NotNull T skillPlayer, Holder<? extends ISkill<?>> skill) {
            super(skillPlayer, (Holder<ISkill<T>>) skill);
        }

        /**
         * The current result if this event
         */
        @Nullable
        public ISkillHandler.Result getResult() {
            return this.result;
        }

        /**
         * Set the result of {@link ISkillHandler#canSkillBeEnabled(Holder, Holder)}
         */
        public void setResult(@Nullable ISkillHandler.Result result) {
            this.result = result;
        }

    }

    /**
     * Fired when a skill is disabled for a player
     */
    public static class SkillDisableEvent<T extends ISkillPlayer<T>> extends SkillEvents<T, ISkill<T>> {

        @ApiStatus.Internal
        @SuppressWarnings("unchecked")
        public SkillDisableEvent(@NotNull T skillPlayer, Holder<? extends ISkill<?>> skill) {
            super(skillPlayer, (Holder<ISkill<T>>) skill);
        }

    }

    /**
     * Fired when a skill is enabled for a player
     */
    public static class SkillEnableEvent<T extends ISkillPlayer<T>> extends SkillEvents<T, ISkill<T>> {

        private final Holder<ISkillTree> skillTree;
        private final boolean fromLoading;

        @ApiStatus.Internal
        @SuppressWarnings("unchecked")
        public SkillEnableEvent(@NotNull T factionPlayer, Holder<? extends ISkill<?>> skill, Holder<ISkillTree> skillTree, boolean fromLoading) {
            super(factionPlayer, (Holder<ISkill<T>>) skill);
            this.skillTree = skillTree;
            this.fromLoading = fromLoading;
        }

        /**
         * @return If the skill is enabled because the player is loaded from the save file
         */
        public boolean isFromLoading() {
            return fromLoading;
        }


        public Holder<ISkillTree> getSkillTree() {
            return skillTree;
        }
    }

}
