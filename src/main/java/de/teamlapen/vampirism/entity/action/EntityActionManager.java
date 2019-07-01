package de.teamlapen.vampirism.entity.action;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityAction;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionManager;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.core.VampirismRegistries;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import java.util.List;

public class EntityActionManager implements IEntityActionManager {

    @Override
    public List<IEntityAction> getAllEntityActions() {
        return Lists.newArrayList(VampirismRegistries.ENTITYACTIONS.getValues());
    }

    @Override
    public IForgeRegistry<IEntityAction> getRegistry() {
        return net.minecraftforge.registries.RegistryManager.ACTIVE.getRegistry(new ResourceLocation("vampirism:entityactions"));
    }

    @Override
    public List<IEntityAction> getAllEntityActionsByTierAndClassType(IFaction faction, @Nonnull EntityActionTier tier, EntityClassType classtype) {
        List<IEntityAction> actions = Lists.newArrayList(VampirismRegistries.ENTITYACTIONS.getValues());
        actions.removeIf(action -> action.getFaction() != faction || action.getTier().getId() > tier.getId() || !ArrayUtils.contains(action.getClassTypes(), (classtype)));
        return actions;
    }
}
