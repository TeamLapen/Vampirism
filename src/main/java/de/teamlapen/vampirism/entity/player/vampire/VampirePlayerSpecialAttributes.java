package de.teamlapen.vampirism.entity.player.vampire;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.config.VampirismConfig;
import org.jetbrains.annotations.Nullable;

/**
 * Stores special attributes that can be activated by skills or actions.
 * One attribute should only be modified by ONE skill/action.
 */
public class VampirePlayerSpecialAttributes {
    public boolean disguised = false;
    public boolean bat = false;
    public boolean blood_vision = false;
    public boolean half_invulnerable = false;
    public boolean waterResistance;
    public boolean advanced_biter = false;
    public boolean blood_vision_garlic;
    public int eyeType;
    public int fangType;
    public boolean glowingEyes;
    public boolean isDBNO;

    /**
     * This cancels the render player event.
     * The invisibility skill additionally sets the entity invisible so armor, shadow and more are not rendered
     */
    public boolean invisible = false;
    private int jump_boost = 0;

    public int getJumpBoost() {
        return jump_boost;
    }

    public void setJumpBoost(int jump_boost) {
        this.jump_boost = (jump_boost >= 0 && jump_boost < 10) ? jump_boost : 0;
    }

    public boolean isCannotInteract() {
        return isDBNO || (bat && !VampirismConfig.BALANCE.vaBatAllowInteraction.get());
    }
}
