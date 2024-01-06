package de.teamlapen.vampirism.entity.action;

import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityAction;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionManager;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.core.Registry;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class ActionManagerEntity implements IEntityActionManager {

    @Override
    public @NotNull List<IEntityAction> getAllEntityActions() {
        return getRegistry().stream().collect(Collectors.toList());
    }

    @Override
    public @NotNull List<IEntityAction> getAllEntityActionsByTierAndClassType(IFaction<?> faction, @NotNull EntityActionTier tier, EntityClassType classType) {
        List<IEntityAction> actions = getAllEntityActions();
        actions.removeIf(action -> action.getFaction() != faction || action.getTier().getId() > tier.getId() || !ArrayUtils.contains(action.getClassTypes(), classType));
        return actions;
    }

    @Override
    public Registry<IEntityAction> getRegistry() {
        return ModRegistries.ENTITY_ACTIONS;
    }
}
