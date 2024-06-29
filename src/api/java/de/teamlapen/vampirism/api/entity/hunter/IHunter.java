package de.teamlapen.vampirism.api.entity.hunter;

import de.teamlapen.vampirism.api.VampirismFactions;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.NotNull;

/**
 * Implemented by all hunter entities
 */
public interface IHunter extends IFactionEntity {

    @NotNull
    @Override
    default Holder<? extends IFaction<?>> getFaction() {
        return VampirismFactions.HUNTER;
    }
}
