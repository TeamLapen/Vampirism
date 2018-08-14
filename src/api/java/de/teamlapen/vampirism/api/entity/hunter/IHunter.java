package de.teamlapen.vampirism.api.entity.hunter;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;

/**
 * Implemented by all hunter entities
 */
public interface IHunter extends IFactionEntity {

    @Override
    default IFaction getFaction() {
        return VReference.HUNTER_FACTION;
    }
}
