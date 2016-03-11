package de.teamlapen.vampirism.api.entity.player.skills;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;

/**
 * Handles skill registration
 */
public interface ISkillRegistry {

    /**
     * @param faction
     * @param skill
     * @return The id which belongs to the given skill
     */
    String getID(IPlayableFaction faction, ISkill skill);

    /**
     * @param faction
     * @return The root skill node for the given faction
     */
    SkillNode getRootSkillNode(IPlayableFaction faction);

    /**
     * @param faction
     * @param id
     * @return The skill that is mapped to the given faction
     */
    ISkill getSkill(IPlayableFaction faction, String id);

    /**
     * FOR INTERNAL USAGE ONLY
     * Registers a node and the included skills
     *
     * @param node
     */
    void registerNode(SkillNode node);

    /**
     * Set the root skill (is unlocked automatically when level oen is reached) for a faction.
     * Can override a previously set one, but should not
     *
     * @param faction
     * @param skill
     * @return The create root skill node
     */
    SkillNode setRootSkill(IPlayableFaction faction, ISkill skill);
}
