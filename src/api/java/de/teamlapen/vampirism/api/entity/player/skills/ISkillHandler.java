package de.teamlapen.vampirism.api.entity.player.skills;


import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import net.minecraft.item.ItemStack;

/**
 * Handles the players skills
 */
public interface ISkillHandler<T extends ISkillPlayer<?>> {

    /**
     * @param skill
     * @return Returns false if the skill already is unlocked or the parent node is not unlocked or the skill is not found
     */
    Result canSkillBeEnabled(ISkill skill);


    /**
     * Disables the given skill
     *
     * @param skill
     */
    void disableSkill(ISkill skill);

    /**
     * Enable the given skill. Check canSkillBeEnabled first
     *
     * @param skill
     */
    void enableSkill(ISkill skill);

    /**
     * @return The count of additional skills that can be currently unlocked
     */
    int getLeftSkillPoints();

    ISkill[] getParentSkills(ISkill skill);

    boolean isSkillEnabled(ISkill skill);

    /**
     * Reset all skills but reactivate the root skill of the faction
     */
    void resetSkills();

    /**
     * Equip the refinement set from the given stack to the appropriate slot
     * If no set is present or it is from the wrong faction, the old set for the slot will be removed, but no new set will be added
     *
     * @param stack
     * @return Whether the item wass equipped
     */
    boolean equipRefinementItem(ItemStack stack);

    boolean isRefinementEquipped(IRefinement refinement);

    ItemStack[] createRefinementItems();

    enum Result {
        OK, ALREADY_ENABLED, PARENT_NOT_ENABLED, NOT_FOUND, NO_POINTS, OTHER_NODE_SKILL, LOCKED_BY_OTHER_NODE
    }
}
