package de.teamlapen.vampirism.entity.action;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.api.entity.actions.IEntityAction;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionManager;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.core.VampirismRegistries;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import java.util.List;

public class EntityActionManager implements IEntityActionManager {

    @Override
    public List<IEntityAction> getEntityActionForFaction(IFaction faction) {
        List<IEntityAction> list = Lists.newArrayList(VampirismRegistries.ENTITYACTIONS.getValues());
        list.removeIf(action -> !faction.equals(action.getFaction()));
        return list;
    }

    @Override
    public IForgeRegistry<IEntityAction> getRegistry() {
        return net.minecraftforge.registries.RegistryManager.ACTIVE.getRegistry(new ResourceLocation("vampirism:entityactions"));
    }

}
