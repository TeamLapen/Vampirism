package de.teamlapen.vampirism.api.entity.player.actions;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.util.ResourceLocation;

/**
 * Default implementation for an action
 */
public abstract class DefaultAction<T extends IFactionPlayer> implements IAction<T> {
    private final ResourceLocation icons;

    /**
     * @param icons If null Vampirism's default one will be used
     */
    public DefaultAction(ResourceLocation icons) {
        this.icons = icons;
    }

    /**
     * Can be overriden to check addidional requirements
     *
     * @return
     */
    public boolean canBeUsedBy(T player) {
        return true;
    }

    @Override
    public IAction.PERM canUse(T player) {
        if (!isEnabled())
            return IAction.PERM.DISABLED;
        return (canBeUsedBy(player) ? IAction.PERM.ALLOWED : IAction.PERM.DISALLOWED);
    }


    @Override
    public ResourceLocation getIconLoc() {
        return icons;
    }

    /**
     *
     * @return Should return false if deactivated in configs
     */
    public abstract boolean isEnabled();

    @Override
    public String toString() {
        return VampirismAPI.actionRegistry().getKeyFromAction(this) + " (" + this.getClass().getSimpleName() + ")";
    }
}
