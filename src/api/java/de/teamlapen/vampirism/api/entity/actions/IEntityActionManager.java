package de.teamlapen.vampirism.api.entity.actions;

import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.List;

public interface IEntityActionManager {

    /**
     * @return A copied mutable list of all actions registered
     */
    List<IEntityAction> getAllEntityActions();

    /**
     * @return A copied mutable list of all actions registered with corresponding @{@link IFaction}, {@link EntityActionTier} and {@link EntityClassType}
     */
    List<IEntityAction> getAllEntityActionsByTierAndClassType(IFaction faction, EntityActionTier tier, EntityClassType classtype);

    /**
     * Use net.minecraftforge.registries.RegistryManager.ACTIVE.getRegistry(new ResourceLocation("vampirism:entityactions"))
     *
     * @return
     */
    IForgeRegistry<IEntityAction> getRegistry();
}
