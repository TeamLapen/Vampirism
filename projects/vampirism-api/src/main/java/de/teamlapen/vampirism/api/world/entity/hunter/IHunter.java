package de.teamlapen.vampirism.api.world.entity.hunter;

import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.factions.IFactionEntity;
import de.teamlapen.vampirism.api.VampirismFactions;
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
