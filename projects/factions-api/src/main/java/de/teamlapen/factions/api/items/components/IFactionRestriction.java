package de.teamlapen.factions.api.items.components;

import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.skills.ISkill;
import net.minecraft.core.HolderSet;

import java.util.Optional;

public interface IFactionRestriction {

    /**
     * Factions that are allowed to use this object
     */
    HolderSet<IFaction<?>> factions();

    /**
     * Skills that are required to use this object.
     */
    Optional<HolderSet<ISkill<?>>> skills();

    /**
     * Minimum level required to use this object.
     */
    Optional<Integer> minLevel();
}
