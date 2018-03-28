package de.teamlapen.vampirism.api.entity.player.actions;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.List;

/**
 * Helper for action registry related things.
 */
public interface IActionManager {

    /**
     * A mutable copied list of all actions registered for this faction
     *
     * @param faction
     * @return
     */
    List<IAction> getActionsForFaction(IPlayableFaction faction);

    /**
     * Use net.minecraftforge.registries.RegistryManager.ACTIVE.getRegistry(new ResourceLocation("vampirism:actions"))
     *
     * @return
     */
    IForgeRegistry<IAction> getRegistry();
}
