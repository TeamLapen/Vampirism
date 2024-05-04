package de.teamlapen.vampirism.api.entity.player.hunter;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.actions.DefaultAction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Basic implementation of IAction<IHunterPlayer>. It is recommended to extend this
 */
public abstract class DefaultHunterAction extends DefaultAction<IHunterPlayer> {
    @NotNull
    @Override
    public Optional<IPlayableFaction<?>> getFaction() {
        return Optional.of(VReference.HUNTER_FACTION);
    }
}
