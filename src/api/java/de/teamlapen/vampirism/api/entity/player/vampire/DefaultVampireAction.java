package de.teamlapen.vampirism.api.entity.player.vampire;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.actions.DefaultAction;
import net.minecraft.util.ResourceLocation;

/**
 * Basic implementation of IAction<IVampirePlayer>. It is recommend to extend this
 */
public abstract class DefaultVampireAction extends DefaultAction<IVampirePlayer> {


    /**
     * @param icons If null Vampirism's default one will be used
     */
    public DefaultVampireAction(ResourceLocation icons) {
        super(VReference.VAMPIRE_FACTION, icons);
    }
}
