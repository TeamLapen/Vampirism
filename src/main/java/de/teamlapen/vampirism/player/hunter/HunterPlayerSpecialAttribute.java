package de.teamlapen.vampirism.player.hunter;

import de.teamlapen.vampirism.player.hunter.actions.DisguiseHunterAction;

/**
 * Stores special attributes that can be activated by skills or actions.
 * One attribute should only be modified by ONE skill/action.
 */
public class HunterPlayerSpecialAttribute {

    private boolean disguised = false;
    private int disguiseTicks = 0;

    private double vampireDistanceRel = 0;

    public void activateDisguise() {
        disguised = true;
        disguiseTicks = 1;
    }

    public float getDisguiseProgress() {
        return disguiseTicks > DisguiseHunterAction.FADE_TICKS ? 1F : disguiseTicks / (float) DisguiseHunterAction.FADE_TICKS;
    }

    public float getVampireNearbyProgress() {
        return (float) vampireDistanceRel;
    }

    public void increaseDisguiseTicks() {
        disguiseTicks++;
    }

    public boolean isDisguised() {
        return disguised;
    }

    public boolean isVampireNearby() {
        return vampireDistanceRel > 0;
    }

    public void nearbyVampire(double distanceRel) {
        vampireDistanceRel = distanceRel;
    }

    public void resetDisguise() {
        disguiseTicks = 0;
        disguised = false;
    }
}
