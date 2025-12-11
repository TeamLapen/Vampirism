package de.teamlapen.vampirism.common.entity.player.vampire.skills;

import de.teamlapen.vampirism.common.config.ModConfig;

public class VampirePlayerSkillProperties {
    public boolean bat = false;
    public boolean blood_vision = false;
    public boolean half_invulnerable = false;
    public boolean waterResistance;
    public boolean advanced_biter = false;
    public boolean blood_vision_garlic;
    public boolean isDBNO;
    public boolean darkStalker;

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
        return isDBNO || (bat && !ModConfig.BALANCE.vaBatAllowInteraction.get());
    }
}
