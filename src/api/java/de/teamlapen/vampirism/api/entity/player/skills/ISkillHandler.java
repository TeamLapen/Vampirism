package de.teamlapen.vampirism.api.entity.player.skills;


import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import de.teamlapen.vampirism.api.entity.player.ISkillPlayer;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Handles the players skills
 */
public interface ISkillHandler<T extends ISkillPlayer<T>> {

    static <T extends ISkillPlayer<T>> Optional<ISkillHandler<T>> get(Player player) {
        return VampirismAPI.factionPlayerHandler(player).getSkillHandler();
    }

    /**
     * @return Returns false if the skill already is unlocked or the parent node is not unlocked or the skill is not found
     */
    Result canSkillBeEnabled(Holder<ISkill<?>> skill, Holder<ISkillTree> skillTree);

    /**
     * Disables the given skill
     */
    void disableSkill(Holder<ISkill<T>> skill, ISkillTree skillTree);

    /**
     * Enable the given skill. Check canSkillBeEnabled first
     */
    default void enableSkill(Holder<ISkill<T>> skill, Holder<ISkillTree> skillTree) {
        enableSkill(skill, skillTree, false);
    }

    void enableSkill(Holder<ISkill<T>> skill, Holder<ISkillTree> skillTree, boolean fromLoading);

    /**
     * @return The count of additional skills that can be currently unlocked
     */
    int getLeftSkillPoints(Holder<ISkillTree> tree);

    List<Holder<ISkill<?>>> getParentSkills(Holder<ISkill<?>> skill);

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
