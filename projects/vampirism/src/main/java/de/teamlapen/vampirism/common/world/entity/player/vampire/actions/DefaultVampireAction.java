package de.teamlapen.vampirism.common.world.entity.player.vampire.actions;

import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.common.factions.actions.DefaultAction;
import de.teamlapen.vampirism.api.VampirismTags;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import net.minecraft.tags.TagKey;

/**
 * Basic implementation of IAction<IVampirePlayer>. It is recommended to extend this
 */
public abstract class DefaultVampireAction extends DefaultAction<IVampirePlayer> {

    @Override
    public TagKey<? extends IFaction<?>> factions() {
        return VampirismTags.Factions.IS_VAMPIRE;
    }
}
