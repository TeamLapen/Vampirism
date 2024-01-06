package de.teamlapen.vampirism.api.entity.actions;

import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.core.Registry;

import java.util.List;

public interface IEntityActionManager {

    /**
     * @return A copied mutable list of all actions registered
     */
    List<IEntityAction> getAllEntityActions();

    /**
     * @return A copied mutable list of all actions registered with corresponding @{@link IFaction}, {@link EntityActionTier} and {@link EntityClassType}
     */
    List<IEntityAction> getAllEntityActionsByTierAndClassType(IFaction<?> faction, EntityActionTier tier, EntityClassType classType);

    /**
     * or use {@code net.minecraftforge.registries.RegistryManager.ACTIVE.getRegistry(new ResourceLocation("vampirism:entityactions"))}
     */
    Registry<IEntityAction> getRegistry();
}
