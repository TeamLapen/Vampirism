package de.teamlapen.vampirism.common.entity.player.hunter.actions;

import de.teamlapen.factions.common.actions.DefaultAction;
import de.teamlapen.vampirism.api.VampirismTags;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import net.minecraft.tags.TagKey;

/**
 * Basic implementation of IAction<IHunterPlayer>. It is recommended to extend this
 */
public abstract class DefaultHunterAction extends DefaultAction<IHunterPlayer> {

    @Override
    public TagKey<IFaction<?>> factions() {
        return VampirismTags.Factions.IS_HUNTER;
    }
}
