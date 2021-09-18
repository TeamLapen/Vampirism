package de.teamlapen.vampirism.api.entity.player.actions;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.List;

/**
 * Helper for action registry related things.
 */
public interface IActionManager {

    /**
     * A copied mutable list of all actions registered for this faction
     *
     */
    List<IAction> getActionsForFaction(IPlayableFaction faction);

    /**
     * or use {@code net.minecraftforge.registries.RegistryManager.ACTIVE.getRegistry(new ResourceLocation("vampirism:actions"))}
     */
    IForgeRegistry<IAction> getRegistry();
}
