package de.teamlapen.vampirism.api.entity.player.skills;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;

import java.util.List;

/**
 * 1.12
 *
 * @author maxanier
 */
public interface ISkillManager {


    /**
     * A mutable copied list of all skills registered for this faction
     */
    <T extends IFactionPlayer<T>> List<ISkill<T>> getSkillsForFaction(IPlayableFaction<T> faction);
}
