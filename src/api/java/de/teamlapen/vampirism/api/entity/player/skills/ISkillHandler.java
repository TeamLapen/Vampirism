package de.teamlapen.vampirism.api.entity.player.skills;


import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import net.minecraft.world.item.ItemStack;

/**
 * Handles the players skills
 */
public interface ISkillHandler<T> {

    /**
     * @return Returns false if the skill already is unlocked or the parent node is not unlocked or the skill is not found
     */
    Result canSkillBeEnabled(ISkill skill);

    ItemStack[] createRefinementItems();

    void damageRefinements();

    /**
     * Disables the given skill
     *
     */
    void disableSkill(ISkill skill);

    /**
     * Enable the given skill. Check canSkillBeEnabled first
     *
     */
    void enableSkill(ISkill skill);

    /**
     * Equip the refinement set from the given stack to the appropriate slot
     * If no set is present, or it is from the wrong faction, the old set for the slot will be removed, but no new set will be added
     *
     * @return Whether the item was equipped
     */
    boolean equipRefinementItem(ItemStack stack);

    /**
     * @return The count of additional skills that can be currently unlocked
     */
    int getLeftSkillPoints();

    ISkill[] getParentSkills(ISkill skill);

    boolean isRefinementEquipped(IRefinement refinement);

    boolean isSkillEnabled(ISkill skill);

    /**
     * remove all equipped refinements
     */
    void resetRefinements();

    /**
     * Reset all skills but reactivate the root skill of the faction
     */
    void resetSkills();

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
