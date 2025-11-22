package de.teamlapen.factions.common.minions;

import com.google.common.collect.ImmutableMap;
import de.teamlapen.factions.api.entities.minion.IMinionEntry;
import de.teamlapen.factions.api.factions.IPlayableFaction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.callback.AddCallback;
import net.neoforged.neoforge.registries.callback.BakeCallback;
import net.neoforged.neoforge.registries.callback.ClearCallback;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class MinionEntryCallbacks implements AddCallback<IMinionEntry<?, ?>>, ClearCallback<IMinionEntry<?, ?>>, BakeCallback<IMinionEntry<?, ?>> {

    private final Map<Holder<? extends IPlayableFaction<?>>, List<Pair<ResourceKey<IMinionEntry<?, ?>>, IMinionEntry<?, ?>>>> tmpEntries = new HashMap<>();
    private Map<Holder<? extends IPlayableFaction<?>>, List<Pair<ResourceKey<IMinionEntry<?, ?>>, IMinionEntry<?, ?>>>> entries = ImmutableMap.of();

    @Override
    public void onAdd(Registry<IMinionEntry<?, ?>> registry, int id, ResourceKey<IMinionEntry<?, ?>> key, IMinionEntry<?, ?> value) {
        this.tmpEntries.computeIfAbsent(value.faction(), k -> new ArrayList<>()).add(Pair.of(key, value));
    }

    @Override
    public void onBake(Registry<IMinionEntry<?, ?>> registry) {
        ImmutableMap.Builder<Holder<? extends IPlayableFaction<?>>, List<Pair<ResourceKey<IMinionEntry<?, ?>>, IMinionEntry<?, ?>>>> builder = ImmutableMap.builder();
        for (Map.Entry<Holder<? extends IPlayableFaction<?>>, List<Pair<ResourceKey<IMinionEntry<?, ?>>, IMinionEntry<?, ?>>>> holderListEntry : this.tmpEntries.entrySet()) {
            builder.put(holderListEntry.getKey(), List.copyOf(holderListEntry.getValue()));
        }
        this.entries = builder.build();
    }

    @Override
    public void onClear(Registry<IMinionEntry<?, ?>> registry, boolean full) {
        if (full) {
            this.tmpEntries.clear();
            this.entries = ImmutableMap.of();
        }
    }

    public Map<Holder<? extends IPlayableFaction<?>>, List<Pair<ResourceKey<IMinionEntry<?, ?>>, IMinionEntry<?, ?>>>> getEntries() {
        return Collections.unmodifiableMap(entries);
    }
}
