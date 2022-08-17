package de.teamlapen.vampirism.api.entity.hunter;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Implemented by all hunter entities
 */
public interface IHunter extends IFactionEntity {

    @NotNull
    @Override
    default IFaction<?> getFaction() {
        return VReference.HUNTER_FACTION;
    }
}
