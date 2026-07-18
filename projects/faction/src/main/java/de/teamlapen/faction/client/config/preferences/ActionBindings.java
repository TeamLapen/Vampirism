package de.teamlapen.faction.client.config.preferences;

import com.mojang.serialization.Codec;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.common.core.ModRegistries;
import de.teamlapen.faction.common.factions.actions.ActionKeys;
import de.teamlapen.faction.common.util.ModCodecs;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ActionBindings extends PreferenceValue<Map<Holder<? extends IFaction<?>>,Map<ActionKeys, Holder<? extends IAction<?>>>>> {

    private static final Identifier ID = FIdentifier.mod("action_bindings");
    private static final Codec<Map<Holder<? extends IFaction<?>>, Map<ActionKeys, Holder<? extends IAction<?>>>>> CODEC = Codec.unboundedMap(
            ModCodecs.faction(),
            Codec.unboundedMap(
                    ActionKeys.STRING_CODEC,
                    ModCodecs.action()
            )
    );
    private final RegistryAccess registryAccess;

    public ActionBindings(RegistryAccess registryAccess) {
        super(ID, CODEC, registryAccess, HashMap::new);
        this.registryAccess = registryAccess;
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

    @Nullable
    public <T extends Holder<? extends IFaction<?>>> Holder<? extends IAction<?>> getOrder(T faction, ActionKeys action) {
        Map<ActionKeys, Holder<? extends IAction<?>>> actionKeysHolderMap = this.getValue().get(faction);
        if (actionKeysHolderMap != null) {
            return actionKeysHolderMap.get(action);
        }
        return null;
    }

    public <T extends Holder<? extends IFaction<?>>> void update(T faction, ActionKeys key, @Nullable Holder<? extends IAction<?>> action) {
        HashMap<ActionKeys, Holder<? extends IAction<?>>> actionKeysHolderHashMap = new HashMap<>(getValue(faction));
        if (action == null) {
            actionKeysHolderHashMap.remove(key);
        } else {
            actionKeysHolderHashMap.put(key, action);
        }
        update(faction, Map.copyOf(actionKeysHolderHashMap));
    }

    private <T extends Holder<? extends IFaction<?>>> Map<ActionKeys, Holder<? extends IAction<?>>> getValue(T faction) {
        return getValue().getOrDefault(faction, Map.of());
    }

    public <T extends Holder<? extends IFaction<?>>> void update(T faction, Map<ActionKeys, Holder<? extends IAction<?>>> actionOrder) {
        Map<ActionKeys, Holder<? extends IAction<?>>> actions = new HashMap<>();
        for (Map.Entry<ActionKeys, Holder<? extends IAction<?>>> holder : actionOrder.entrySet()) {
            if(isAllowed(faction, holder.getValue())) {
                actions.put(holder.getKey(), holder.getValue());
            }
        }
        Map<Holder<? extends IFaction<?>>, Map<ActionKeys, Holder<? extends IAction<?>>>> value = new HashMap<>(getValue());
        value.put(faction, actions);
        setValue(Map.copyOf(value));
        save();
    }
}
