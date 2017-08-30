package de.teamlapen.vampirism.api.entity.player.hunter;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.actions.DefaultAction;
import net.minecraft.util.ResourceLocation;

/**
 * Basic implementation of IAction<IHunterPlayer>. It is recommend to extend this
 */
public abstract class DefaultHunterAction extends DefaultAction<IHunterPlayer> {
    /**
     * @param icons If null Vampirism's default one will be used
     */
    public DefaultHunterAction(ResourceLocation icons) {
        super(VReference.HUNTER_FACTION, icons);
    }



}
