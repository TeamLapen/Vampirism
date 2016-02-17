package de.teamlapen.vampirism.api.entity.player.vampire;

import net.minecraft.util.ResourceLocation;

/**
 * Reference implementation of IVampireSkill. It is recommend to extend this
 */
public abstract class DefaultSkill implements IVampireSkill {

    private final ResourceLocation icons;

    /**
     * @param icons If null Vampirism's default one will be used
     */
    public DefaultSkill(ResourceLocation icons) {
        this.icons = icons;
    }

    @Override
    public PERM canUse(IVampirePlayer vampire) {
        if (getMinLevel() == -1)
            return PERM.DISABLED;
        if (vampire.getLevel() < getMinLevel())
            return PERM.LEVEL_TO_LOW;
        return (canBeUsedBy(vampire) ? PERM.ALLOWED : PERM.DISALLOWED);
    }

    /**
     * @return The minimum level which is required to use this skill
     */
    public abstract int getMinLevel();


    @Override
    public String toString() {
        return super.toString() + " (" + SkillRegistry.getKeyFromSkill(this) + ")";
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
    public ResourceLocation getIconLoc() {
        return icons;
    }

}
