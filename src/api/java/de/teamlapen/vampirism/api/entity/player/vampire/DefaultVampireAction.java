package de.teamlapen.vampirism.api.entity.player.vampire;

import de.teamlapen.vampirism.api.VampirismTags;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.actions.DefaultAction;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

/**
 * Basic implementation of IAction<IVampirePlayer>. It is recommended to extend this
 */
public abstract class DefaultVampireAction extends DefaultAction<IVampirePlayer> {

    @Override
    public @NotNull TagKey<? extends IFaction<?>> factions() {
        return VampirismTags.Factions.IS_VAMPIRE;
    }
}
