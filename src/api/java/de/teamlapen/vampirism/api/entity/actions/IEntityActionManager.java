package de.teamlapen.vampirism.api.entity.actions;

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
}
