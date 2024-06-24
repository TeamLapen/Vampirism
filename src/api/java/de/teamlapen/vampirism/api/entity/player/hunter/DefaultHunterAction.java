package de.teamlapen.vampirism.api.entity.player.hunter;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismTags;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.actions.DefaultAction;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Basic implementation of IAction<IHunterPlayer>. It is recommended to extend this
 */
public abstract class DefaultHunterAction extends DefaultAction<IHunterPlayer> {

    @NotNull
    @Override
    @Deprecated
    public Optional<IPlayableFaction<?>> getFaction() {
        return Optional.of(VReference.HUNTER_FACTION);
    }

    @Override
    public @NotNull TagKey<IFaction<?>> factions() {
        return VampirismTags.Factions.IS_HUNTER;
    }
}
