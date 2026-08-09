package de.teamlapen.faction.client.config.preferences;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.api.util.FIdentifier;
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
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Per-faction saved action order/exclusion list for the radial action-select menu.
 */
public class ActionOrder extends PreferenceValue<Map<Holder<? extends IFaction<?>>, ActionOrder.ActionPreferenceValue>> {

    private static final Identifier ID = FIdentifier.mod("action_order");
    private static final Codec<Map<Holder<? extends IFaction<?>>, ActionPreferenceValue>> CODEC = Codec.unboundedMap(
            ModCodecs.faction(),
            ActionPreferenceValue.CODEC
    );
    private final RegistryAccess registryAccess;

    public ActionOrder(RegistryAccess registryAccess) {
        this.registryAccess = registryAccess;
        super(ID, CODEC, registryAccess, () -> provideDefault(registryAccess));
    }

    /**
     * Backfills missing/newly-registered factions and actions, drops factions no longer registered, and replaces the stored value with the reconciled result.
     *
     * @return whether the reconciled value differs from what was loaded, i.e. whether it needs to be saved.
     */
    @Override
    protected boolean checkValues(Map<Holder<? extends IFaction<?>>, ActionPreferenceValue> value, RegistryAccess registryAccess) {
        Registry<IFaction<?>> factions = registryAccess.lookupOrThrow(FactionRegistries.Keys.FACTION);
        Map<Holder<? extends IFaction<?>>, ActionPreferenceValue> reconciled = new HashMap<>();
        factions.listElements().forEach(faction -> {
            ActionPreferenceValue existing = value.getOrDefault(faction, ActionPreferenceValue.EMPTY);
            Set<Holder<? extends IAction<?>>> known = new HashSet<>(existing.order());
            known.addAll(existing.excluded());
            List<Holder<? extends IAction<?>>> order = new ArrayList<>(existing.order());
            allowedActions(faction).stream().filter(action -> !known.contains(action)).forEach(order::add);
            reconciled.put(faction, new ActionPreferenceValue(order, existing.excluded()));
        });
        Map<Holder<? extends IFaction<?>>, ActionPreferenceValue> immutable = Map.copyOf(reconciled);
        setValue(immutable);
        return !immutable.equals(value);
    }

    private static Map<Holder<? extends IFaction<?>>, ActionPreferenceValue> provideDefault(RegistryAccess registryAccess) {
        Registry<IFaction<?>> factions = registryAccess.lookupOrThrow(FactionRegistries.Keys.FACTION);
        Map<Holder<? extends IFaction<?>>, List<Holder<? extends IAction<?>>>> staged = factions.listElements()
                .collect(Collectors.toMap(faction -> faction, faction -> new ArrayList<>()));
        registryAccess.lookupOrThrow(FactionRegistries.Keys.ACTION).listElements().forEach(action ->
                staged.forEach((faction, order) -> {
                    if (IFaction.is(faction, action.value().factions())) {
                        order.add(action);
                    }
                })
        );
        Map<Holder<? extends IFaction<?>>, ActionPreferenceValue> result = new HashMap<>();
        staged.forEach((faction, order) -> result.put(faction, new ActionPreferenceValue(order, List.of())));
        return Map.copyOf(result);
    }

    /** Actions the given faction is currently allowed to use, in registry order. */
    public List<Holder<? extends IAction<?>>> allowedActions(Holder<? extends IFaction<?>> faction) {
        Registry<IAction<?>> actions = registryAccess.lookupOrThrow(FactionRegistries.Keys.ACTION);
        return actions.listElements()
                .filter(action -> isAllowed(faction, action))
                .collect(Collectors.toList());
    }

    /** Whether the given faction is allowed to use the given action. */
    public boolean isAllowed(Holder<? extends IFaction<?>> faction, Holder<? extends IAction<?>> action) {
        return IFaction.is(faction, action.value().factions());
    }

    /** The faction's saved action order; does not include excluded actions. */
    public List<Holder<? extends IAction<?>>> getOrder(Holder<? extends IFaction<?>> faction) {
        return getValue().getOrDefault(faction, ActionPreferenceValue.EMPTY).order();
    }

    /** Persists a new order/exclusion list for the faction, dropping any actions the faction is no longer allowed to use. */
    public void update(Holder<? extends IFaction<?>> faction, List<Holder<? extends IAction<?>>> order, List<Holder<? extends IAction<?>>> excluded) {
        List<Holder<? extends IAction<?>>> allowedOrder = order.stream().filter(action -> isAllowed(faction, action)).toList();
        List<Holder<? extends IAction<?>>> allowedExcluded = excluded.stream().filter(action -> isAllowed(faction, action)).toList();
        Map<Holder<? extends IFaction<?>>, ActionPreferenceValue> value = new HashMap<>(getValue());
        value.put(faction, new ActionPreferenceValue(allowedOrder, allowedExcluded));
        setValue(Map.copyOf(value));
        save();
    }

    /**
     * Immutable saved action order for a single faction; {@code order} and {@code excluded} are always duplicate-free and disjoint.
     */
    record ActionPreferenceValue(List<Holder<? extends IAction<?>>> order, List<Holder<? extends IAction<?>>> excluded) {

        private static final ActionPreferenceValue EMPTY = new ActionPreferenceValue(List.of(), List.of());

        private static final Codec<ActionPreferenceValue> CODEC = RecordCodecBuilder.<ActionPreferenceValue>create(inst -> inst.group(
                ModCodecs.action().listOf().fieldOf("order").forGetter(ActionPreferenceValue::order),
                ModCodecs.action().listOf().fieldOf("excluded").forGetter(ActionPreferenceValue::excluded)
        ).apply(inst, ActionPreferenceValue::new)).validate(ActionPreferenceValue::validate);

        ActionPreferenceValue {
            order = List.copyOf(order);
            excluded = List.copyOf(excluded);
        }

        private static DataResult<ActionPreferenceValue> validate(ActionPreferenceValue value) {
            Set<Holder<? extends IAction<?>>> seen = new HashSet<>();
            for (Holder<? extends IAction<?>> action : value.order()) {
                if (!seen.add(action)) {
                    return DataResult.error(() -> "Duplicate action '" + action + "' in action order");
                }
            }
            for (Holder<? extends IAction<?>> action : value.excluded()) {
                if (!seen.add(action)) {
                    return DataResult.error(() -> "Action '" + action + "' duplicated or present in both order and excluded lists");
                }
            }
            return DataResult.success(value);
        }
    }
}
