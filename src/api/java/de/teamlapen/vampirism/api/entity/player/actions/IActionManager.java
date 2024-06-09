package de.teamlapen.vampirism.api.entity.player.actions;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;

import java.util.List;

/**
 * Helper for action registry related things.
 */
public interface IActionManager {

    /**
     * A copied mutable list of all actions registered for this faction
     */
    @Deprecated
    <T extends IFactionPlayer<T>> List<IAction<T>> getActionsForFaction(IPlayableFaction<T> faction);

    List<Holder<IAction<?>>> getActionsForFaction(Holder<? extends IPlayableFaction<?>> faction);

    /**
     * or use {@code net.minecraftforge.registries.RegistryManager.ACTIVE.getRegistry(new ResourceLocation("vampirism:actions"))}
     */
    @Deprecated
    Registry<IAction<?>> getRegistry();
}
