package de.teamlapen.vampirism.common.entity.player.vampire;

import de.teamlapen.factions.common.actions.DefaultAction;
import de.teamlapen.vampirism.api.VampirismTags;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
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
