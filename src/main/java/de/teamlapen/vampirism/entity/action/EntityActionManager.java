package de.teamlapen.vampirism.entity.action;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.api.entity.actions.IEntityAction;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionManager;
import de.teamlapen.vampirism.core.VampirismRegistries;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityActionManager implements IEntityActionManager {
    public static int ID = 0;
    public static final Map<Integer, IEntityAction> actionsByID = new HashMap<>();
    public static final Map<IEntityAction, Integer> idByActions = new HashMap<>();

    @Override
    public List<IEntityAction> getAllEntityActions() {
        return Lists.newArrayList(VampirismRegistries.ENTITYACTIONS.getValuesCollection());
    }

    @Override
    public IForgeRegistry<IEntityAction> getRegistry() {
        return net.minecraftforge.registries.RegistryManager.ACTIVE.getRegistry(new ResourceLocation("vampirism:entityactions"));
    }

}
