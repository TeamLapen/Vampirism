package de.teamlapen.vampirism.api.entity.player.skills;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 1.12
 *
 * @author maxanier
 */
public interface ISkillManager {


    /**
     * Creates a new child node for the given parent
     * @param skills One or more xor skills
     * @return The created skill node
     */
    SkillNode createSkillNode(@Nonnull SkillNode parent, ISkill... skills);

    /**
     * DO NOT CALL BEFORE INIT IS FINISHED
     * @param faction
     * @return The root skill node for the given faction
     */
    SkillNode getRootSkillNode(IPlayableFaction faction);

    /**
     * A mutable copied list of all skills registered for this faction
     *
     * @param faction
     * @return
     */
    List<ISkill> getSkillsForFaction(IPlayableFaction faction);
}
