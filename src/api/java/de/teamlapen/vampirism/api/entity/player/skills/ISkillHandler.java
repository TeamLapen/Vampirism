package de.teamlapen.vampirism.api.entity.player.skills;


import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.api.util.RegUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.List;

/**
 * Handles the players skills
 */
public interface ISkillHandler<T extends IFactionPlayer<T>> {

    /**
     * @return Returns false if the skill already is unlocked or the parent node is not unlocked or the skill is not found
     */
    Result canSkillBeEnabled(Holder<ISkill<?>> skill);

    @Deprecated
    default Result canSkillBeEnabled(ISkill<?> skill) {
        return canSkillBeEnabled(RegUtil.holder(skill));
    }

    ItemStack[] createRefinementItems();

    NonNullList<ItemStack> getRefinementItems();

    void damageRefinements();

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
     * Equip the refinement set from the given stack to the appropriate slot
     * If no set is present, or it is from the wrong faction, the old set for the slot will be removed, but no new set will be added
     *
     * @return Whether the item was equipped
     */
    boolean equipRefinementItem(ItemStack stack);

    void removeRefinementItem(IRefinementItem.AccessorySlotType slot);

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
    default boolean isRefinementEquipped(IRefinement refinement) {
        return isRefinementEquipped(RegUtil.holder(refinement));
    }

    boolean isRefinementEquipped(Holder<IRefinement> refinement);

    @Deprecated
    default boolean isSkillEnabled(ISkill<?> skill) {
        return isSkillEnabled(RegUtil.holder(skill));
    }

    boolean isSkillEnabled(Holder<ISkill<?>> skill);

    /**
     * remove all equipped refinements
     */
    void resetRefinements();

    /**
     * Reset all skills but reactivate the root skill of the faction
     */
    void resetSkills();

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
