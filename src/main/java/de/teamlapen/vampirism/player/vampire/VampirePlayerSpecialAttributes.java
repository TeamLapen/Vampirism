package de.teamlapen.vampirism.player.vampire;

import de.teamlapen.vampirism.api.entity.factions.IFaction;

/**
 * Stores special attributes that can be activated by skills or actions.
 * One attribute should only be modified by ONE skill/action.
 */
public class VampirePlayerSpecialAttributes {
    public boolean disguised = false;
    public IFaction disguisedAs = null;
    public boolean bat = false;
    public boolean blood_vision = false;
    public boolean half_invulnerable = false;
    public boolean increasedVampireFogDistance = true;
    public boolean waterResistance;
    public boolean advanced_biter = false;
    public boolean blood_vision_garlic;
    private int jump_boost = 0;

    public int getJumpBoost() {
        return jump_boost;
    }

    public void setJumpBoost(int jump_boost) {
        this.jump_boost = (jump_boost >= 0 && jump_boost < 10) ? jump_boost : 0;
    }
}
