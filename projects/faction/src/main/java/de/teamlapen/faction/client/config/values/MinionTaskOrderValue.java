package de.teamlapen.faction.client.config.values;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.world.entities.minion.IFactionMinionTask;
import de.teamlapen.faction.api.world.entities.minion.IMinionTask;
import de.teamlapen.faction.api.world.entities.minion.INoGlobalCommandTask;
import de.teamlapen.faction.client.gui.screens.SelectMinionTaskRadialScreen;
import de.teamlapen.faction.common.core.ModRegistries;
import de.teamlapen.faction.common.util.ModCodecs;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StrictJsonParser;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class MinionTaskOrderValue {
    private static final Logger LOGGER = LoggerFactory.getLogger(MinionTaskOrderValue.class);
    private static final Codec<Map<Holder<? extends IFaction<?>>, List<SelectMinionTaskRadialScreen.Entry>>> CODEC = Codec.lazyInitialized(() -> Codec.simpleMap(ModCodecs.faction(), EntryCodec.CODEC.listOf(), FactionRegistries.FACTION.get()).codec());

    public final ModConfigSpec.ConfigValue<String> order;
    private Map<Holder<? extends IFaction<?>>, List<SelectMinionTaskRadialScreen.Entry>> minionTasks = new HashMap<>();

    public MinionTaskOrderValue(ModConfigSpec.Builder builder, String path) {
        this.order = builder.define(path, "{}", this::test);
    }

    private boolean test(Object o) {
        if (!(o instanceof String string)) return false;
        return CODEC.parse(JsonOps.INSTANCE, StrictJsonParser.parse(string)).result().isPresent();
    }

    public List<SelectMinionTaskRadialScreen.Entry> get(Holder<? extends IFaction<?>> faction) {
        if(this.minionTasks.containsKey(faction)){
            return this.minionTasks.get(faction);
        }
        return setAndSave(faction, allowedValues(faction));
    }

    public List<SelectMinionTaskRadialScreen.Entry> allowedValues(Holder<? extends IFaction<?>> faction) {
        return Stream.concat(ModRegistries.MINION_TASKS.listElements()
                                .filter(x -> !(x.value() instanceof INoGlobalCommandTask<?, ?>))
                                .filter(x -> !(x.value() instanceof IFactionMinionTask<?, ?> factionTask) || IFaction.is(faction, factionTask.getFaction()))
                                .map(SelectMinionTaskRadialScreen.Entry::new),
                        SelectMinionTaskRadialScreen.CUSTOM_ENTRIES.values().stream())
                .toList();
    }

    /**
     * @param faction The minion faction to save the given list for
     * @param tasks A list of available minions tasks
     * return The given task list
     */
    public List<SelectMinionTaskRadialScreen.Entry> setAndSave(Holder<? extends IFaction<?>> faction, List<SelectMinionTaskRadialScreen.Entry> tasks) {
        this.minionTasks.put(faction, tasks);
        save();
        return tasks;
    }

    public void save() {
        CODEC.encodeStart(JsonOps.INSTANCE, this.minionTasks)
                .resultOrPartial(error -> LOGGER.error("Failed to encode minion tasks: {}", error))
                .ifPresent(json -> this.order.set(json.toString()));
    }

    public void refresh() {
        var result = CODEC.parse(JsonOps.INSTANCE, StrictJsonParser.parse(this.order.get()));
        this.minionTasks = result
                .resultOrPartial(error -> LOGGER.error("Failed to parse minion task order: {}\n", error)).map(HashMap::new)
                .orElseGet(HashMap::new);
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
