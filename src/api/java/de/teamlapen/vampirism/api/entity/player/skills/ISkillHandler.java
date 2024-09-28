package de.teamlapen.vampirism.api.entity.player.skills;


import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import de.teamlapen.vampirism.api.entity.player.ISkillPlayer;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.Optional;

/**
 * Handles the players skills
 */
public interface ISkillHandler<T extends ISkillPlayer<T>> {

    static <T extends ISkillPlayer<T>> Optional<ISkillHandler<T>> get(Player player) {
        return VampirismAPI.factionPlayerHandler(player).getSkillHandler();
    }

    static <T extends ISkillPlayer<T>> boolean isSkillEnabled(Player player, Holder<ISkill<?>> skill) {
        return get(player).map(handler -> handler.isSkillEnabled(skill)).orElse(false);
    }

    /**
     * @return Returns false if the skill already is unlocked or the parent node is not unlocked or the skill is not found
     */
    Result canSkillBeEnabled(Holder<ISkill<?>> skill, Holder<ISkillTree> skillTree);

    /**
     * Only use this if you have no access to the skill tree.
     * <p>
     * This will select any skill tree that contains the skill
     *
     * @deprecated Use {@link #canSkillBeEnabled(Holder, Holder)} instead
     */
    @Deprecated
    Result canSkillBeEnabled(Holder<ISkill<?>> skill);

    /**
     * Disables the given skill
     */
    void disableSkill(Holder<ISkill<T>> skill, Holder<ISkillTree> skillTree);

    /**
     * Only use this if you have no access to the skill tree.
     * <p>
     * This will select any skill tree that contains the skill
     *
     * @deprecated Use {@link #disableSkill(Holder, Holder)} instead
     */
    @Deprecated
    void disableSkill(Holder<ISkill<T>> skill);

    /**
     * Enable the given skill. Check canSkillBeEnabled first
     */
    default void enableSkill(Holder<ISkill<T>> skill, Holder<ISkillTree> skillTree) {
        enableSkill(skill, skillTree, false);
    }

    /**
     * Only use this if you have no access to the skill tree.
     * <p>
     * This will select any skill tree that contains the skill
     *
     * @deprecated Use {@link #enableSkill(Holder, Holder)} instead
     */
    @Deprecated
    void enableSkill(Holder<ISkill<T>> skill);

    void enableSkill(Holder<ISkill<T>> skill, Holder<ISkillTree> skillTree, boolean fromLoading);

    /**
     * @return The count of additional skills that can be currently unlocked
     */
    int getLeftSkillPoints(Holder<ISkillTree> tree);

    boolean areSkillsEnabled(Collection<Holder<ISkill<?>>> skill);

    boolean isSkillEnabled(Holder<ISkill<?>> skill);

    void reset();

    void updateUnlockedSkillTrees(Collection<Holder<ISkillTree>> skillTrees);

    Collection<Holder<ISkillTree>> unlockedSkillTrees();

    enum Result {
        /**
         * can be enabled
         */
        OK,
        /**
         * Skill is already enabled
         */
        ALREADY_ENABLED,
        /**
         * Skill can not be enabled, because the parent skill is not unlocked
         */
        PARENT_NOT_ENABLED,
        /**
         * the skill could not be found in the skill tree
         */
        NOT_FOUND,
        /**
         * Skill points are missing to unlock the skill
         */
        NO_POINTS,
        /**
         * Skill is locked, because the sibling is unlocked
         */
        OTHER_NODE_SKILL,
        /**
         * Skill is locked, because the referenced locking skill is unlocked
         */
        LOCKED_BY_OTHER_NODE,
        /**
         * Skill is locked, because the player is not in a state of unlocking skills
         */
        LOCKED_BY_PLAYER_STATE
    }
}
