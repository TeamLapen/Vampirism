package de.teamlapen.vampirism.api.entity.actions;

import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraftforge.registries.IForgeRegistry;
import java.util.List;

public interface IEntityActionManager {

    /**
     * A mutable copied list of all actions registered
     *
     * @return
     */
    List<IEntityAction> getAllEntityActions();

    /**
     * Use net.minecraftforge.registries.RegistryManager.ACTIVE.getRegistry(new ResourceLocation("vampirism:entityactions"))
     *
     * @return
     */
    IForgeRegistry<IEntityAction> getRegistry();

    /**
     * A mutable copied list of all actions registered with corresponding @{@link IFaction}, {@link EntityActionTier} and {@link EntityClassType}
     *
     * @return
     */
    List<IEntityAction> getAllEntityActionsByTierAndClassType(IFaction faction, EntityActionTier tier, EntityClassType classtype);
}
