package de.teamlapen.vampirism.api.entity.player.hunter;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.actions.DefaultAction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionPlayer;
import net.minecraft.util.ResourceLocation;

/**
 * Basic implementation of IAction<IHunterPlayer>. It is recommend to extend this
 */
public abstract class DefaultHunterAction extends DefaultAction<IHunterPlayer> {
    /**
     * @param icons If null Vampirism's default one will be used
     */
    public DefaultHunterAction(ResourceLocation icons) {
        super(icons);
    }

    @Override
    public IPlayableFaction<? extends IActionPlayer> getFaction() {
        return VReference.HUNTER_FACTION;
    }


}
