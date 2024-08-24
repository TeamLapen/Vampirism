package de.teamlapen.vampirism.api.event;

import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import de.teamlapen.vampirism.api.entity.player.ISkillPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
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
    private final T factionPlayer;
    @NotNull
    private final Holder<Z> skill;

    @ApiStatus.Internal
    public SkillEvents(@NotNull T skillPlayer, @NotNull Holder<Z> skill) {
        this.factionPlayer = skillPlayer;
        this.skill = skill;
    }

    /**
     * @return The skill Player for which this event is fired
     */
    public @NotNull ISkillPlayer<?> getFactionPlayer() {
        return this.factionPlayer;
    }

    /**
     * @return The skill the event is firing for.
     */
    public @NotNull Holder<Z> skill() {
        return this.skill;
    }

    /**
     * @return The player for which this event is fired
     */
    @NotNull
    public Player getPlayer() {
        return this.factionPlayer.asEntity();
    }

    /**
     * This event is posted before the skill check is conducted.
     * If {@link #result} is set using {@link #setResult(de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler.Result)} the check will be skipped. and {@link #getResult()} will be used as the result.
     */
    public static class SkillUnlockCheckEvent<T extends ISkillPlayer<T>> extends SkillEvents<T, ISkill<T>> {

        @Nullable
        private ISkillHandler.Result result;

        @ApiStatus.Internal
        @SuppressWarnings("unchecked")
        public SkillUnlockCheckEvent(@NotNull T skillPlayer, @NotNull Holder<? extends ISkill<?>> skill) {
            super(skillPlayer, (Holder<ISkill<T>>) skill);
        }

        /**
         * Set the result of {@link de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler#canSkillBeEnabled(net.minecraft.core.Holder)}
         */
        public void setResult(@Nullable ISkillHandler.Result result) {
            this.result = result;
        }

        /**
         * The current result if this event
         */
        @Nullable
        public ISkillHandler.Result getResult() {
            return this.result;
        }

    }

    /**
     * Fired when a skill is disabled for a player
     */
    public static class SkillDisableEvent<T extends ISkillPlayer<T>> extends SkillEvents<T, ISkill<T>> {

        @ApiStatus.Internal
        @SuppressWarnings("unchecked")
        public SkillDisableEvent(@NotNull T skillPlayer, @NotNull Holder<? extends ISkill<?>> skill) {
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
        public SkillEnableEvent(@NotNull T factionPlayer, @NotNull Holder<? extends ISkill<?>> skill, Holder<ISkillTree> skillTree, boolean fromLoading) {
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
