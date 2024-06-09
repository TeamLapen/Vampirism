package de.teamlapen.vampirism.api.entity.player.vampire;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismTags;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.actions.DefaultAction;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Basic implementation of IAction<IVampirePlayer>. It is recommended to extend this
 */
public abstract class DefaultVampireAction extends DefaultAction<IVampirePlayer> {

    @NotNull
    @Override
    public Optional<IPlayableFaction<?>> getFaction() {
        return Optional.of(VReference.VAMPIRE_FACTION);
    }

    @Override
    public @NotNull TagKey<? extends IFaction<?>> factions() {
        return VampirismTags.Factions.IS_VAMPIRE;
    }
}
