package de.teamlapen.vampirism.api.entity.player.skills;


import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.util.RegUtil;
import net.minecraft.core.Holder;

import java.util.Collection;
import java.util.List;

/**
 * Handles the players skills
 */
public interface ISkillHandler<T extends IFactionPlayer<T>> extends IRefinementHandler {

    /**
     * @return Returns false if the skill already is unlocked or the parent node is not unlocked or the skill is not found
     */
    Result canSkillBeEnabled(Holder<ISkill<?>> skill);

    @Deprecated
    default Result canSkillBeEnabled(ISkill<?> skill) {
        return canSkillBeEnabled(RegUtil.holder(skill));
    }

    /**
     * Disables the given skill
     */
    void disableSkill(Holder<ISkill<T>> skill);

    /**
     * Enable the given skill. Check canSkillBeEnabled first
     */
    default void enableSkill(Holder<ISkill<T>> skill) {
        enableSkill(skill, false);
    }

    void enableSkill(Holder<ISkill<T>> skill, boolean fromLoading);

    /**
     * @return The count of additional skills that can be currently unlocked
     */
    int getLeftSkillPoints();

    @Deprecated
    default ISkill<?>[] getParentSkills(ISkill<?> skill) {
        return getParentSkills(RegUtil.holder(skill)).stream().map(Holder::value).toArray(ISkill[]::new);
    }

    List<Holder<ISkill<?>>> getParentSkills(Holder<ISkill<?>> skill);

    @Deprecated
    default boolean isSkillEnabled(ISkill<?> skill) {
        return isSkillEnabled(RegUtil.holder(skill));
    }

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
