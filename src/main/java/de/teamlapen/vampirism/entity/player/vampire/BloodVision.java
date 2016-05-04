package de.teamlapen.vampirism.entity.player.vampire;

import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireVision;

/**
 * Blood vision
 */
public class BloodVision implements IVampireVision {
    @Override
    public String getUnlocName() {
        return "text.vampirism.skill.blood_vision";
    }

    @Override
    public void onActivated(IVampirePlayer player) {
        ((VampirePlayer) player).getSpecialAttributes().blood_vision = true;
    }

    @Override
    public void onDeactivated(IVampirePlayer player) {
        ((VampirePlayer) player).getSpecialAttributes().blood_vision = false;
    }

    @Override
    public void onUpdate(IVampirePlayer player) {

    }
}
