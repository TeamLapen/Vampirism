package de.teamlapen.faction.client.config.preferences;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.api.world.entities.minion.tasks.IMinionTask;
import de.teamlapen.faction.api.world.entities.minion.tasks.IMinionTaskCategory;
import de.teamlapen.faction.client.gui.screens.SelectMinionTaskRadialScreen;
import de.teamlapen.faction.client.gui.screens.SelectMinionTaskRadialScreen.CategoryEntry;
import de.teamlapen.faction.client.gui.screens.SelectMinionTaskRadialScreen.Entry;
import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.faction.common.core.ModRegistries;
import de.teamlapen.faction.common.util.ModCodecs;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Order of the entries of the minion task radial menu.
 * <p>
 * The order is kept on the top level: tasks that belong to a {@link IMinionTaskCategory} are represented by a single
 * {@link CategoryEntry}, their sub tasks are not ordered individually.
 */
public class MinionTaskOrder extends PreferenceValue<Map<Holder<? extends IFaction<?>>, List<Entry>>> {

    private static final Identifier ID = FIdentifier.mod("minion_task_order");
    private static final Codec<Map<Holder<? extends IFaction<?>>, List<Entry>>> CODEC = Codec.unboundedMap(
            ModCodecs.faction(),
            EntryCodec.CODEC.listOf()
    );
    private final RegistryAccess registryAccess;

    public MinionTaskOrder(RegistryAccess registryAccess) {
        super(ID, CODEC, registryAccess, () -> provideDefault(registryAccess));
        this.registryAccess = registryAccess;
    }

    private static Map<Holder<? extends IFaction<?>>, List<Entry>> provideDefault(RegistryAccess registryAccess) {
        Registry<IFaction<?>> factions = registryAccess.lookupOrThrow(FactionRegistries.Keys.FACTION);
        return factions.listElements().collect(Collectors.toMap(x -> x, faction -> topLevelEntries(faction, registryAccess)));
    }

    /**
     * @return All entries that can be placed in the radial menu of the given faction. A whole category is represented
     * by a single {@link CategoryEntry}
     */
    private static List<Entry> topLevelEntries(Holder<? extends IFaction<?>> faction, RegistryAccess registryAccess) {
        List<Entry> entries = new ArrayList<>();
        Set<Identifier> categories = new HashSet<>();
        SelectMinionTaskRadialScreen.CUSTOM_ENTRIES.values().forEach(entry -> addTopLevel(entries, categories, entry));
        registryAccess.lookupOrThrow(FactionRegistries.Keys.MINION_TASK).listElements()
                .filter(x -> x.components().has(FactionDataComponents.MINION_TASK_GLOBAL_COMMAND))
                .filter(x -> IFaction.is(faction, x.value().allowedFactions()))
                .forEach(task -> addTopLevel(entries, categories, new Entry(task)));
        return entries;
    }

    private static void addTopLevel(List<Entry> entries, Set<Identifier> categories, Entry entry) {
        Holder<IMinionTaskCategory> category = entry.getCategory();
        if (category == null) {
            entries.add(entry);
        } else if (categories.add(CategoryEntry.id(category))) {
            entries.add(entry instanceof CategoryEntry ? entry : new CategoryEntry(category));
        }
    }

    public List<Entry> allowedValues(Holder<? extends IFaction<?>> faction) {
        return topLevelEntries(faction, this.registryAccess);
    }

    public boolean isAllowed(Holder<? extends IFaction<?>> faction, Entry task) {
        if (task instanceof CategoryEntry) {
            return true;
        }
        return SelectMinionTaskRadialScreen.CUSTOM_ENTRIES.containsKey(task.getId()) ||
                (task.getTask() != null && task.getTask().components().has(FactionDataComponents.MINION_TASK_GLOBAL_COMMAND) && IFaction.is(faction, task.getTask().value().allowedFactions()));
    }

    public <T extends Holder<? extends IFaction<?>>> List<Entry> getOrder(T faction) {
        return normalize(this.getValue().getOrDefault(faction, List.of()));
    }

    /**
     * Replaces entries of tasks that have been grouped into a category with the category itself. Keeps orders that
     * were saved before the categories existed working.
     */
    private static List<Entry> normalize(List<Entry> entries) {
        List<Entry> result = new ArrayList<>();
        Set<Identifier> categories = new HashSet<>();
        entries.forEach(entry -> addTopLevel(result, categories, entry));
        return result;
    }

    public <T extends Holder<? extends IFaction<?>>> void update(T faction, List<Entry> order) {
        List<Entry> tasks = new ArrayList<>();
        for (Entry holder : normalize(order)) {
            if (isAllowed(faction, holder)) {
                tasks.add(holder);
            }
        }
        Map<Holder<? extends IFaction<?>>, List<Entry>> value = new HashMap<>(getValue());
        value.put(faction, List.copyOf(tasks));
        setValue(Map.copyOf(value));
        save();
    }

    private static class EntryCodec implements Codec<Entry> {

        public static final Codec<Entry> CODEC = new EntryCodec();
        private static final Codec<Holder<IMinionTask<?>>> ID_CODEC = ModRegistries.MINION_TASKS.holderByNameCodec();

        @Override
        public <T> DataResult<Pair<Entry, T>> decode(DynamicOps<T> ops, T input) {
            DataResult<Pair<Holder<IMinionTask<?>>, T>> decode = ID_CODEC.decode(ops, input);
            if (!decode.isError()) {
                return decode.map(x -> Pair.of(new Entry(x.getFirst()), x.getSecond()));
            }
            var identifier = Identifier.CODEC.decode(ops, input);
            if (identifier.error().isPresent()) {
                DataResult.Error<Pair<Identifier, T>> error = identifier.error().get();
                return DataResult.error(() -> "Failed to decode minion task entry: " + error);
            }
            //noinspection OptionalGetWithoutIsPresent
            Identifier id = identifier.result().get().getFirst();
            return resolve(id).map(entry -> identifier.map(x -> Pair.of(entry, x.getSecond())))
                    .orElseGet(() -> DataResult.error(() -> "Failed to decode minion task entry: Unknown entry " + id));
        }

        /**
         * Resolves everything that is not a plain minion task: categories and the entries that are not backed by a task
         */
        private static Optional<Entry> resolve(Identifier id) {
            if (id.getPath().startsWith(SelectMinionTaskRadialScreen.CATEGORY_PREFIX)) {
                Identifier categoryId = id.withPath(path -> path.substring(SelectMinionTaskRadialScreen.CATEGORY_PREFIX.length()));
                return ModRegistries.MINION_TASK_CATEGORIES.get(categoryId).map(CategoryEntry::new);
            }
            return Optional.ofNullable(SelectMinionTaskRadialScreen.CUSTOM_ENTRIES.get(id));
        }

        @Override
        public <T> DataResult<T> encode(Entry input, DynamicOps<T> ops, T prefix) {
            if (input instanceof CategoryEntry || input.getTask() == null) {
                return Identifier.CODEC.encode(input.getId(), ops, prefix);
            }
            return ID_CODEC.encode(input.getTask(), ops, prefix);
        }
    }
}
