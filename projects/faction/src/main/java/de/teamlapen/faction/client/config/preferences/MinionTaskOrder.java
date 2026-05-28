package de.teamlapen.faction.client.config.preferences;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.api.world.entities.minion.IFactionMinionTask;
import de.teamlapen.faction.api.world.entities.minion.IMinionTask;
import de.teamlapen.faction.api.world.entities.minion.INoGlobalCommandTask;
import de.teamlapen.faction.client.gui.screens.SelectMinionTaskRadialScreen;
import de.teamlapen.faction.common.core.ModRegistries;
import de.teamlapen.faction.common.util.ModCodecs;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MinionTaskOrder extends PreferenceValue<Map<Holder<? extends IFaction<?>>, List<SelectMinionTaskRadialScreen.Entry>>> {

    private static final Identifier ID = FIdentifier.mod("minion_task_order");
    private static final Codec<Map<Holder<? extends IFaction<?>>, List<SelectMinionTaskRadialScreen.Entry>>> CODEC = Codec.unboundedMap(
            ModCodecs.faction(),
            EntryCodec.CODEC.listOf()
    );
    private final RegistryAccess registryAccess;

    public MinionTaskOrder(RegistryAccess registryAccess) {
        super(ID, CODEC, registryAccess, () -> provideDefault(registryAccess));
        this.registryAccess = registryAccess;
    }

    private static Map<Holder<? extends IFaction<?>>, List<SelectMinionTaskRadialScreen.Entry>> provideDefault(RegistryAccess registryAccess) {
        Registry<IFaction<?>> iFactions = registryAccess.lookupOrThrow(FactionRegistries.Keys.FACTION);
        Map<Holder<? extends IFaction<?>>, List<SelectMinionTaskRadialScreen.Entry>> map = iFactions.listElements().collect(Collectors.toMap(x -> x, faction -> new ArrayList<>()));
        map.forEach((faction, list) -> list.addAll(SelectMinionTaskRadialScreen.CUSTOM_ENTRIES.values()));
        registryAccess.lookupOrThrow(FactionRegistries.Keys.MINION_TASK)
                .listElements().forEach(task -> {
                    map.entrySet().stream().filter(x -> !(task.value() instanceof INoGlobalCommandTask<?, ?>) && (!(task.value() instanceof IFactionMinionTask<?, ?> s) || IFaction.is(x.getKey(), s.getFaction()))).forEach(list -> list.getValue().add(new SelectMinionTaskRadialScreen.Entry(task)));
                });

        return map;
    }

    public List<SelectMinionTaskRadialScreen.Entry> allowedValues(Holder<? extends IFaction<?>> faction) {
        Registry<IMinionTask<?, ?>> registry = registryAccess.lookupOrThrow(FactionRegistries.Keys.MINION_TASK);
        return Stream.concat(registry.listElements()
                                .filter(x -> !(x.value() instanceof INoGlobalCommandTask<?, ?>))
                                .filter(x -> !(x.value() instanceof IFactionMinionTask<?, ?> factionTask) || IFaction.is(faction, factionTask.getFaction()))
                                .map(SelectMinionTaskRadialScreen.Entry::new),
                        SelectMinionTaskRadialScreen.CUSTOM_ENTRIES.values().stream())
                .toList();
    }

    public boolean isAllowed(Holder<? extends IFaction<?>> faction, SelectMinionTaskRadialScreen.Entry task) {
        return SelectMinionTaskRadialScreen.CUSTOM_ENTRIES.containsKey(task.getId()) ||
                (task.getTask() != null && ((!(task.getTask().value() instanceof INoGlobalCommandTask<?,?>) || (!(task.getTask().value() instanceof IFactionMinionTask<?,?> fac) || IFaction.is(faction, fac.getFaction())))));
    }

    public <T extends Holder<? extends IFaction<?>>> List<SelectMinionTaskRadialScreen.Entry> getOrder(T faction) {
        return this.getValue().getOrDefault(faction, List.of());
    }

    public <T extends Holder<? extends IFaction<?>>> void update(T faction,  List<SelectMinionTaskRadialScreen.Entry> order) {
        List<SelectMinionTaskRadialScreen.Entry> tasks = new ArrayList<>();
        for (SelectMinionTaskRadialScreen.Entry holder : order) {
            if(isAllowed(faction, holder)) {
                tasks.add(holder);
            }
        }
        Map<Holder<? extends IFaction<?>>,  List<SelectMinionTaskRadialScreen.Entry>> value = new HashMap<>(getValue());
        value.put(faction, List.copyOf(tasks));
        setValue(Map.copyOf(value));
        save();
    }

    private static class EntryCodec implements Codec<SelectMinionTaskRadialScreen.Entry> {

        public static final Codec<SelectMinionTaskRadialScreen.Entry> CODEC = new EntryCodec();
        private static final Codec<Holder<IMinionTask<?, ?>>> ID_CODEC = ModRegistries.MINION_TASKS.holderByNameCodec();

        @Override
        public <T> DataResult<Pair<SelectMinionTaskRadialScreen.Entry, T>> decode(DynamicOps<T> ops, T input) {
            DataResult<Pair<Holder<IMinionTask<?, ?>>, T>> decode = ID_CODEC.decode(ops, input);
            if (decode.isError()) {
                var ide = Identifier.CODEC.decode(ops, input);
                if (ide.error().isPresent()) {
                    DataResult.Error<Pair<Identifier, T>> pairError = ide.error().get();
                    return DataResult.error(() -> "Failed to decode minion task entry: " + pairError);
                } else {
                    var identifierTPair = ide.result().map(Pair::getFirst).orElse(null);
                    var entry = SelectMinionTaskRadialScreen.CUSTOM_ENTRIES.get(identifierTPair);
                    if (entry == null) {
                        return DataResult.error(() -> "Failed to decode minion task entry: No custom entry found for identifier " + identifierTPair);
                    } else {
                        return ide.map(x -> Pair.of(entry, x.getSecond()));
                    }
                }
            } else {
                return decode.map(x -> Pair.of(new SelectMinionTaskRadialScreen.Entry(x.getFirst()), x.getSecond()));
            }
        }

        @Override
        public <T> DataResult<T> encode(SelectMinionTaskRadialScreen.Entry input, DynamicOps<T> ops, T prefix) {
            if (input.getTask() != null) {
                return ID_CODEC.encode(input.getTask(), ops, prefix);
            } else {
                return Identifier.CODEC.encode(input.getId(), ops, prefix);
            }
        }
    }
}
