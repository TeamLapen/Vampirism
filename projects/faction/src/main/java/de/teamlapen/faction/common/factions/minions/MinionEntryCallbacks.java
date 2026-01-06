package de.teamlapen.faction.common.factions.minions;

import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.world.entities.minion.IMinionEntry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.neoforged.neoforge.registries.callback.BakeCallback;

import java.util.HashSet;
import java.util.Set;

public class MinionEntryCallbacks implements BakeCallback<IMinionEntry<?, ?>> {

    @Override
    public void onBake(Registry<IMinionEntry<?, ?>> registry) {
        Set<Holder<? extends IPlayableFaction<?>>> existingFactions = new HashSet<>();
        registry.forEach(entry -> {
            if (!existingFactions.add(entry.faction())) {
                throw new IllegalStateException("A faction can only have one minion type. Faction: " + entry.faction() + ", Minion: " + entry);
            }
        });
    }
}
