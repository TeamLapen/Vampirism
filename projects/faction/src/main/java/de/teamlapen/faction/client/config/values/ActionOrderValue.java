package de.teamlapen.faction.client.config.values;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.common.core.ModRegistries;
import de.teamlapen.faction.common.util.ModCodecs;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.util.StrictJsonParser;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ActionOrderValue {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionOrderValue.class);
    private static final Codec<Map<Holder<? extends IFaction<?>>, List<Holder<IAction<?>>>>> CODEC = Codec.lazyInitialized(() -> Codec.simpleMap(ModCodecs.faction(), RegistryFixedCodec.create(FactionRegistries.Keys.ACTION).listOf(), FactionRegistries.FACTION.get()).codec());

    public final ModConfigSpec.ConfigValue<String> order;
    private Map<Holder<? extends IFaction<?>>, List<Holder<IAction<?>>>> actions = new HashMap<>();

    public ActionOrderValue(ModConfigSpec.Builder builder, String path) {
        this.order = builder.define(path, "{}", this::test);
    }

    private boolean test(Object o) {
        if (!(o instanceof String string)) return false;
        return CODEC.parse(JsonOps.INSTANCE, StrictJsonParser.parse(string)).result().isPresent();
    }

    public List<Holder<IAction<?>>> get(Holder<? extends IFaction<?>> faction) {
        return this.actions.computeIfAbsent(faction, item -> set(item, allowedValues(item)));
    }

    public List<Holder<IAction<?>>> allowedValues(Holder<? extends IFaction<?>> faction) {
        return ModRegistries.ACTIONS.listElements()
                .filter(x -> IFaction.is(faction, x.value().factions()))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Holder<IAction<?>>> set(Holder<? extends IFaction<?>> faction, List<Holder<IAction<?>>> actions) {
        this.actions.put(faction, actions);
        save();

        return actions;
    }

    private void save() {
        CODEC.encodeStart(JsonOps.INSTANCE, this.actions)
                .resultOrPartial(error -> LOGGER.error("Failed to encode actions: {}", error))
                .ifPresent(json -> this.order.set(json.toString()));
    }

    public void refresh() {
        var result = CODEC.parse(JsonOps.INSTANCE, StrictJsonParser.parse(this.order.get()));
        this.actions = Collections.unmodifiableMap(result
                .resultOrPartial(error -> LOGGER.error("Failed to parse actions: {}", error))
                .orElseGet(HashMap::new));
    }
}
