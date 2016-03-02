package de.teamlapen.vampirism.api.entity.player;

import de.teamlapen.vampirism.api.VampirismAPI;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Max on 02.03.2016.
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
    public boolean canBeUsedBy(T vampire) {
        return true;
    }

    @Override
    public IAction.PERM canUse(T vampire) {
        if (getMinLevel() == -1)
            return IAction.PERM.DISABLED;
        if (vampire.getLevel() < getMinLevel())
            return IAction.PERM.LEVEL_TO_LOW;
        return (canBeUsedBy(vampire) ? IAction.PERM.ALLOWED : IAction.PERM.DISALLOWED);
    }


    @Override
    public ResourceLocation getIconLoc() {
        return icons;
    }

    /**
     * @return The minimum level which is required to use this skill
     */
    public abstract int getMinLevel();

    @Override
    public String toString() {
        return super.toString() + " (" + VampirismAPI.actionRegistry().getKeyFromAction(this) + ")";
    }
}
