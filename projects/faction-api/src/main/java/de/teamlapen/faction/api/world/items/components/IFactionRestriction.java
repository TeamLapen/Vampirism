package de.teamlapen.faction.api.world.items.components;

import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IFactionPlayerHandler;
import de.teamlapen.faction.api.factions.skills.ISkill;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;

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

    /**
     * A custom message that appears when the object is restricted to be used.
     */
    Optional<Component> customMessage();

    Result canUse(IFactionPlayerHandler player);

    record Result(Optional<Component> message, boolean success) {
    }
}
