package de.teamlapen.vampirism.api.entity.actions;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraftforge.registries.IForgeRegistry;
import java.util.List;

public interface IEntityActionManager {

    /**
     * A mutable copied list of all actions registered for this faction
     *
     * @param faction
     * @return
     */
    List<IEntityAction> getEntityActionForFaction(IFaction faction);

    /**
     * Use net.minecraftforge.registries.RegistryManager.ACTIVE.getRegistry(new ResourceLocation("vampirism:entityactions"))
     *
     * @return
     */
    IForgeRegistry<IEntityAction> getRegistry();
}
