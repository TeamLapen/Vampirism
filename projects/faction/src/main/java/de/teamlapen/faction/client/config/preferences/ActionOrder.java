package de.teamlapen.faction.client.config.preferences;

import com.mojang.serialization.Codec;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.common.util.ModCodecs;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;

import java.util.*;
import java.util.stream.Collectors;

public class ActionOrder extends PreferenceValue<Map<Holder<? extends IFaction<?>>, List<Holder<? extends IAction<?>>>>> {

    private static final Identifier ID = FIdentifier.mod("action_order");
    private static final Codec<Map<Holder<? extends IFaction<?>>, List<Holder<? extends IAction<?>>>>> CODEC = Codec.unboundedMap(
            ModCodecs.faction(),
            ModCodecs.action().listOf()
    );
    private final RegistryAccess registryAccess;

    public ActionOrder(RegistryAccess registryAccess) {
        super(ID, CODEC, registryAccess, () -> provideDefault(registryAccess));
        this.registryAccess = registryAccess;
    }

    private static Map<Holder<? extends IFaction<?>>, List<Holder<? extends IAction<?>>>> provideDefault(RegistryAccess registryAccess) {
        Registry<IFaction<?>> iFactions = registryAccess.lookupOrThrow(FactionRegistries.Keys.FACTION);
        Map<Holder<? extends IFaction<?>>, List<Holder<? extends IAction<?>>>> map = iFactions.listElements().collect(Collectors.toMap(x -> x, faction -> new ArrayList<Holder<? extends IAction<?>>>()));
        registryAccess.lookupOrThrow(FactionRegistries.Keys.ACTION)
                .listElements().forEach(action -> {
                    map.entrySet().stream().filter(x -> IFaction.is(x.getKey(), action.value().factions())).forEach(list -> list.getValue().add(action));
                });

        return map;
    }

    public List<Holder<? extends IAction<?>>> allowedActions(Holder<? extends IFaction<?>> faction) {
        Registry<IAction<?>> actions = registryAccess.lookupOrThrow(FactionRegistries.Keys.ACTION);
        return actions.listElements()
                .filter(x -> isAllowed(faction, x))
                .collect(Collectors.toUnmodifiableList());
    }

    public boolean isAllowed(Holder<? extends IFaction<?>> faction, Holder<? extends IAction<?>> action) {
        return IFaction.is(faction, action.value().factions());
    }

    public <T extends Holder<? extends IFaction<?>>> List<Holder<? extends IAction<?>>> getOrder(T faction) {
        return this.getValue().getOrDefault(faction, List.of());
    }

    public <T extends Holder<? extends IFaction<?>>> void update(T faction, List<Holder<? extends IAction<?>>> actionOrder) {
        List<Holder<? extends IAction<?>>> actions = new ArrayList<>();
        for (Holder<? extends IAction<?>> holder : actionOrder) {
            if(isAllowed(faction, holder)) {
                actions.add(holder);
            }
        }
        Map<Holder<? extends IFaction<?>>, List<Holder<? extends IAction<?>>>> value = new HashMap<>(getValue());
        value.put(faction, List.copyOf(actions));
        setValue(Map.copyOf(value));
        save();
    }
}
