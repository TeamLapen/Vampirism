package de.teamlapen.vampirism.api.entity.player.vampire;

import de.teamlapen.vampirism.api.VampirismAPI;
import net.minecraft.util.ResourceLocation;

/**
 * Reference implementation of IVampireAction. It is recommend to extend this
 */
public abstract class DefaultAction implements IVampireAction {

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
     * @param vampire
     * @return
     */
    public boolean canBeUsedBy(IVampirePlayer vampire) {
        return true;
    }

    @Override
    public PERM canUse(IVampirePlayer vampire) {
        if (getMinLevel() == -1)
            return PERM.DISABLED;
        if (vampire.getLevel() < getMinLevel())
            return PERM.LEVEL_TO_LOW;
        return (canBeUsedBy(vampire) ? PERM.ALLOWED : PERM.DISALLOWED);
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
