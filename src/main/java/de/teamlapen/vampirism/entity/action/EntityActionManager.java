package de.teamlapen.vampirism.entity.action;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.api.entity.actions.IEntityAction;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionManager;
import de.teamlapen.vampirism.core.VampirismRegistries;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import java.util.List;

public class EntityActionManager implements IEntityActionManager {

    @Override
    public List<IEntityAction> getAllEntityActions() {
        return Lists.newArrayList(VampirismRegistries.ENTITYACTIONS.getValuesCollection());
    }

    @Override
    public IForgeRegistry<IEntityAction> getRegistry() {
        return net.minecraftforge.registries.RegistryManager.ACTIVE.getRegistry(new ResourceLocation("vampirism:entityactions"));
    }

}
